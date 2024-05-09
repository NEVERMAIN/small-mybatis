package com.openicu.mybatis.session;

import com.openicu.mybatis.builder.xml.XMLConfigBuilder;
import com.openicu.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @description: 构建 SqlSessionFactory 的工厂
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader){
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return  build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration config){
        return new DefaultSqlSessionFactory(config);
    }

}
