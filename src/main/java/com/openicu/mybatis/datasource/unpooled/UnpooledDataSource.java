package com.openicu.mybatis.datasource.unpooled;


import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @description: 无池化数据源实现
 * @author: 云奇迹
 * @date: 2024/5/10
 */
public class UnpooledDataSource implements DataSource {

    /**
     * 类加载器
     */
    private ClassLoader driverClassLoader;
    /**
     * 驱动配置，也可以扩展属性信息 driver.encoding=UTF8
     */
    private Properties driverProperties;
    /**
     * 驱动注册器
     */
    private static Map<String, Driver> registeredDrivers = new ConcurrentHashMap<>();
    /**
     * 驱动
     */
    private String driver;
    /**
     * DB 连接地址
     */
    private String url;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 是否自动提交
     */
    private Boolean autoCommit;

    private Integer defaultTransactionIsolationLevel;

    static {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            registeredDrivers.put(driver.getClass().getName(), driver);
        }
    }

    /**
     * 驱动代理
     */
    private static class DriverProxy implements Driver {

        private Driver driver;

        public DriverProxy(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return this.driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return this.driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return this.driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return this.driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return this.driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return this.driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }

    /**
     * 初始化驱动
     */
    private synchronized void initializerDriver() throws SQLException {
        if (!registeredDrivers.containsKey(driver)) {
            try {
                Class<?> driverType = Class.forName(driver, true, driverClassLoader);
                Driver driverInstance = (Driver) driverType.newInstance();
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                registeredDrivers.put(driver,driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource, Cause: " + e);
            }
        }
    }

    private Connection doGetConnection(String  username,String password) throws SQLException {
        Properties props = new Properties();
        if(driverProperties != null){
            props.putAll(driverProperties);
        }
        if(username != null){
            props.put("user",username);
        }
        if(password != null){
            props.put("password",password);
        }
        return doGetConnection(props);
    }

    /**
     * 获取数据库连接的方法。
     * 此方法会初始化驱动，根据给定的属性获取数据库连接，并根据需要设置自动提交和事务隔离级别。
     *
     * @param properties 连接数据库所需的属性，包含用户名、密码等。
     * @return 返回建立的数据库连接。
     * @throws SQLException 如果在获取连接或设置连接属性时发生错误，则抛出SQLException。
     */
    private Connection doGetConnection(Properties properties) throws SQLException {
        initializerDriver(); // 初始化数据库驱动
        Connection connection = DriverManager.getConnection(url, properties); // 获取数据库连接

        // 如果autoCommit设置不为空，并且与连接的当前自动提交状态不同，则更改连接的自动提交状态
        if(autoCommit != null && autoCommit != connection.getAutoCommit()){
            connection.setAutoCommit(autoCommit);
        }

        // 如果默认事务隔离级别不为空，则设置连接的事务隔离级别
        if(defaultTransactionIsolationLevel != null){
            connection.setTransactionIsolation(defaultTransactionIsolationLevel);
        }

        return connection; // 返回建立的数据库连接
    }


    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username,password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username,password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + "is not a wrapper. ");
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

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    public Integer getDefaultTransactionIsolationLevel() {
        return defaultTransactionIsolationLevel;
    }

    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public static Map<String, Driver> getRegisteredDriver() {
        return registeredDrivers;
    }

    public static void setRegisteredDriver(Map<String, Driver> registeredDriver) {
        UnpooledDataSource.registeredDrivers = registeredDriver;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
