package com.openicu.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public interface Transaction {

    Connection getConnection() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;

    void close() throws SQLException;

}
