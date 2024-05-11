package com.openicu.mybatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: 池化代理的链接
 * @author: 云奇迹
 * @date: 2024/5/10
 */
public class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] IFACES = new Class<?>[]{Connection.class};

    private int hashCode = 0;
    private PooledDataSource dataSource;

    // 真实的连接
    private Connection realConnection;
    // 代理的连接
    private Connection proxyConnection;

    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private int connectionTypeCode;
    private boolean valid;

    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
    }

    /**
     * 委派调用方法。此方法用于拦截对代理对象的方法调用，并将调用转发给实际的连接对象。
     * 如果调用的是关闭连接的方法，则将连接返回给连接池。
     *
     * @param proxy 代理对象，即被代理的目标对象。
     * @param method 被调用的方法。
     * @param args 方法调用时传入的参数数组。
     * @return 方法的返回值。如果调用的是关闭连接方法，则返回null，否则返回方法的实际返回值。
     * @throws Throwable 如果方法调用过程中发生异常，则抛出。
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();

        // 判断是否调用了关闭连接的方法
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            dataSource.pushConnection(this); // 将连接返回给连接池
            return null;
        } else {
            // 检查连接是否合法，排除toString方法的调用
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            // 将方法调用委派给真实的连接对象执行
            return method.invoke(realConnection, args);
        }
    }



    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid");
        }
    }

    public void invalidate() {
        valid = false;
    }

    public boolean isValid() {
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PooledConnection) {
            return realConnection.hashCode() == (((PooledConnection) obj).realConnection.hashCode());
        } else if (obj instanceof Connection) {
            return hashCode == obj.hashCode();
        } else {
            return false;
        }
    }
}
