package com.openicu.mybatis.datasource.pooled;

import com.openicu.mybatis.datasource.unpooled.UnpooledDataSource;


import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;

/**
 * @description: 有连接池的数据源
 * @author: 云奇迹
 * @date: 2024/5/10
 */
public class PooledDataSource implements DataSource {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PooledDataSource.class);

    // 池状态
    private final PoolState state = new PoolState(this);

    // 未池化连接
    private final UnpooledDataSource dataSource;

    // 活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 空闲连接数
    protected int poolMaximumIdleConnections = 5;
    // 在被强制返回之前,池中连接被检查的时间
    protected int poolMaximumCheckoutTime = 20000;
    // 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间 为了避免连接池没有配置时静默失败)。
    protected int poolTimeToWait = 20000;
    // 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,这会引起许多数据库驱动连接由一 个错误信息而导致失败
    protected String poolPingQuery = "NO PING QUERY SET";
    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置 poolPingQuery 多少次被用一次
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.dataSource = new UnpooledDataSource();
    }

    /**
     * 将一个连接推回连接池。
     * 此方法会首先尝试将连接标记为无效，如果连接有效且当前空闲连接数小于最大空闲连接数，则该连接会被加入到空闲连接列表中。
     * 如果连接无效或者空闲连接数已满，连接将被关闭并不再可用。
     *
     * @param connection 需要被推回连接池的连接对象。
     * @throws SQLException 如果操作数据库连接时发生错误。
     */
    protected void pushConnection(PooledConnection connection) throws SQLException {
        synchronized (state) { // 同步代码块，确保对连接池状态的操作是线程安全的
            // 从活动连接列表中移除当前连接
            state.activeConnections.remove(connection);

            // 判断连接是否有效
            if (connection.isValid()) {
                // 如果空闲链接小于设定数量,也就是太少时
                if (state.idleConnections.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    // 对连接进行回收处理，准备重新放入池中供他人使用
                    state.accumulatedCheckoutTime += connection.getCheckoutTimestamp();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        // 若非自动提交，回滚事务
                        connection.getRealConnection().rollback();
                    }
                    // 实例化一个新的DB连接,加入到 idle 列表
                    PooledConnection newConnection = new PooledConnection(connection.getRealConnection(), this);
                    state.idleConnections.add(newConnection);
                    newConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    newConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    connection.invalidate(); // 标记原连接为无效，防止重复使用
                    logger.info("Returned connection " + newConnection.getRealConnection() + " to pool. ");

                    // 通知其他等待的线程，可以尝试获取连接
                    state.notifyAll();
                } else {
                    // 连接不满足重新放入池的条件，直接关闭
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if (!connection.getRealConnection().getAutoCommit()) {
                        // 若非自动提交，回滚事务
                        connection.getRealConnection().rollback();
                    }
                    // 关闭连接
                    connection.getRealConnection().close();
                    logger.info("Closed connection " + connection.getRealConnection() + ".");
                    // 标记连接为无效
                    connection.invalidate();
                }
            } else {
                // 连接已经无效，直接记录日志并丢弃
                logger.info("A bad connection (" + connection.getRealConnection() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
            }
        }
    }

    /**
     * 从连接池中获取一个连接。
     * 如果池中存在空闲连接，则返回第一个空闲连接。
     * 如果没有空闲连接，并且活跃连接数未达到上限，则创建一个新的连接。
     * 如果活跃连接已满，并且最老的活跃连接已超出最大检查时间，则回收该连接并创建一个新的。
     * 如果无法获取连接，且等待时间未达到上限，则等待。
     * 如果等待后仍无法获取连接，或者获取到的连接无效，则抛出SQLException。
     *
     * @param username 数据源认证的用户名。
     * @param password 数据源认证的密码。
     * @return 从连接池中获取的PooledConnection对象。
     * @throws SQLException 如果无法获取有效的数据库连接。
     */
    private PooledConnection popConnection(String username, String password) throws SQLException {

        // 是否等待的标记
        boolean countedWait = false;
        PooledConnection conn = null;
        // 开始时间
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (state) {
                // 如果有空闲连接,返回第一个
                if (!state.idleConnections.isEmpty()) {
                    conn = state.idleConnections.remove(0);
                    logger.info("Checked out connection " + conn.getRealHashCode() + " from pool.");
                } else {
                    // 如果没有空闲连接,创建新的连接
                    // 1.活跃连接数不足,没达到上限
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                        logger.info("Create connection " + conn.getRealHashCode());
                    } else {
                        // 活跃连接数已经满了
                        // 1.获取活跃连接列表的第一个,也就是最老的一个连接
                        PooledConnection oldestPooledConnection = state.activeConnections.get(0);
                        // 2.获取连接花费的时间
                        long longestCheckoutTime = oldestPooledConnection.getCheckoutTime();
                        // 3. 如果 checkout 时间过长,则这个连接就标记为过期
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            state.accumulatedCheckoutTime += longestCheckoutTime;
                            // 从集合中删除超时的连接
                            state.activeConnections.remove(oldestPooledConnection);
                            if (!oldestPooledConnection.getRealConnection().getAutoCommit()) {
                                oldestPooledConnection.getRealConnection().rollback();
                            }
                            // 删除最老的连接,然后重新实例化一个新的连接
                            conn = new PooledConnection(oldestPooledConnection.getProxyConnection(), this);
                            oldestPooledConnection.invalidate();
                            logger.info("Claimed overdue connection " + conn.getRealHashCode() + ".");
                        } else {
                            try {
                                // 如果 checkout 时间不够长,则需要等待
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                logger.info("Waiting ad long as " + poolTimeToWait + " milliseconds for connection.");
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                // 被唤醒,退出循环获取连接
                                break;
                            }
                        }
                    }
                }

                // 获取到连接
                if (conn != null) {
                    if (conn.isValid()) {
                        // 回滚之前的事务
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        // 记录 checkout 时间
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        state.activeConnections.add(conn);
                        state.requestCount++;
                        // 请求花费的时间
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        logger.info("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
                        // 如果没有拿到,统计信息:失败连接 + 1
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        // 让超时的连接失效,后面继续获取
                        conn = null;
                        // 失败次数较多,抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            logger.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (conn == null) {
            logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;

    }

    public void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            // 关闭活跃连接
            for (int i = state.activeConnections.size(); i > 0; i--) {

                try {
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {

                    }
                    realConn.close();
                } catch (Exception ignore) {

                }
            }

            // 关闭空闲连接
            for (int i = state.idleConnections.size(); i > 0; i--) {

                try {
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                } catch (Exception ignore) {

                }
            }
            logger.info("PooledDataSource forcefully closed/removed all connections.");
        }

    }

    protected boolean pingConnection(PooledConnection conn) {

        boolean result = true;

        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
            result = false;
        }

        if (result) {
            if (poolPingEnabled) {
                if (poolPingConnectionsNotUsedFor >= 0 && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                    try {
                        logger.info("Testing connection " + conn.getRealHashCode() + " ...");
                        Connection realConn = conn.getRealConnection();
                        Statement statement = realConn.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (realConn.getAutoCommit()) {
                            realConn.rollback();
                        }
                        result = true;
                        logger.info("Connection " + conn.getRealHashCode() + " is GOOD!");
                    } catch (Exception e) {
                        logger.info("Execution of ping query '" + poolPingQuery + "' failed: " + e.getMessage());
                        try {
                            conn.getRealConnection().close();
                        } catch (SQLException ex) {

                        }
                        result = false;
                        logger.info("Connection " + conn.getRealHashCode() + " is BAD: " + e.getMessage());
                    }
                }
            }
        }

        return result;

    }

    public static Connection unwrapConnection(Connection conn) {
        if(Proxy.isProxyClass(conn.getClass())){
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if(handler instanceof PooledConnection){
                return ((PooledConnection)handler).getRealConnection();
            }
        }

        return conn;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }


    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(),dataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }


    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
    }

    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
    }

    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
    }

    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
    }

    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
    }

    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
    }

    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
        this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
    }

    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }


}
