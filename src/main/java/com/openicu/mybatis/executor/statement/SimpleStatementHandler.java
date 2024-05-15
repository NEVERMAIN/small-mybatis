package com.openicu.mybatis.executor.statement;

import com.openicu.mybatis.executor.Executor;
import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/14
 */
public class SimpleStatementHandler extends BaseStatementHandler{

    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameterObject, resultHandler, boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.handleResultSets(statement);
    }
}
