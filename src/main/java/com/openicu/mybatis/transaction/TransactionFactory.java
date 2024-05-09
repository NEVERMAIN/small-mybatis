package com.openicu.mybatis.transaction;

import com.openicu.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description: 事务工厂
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public interface TransactionFactory {

    /**
     * 根据 Connection 创建 Transaction
     * @param connection
     * @return
     */
    Transaction newTransaction(Connection connection) throws SQLException;

    /**
     * 根据数据源和事务隔离级别创建 Transaction
     * @param dataSource
     * @param level
     * @param autoCommit
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level,boolean autoCommit);

}
