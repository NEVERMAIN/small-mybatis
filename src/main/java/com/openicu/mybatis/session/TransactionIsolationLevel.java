package com.openicu.mybatis.session;

import com.openicu.mybatis.transaction.Transaction;

import java.sql.Connection;

/**
 * @description: 事务隔离级别
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public enum TransactionIsolationLevel {

    // JDBC 支持的 5 个事务级别
    NONE(Connection.TRANSACTION_NONE),
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    TransactionIsolationLevel(int level){
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }

}
