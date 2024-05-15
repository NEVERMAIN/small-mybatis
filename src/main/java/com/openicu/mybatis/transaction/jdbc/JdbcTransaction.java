package com.openicu.mybatis.transaction.jdbc;

import com.openicu.mybatis.session.TransactionIsolationLevel;
import com.openicu.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: JDBC 事务,直接利用 JDBC 的 commit、rollback。依赖数据源获取的链接来管理事务范围
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public class JdbcTransaction  implements Transaction {

    protected Connection connection;

    protected DataSource dataSource;

    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;

    protected boolean autoCommit;

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setTransactionIsolation(level.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if(connection != null && !autoCommit){
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if(connection != null && !connection.getAutoCommit()){
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if(connection != null && !connection.getAutoCommit()){
            connection.close();
        }
    }
}
