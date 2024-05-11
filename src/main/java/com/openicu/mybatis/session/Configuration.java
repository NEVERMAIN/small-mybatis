package com.openicu.mybatis.session;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.datasource.druid.DruidDataSourceFactory;
import com.openicu.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.openicu.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.openicu.mybatis.mapping.Environment;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.openicu.mybatis.type.TypeAliasRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class Configuration {

    /**
     * 环境
     */
    protected Environment environment;

    /**
     * 映射注册机
     */
    protected MapperRegister mapperRegister = new MapperRegister(this);

    /**
     * 映射的语句,存在 Map 里
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

    /**
     * 类型别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("DRUID", DruidDataSourceFactory.class);

        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
    }

    public <T> void addMappers(String packageName){
        mapperRegister.addMapper(packageName);
    }

    public <T> void addMapper(Class<T> type){
        mapperRegister.addMapper(type);
    }

    public <T> T getMapper(Class<T> type,SqlSession sqlSession){
        return mapperRegister.getMapper(type,sqlSession);
    }

    public boolean hasMapper(Class<?> type){
        return mapperRegister.hasMapper(type);
    }

    public void addMapperStatement(MappedStatement mappedStatement){
        mappedStatements.put(mappedStatement.getId(),mappedStatement);
    }

    public MappedStatement getMappedStatement(String id){
        return mappedStatements.get(id);
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment(){
        return environment;
    }
}
