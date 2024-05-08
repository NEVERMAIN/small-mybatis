package com.openicu.mybatis.session.defaults;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;

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

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T)("你被代理了"+statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedStatement = configuration.getMappedStatement(statement);
        return (T)("你被代理了！"+"\n方法: "+statement + "\n入参: :"+parameter + "\n待执行的SQL: "+mappedStatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
       return configuration.getMapper(type,this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
