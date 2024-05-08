package com.openicu.mybatis.session;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class Configuration {

    /**
     * 映射注册机
     */
    protected MapperRegister mapperRegister = new MapperRegister(this);

    /**
     * 映射的语句,存在 Map 里
     */
    protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

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



}
