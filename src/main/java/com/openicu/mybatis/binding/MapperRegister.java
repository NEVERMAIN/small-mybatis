package com.openicu.mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @description: 映射器注册机
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public class MapperRegister {

    private Configuration configuration;

    public MapperRegister(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 将已添加的映射器代理加入到 HashMap
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {

        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type:" + type + " is not know to the MapperRegister");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause:" + e);
        }
    }


    public void addMapper(Class<?> type) {
        // Mapper 必须是接口才会注册
        if (type.isInterface()) {
            if (hasMapper(type)) {
                // 如果重复添加,报错
                throw new RuntimeException("Type" + type + " is already know to the MapperRegistry");
            }
            // 注册映射器代理工厂
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }

    public <T> boolean hasMapper(Class<?> type) {
        return knownMappers.containsKey(type);
    }

    public void addMapper(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }


}
