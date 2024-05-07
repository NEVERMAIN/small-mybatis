package com.openicu.mybatis.session.defaults;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.session.SqlSession;

/**
 * @description: 默认 SqlSession 实现类
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public class DefaultSqlSession implements SqlSession {

    /**
     * 映射器注册机
     */
    private MapperRegister mapperRegister;

    public DefaultSqlSession(MapperRegister mapperRegister) {
        this.mapperRegister = mapperRegister;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T)("你被代理了"+statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        return (T)("你被代理了！"+"方法"+statement + " 入参:"+parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegister.getMapper(type,this);
    }
}
