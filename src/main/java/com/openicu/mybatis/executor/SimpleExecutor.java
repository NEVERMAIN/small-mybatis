package com.openicu.mybatis.executor;

import com.openicu.mybatis.executor.statement.StatementHandler;
import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.ResultHandler;
import com.openicu.mybatis.transaction.Transaction;

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
public class SimpleExecutor extends BaseExecutor{

    public SimpleExecutor(Configuration configuration, Transaction transaction){
        super(configuration,transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            Statement statement = handler.prepare(connection);
            handler.parameterize(statement);
            return handler.query(statement,resultHandler);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
