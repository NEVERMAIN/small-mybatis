package com.openicu.mybatis.session.defaults;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.session.SqlSession;
import com.openicu.mybatis.session.SqlSessionFactory;

/**
 * @description: 默认的 SqlSessionFactory
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private MapperRegister mapperRegister;

    public DefaultSqlSessionFactory(MapperRegister mapperRegister) {
        this.mapperRegister = mapperRegister;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegister);
    }
}
