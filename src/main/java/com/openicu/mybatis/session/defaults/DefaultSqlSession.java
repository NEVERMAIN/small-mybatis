package com.openicu.mybatis.session.defaults;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.executor.Executor;
import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.Environment;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @description: 默认 SqlSession 实现类
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public class DefaultSqlSession implements SqlSession {

    /**
     * 配置类
     */
    private Configuration configuration;

    private Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return this.selectOne(statement, null);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        List<T> list = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
        return list.get(0);
    }


    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
