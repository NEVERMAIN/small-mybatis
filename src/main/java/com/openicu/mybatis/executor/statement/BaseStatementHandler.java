package com.openicu.mybatis.executor.statement;

import com.openicu.mybatis.executor.Executor;
import com.openicu.mybatis.executor.resutset.ResultSetHandler;
import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @description: 语句处理器抽象基类
 * @author: 云奇迹
 * @date: 2024/5/14
 */
public abstract class BaseStatementHandler implements StatementHandler {

    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;
    protected final Object parameterObject;
    protected final ResultSetHandler resultSetHandler;
    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, ResultHandler resultHandler,BoundSql boundSql){
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.boundSql = boundSql;
        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement = null;
        try{
            // 1.实例化 Statement
            statement = instantiateStatement(connection);
            // 2.参数设置
            statement.setQueryTimeout(350);
            statement.setFetchSize(1000);
            // 3.返回结果
            return statement;
        }catch (Exception e){
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;
}
