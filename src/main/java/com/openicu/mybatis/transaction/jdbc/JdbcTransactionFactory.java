package com.openicu.mybatis.transaction.jdbc;

import com.openicu.mybatis.session.TransactionIsolationLevel;
import com.openicu.mybatis.transaction.Transaction;
import com.openicu.mybatis.transaction.TransactionFactory;
import com.sun.org.apache.bcel.internal.generic.ARETURN;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: JDBC 事务工厂
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public class JdbcTransactionFactory implements TransactionFactory {

    @Override
    public Transaction newTransaction(Connection connection) throws SQLException {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource,level,autoCommit);
    }
}
