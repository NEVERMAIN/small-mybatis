package com.openicu.mybatis.executor.resutset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @description: 结果集处理器
 * @author: 云奇迹
 * @date: 2024/5/14
 */
public interface ResultSetHandler {

    <E> List<E> handleResultSets(Statement statement) throws SQLException;
}
