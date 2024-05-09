package com.openicu.mybatis.builder;

import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.type.TypeAliasRegistry;

/**
 * @description: 构建器的基类，建造者模式
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class BaseBuilder {

    protected final Configuration configuration;

    protected final TypeAliasRegistry typeAliasRegistry;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration(){
        return configuration;
    }
}
