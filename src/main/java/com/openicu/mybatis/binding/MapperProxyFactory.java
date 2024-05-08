package com.openicu.mybatis.binding;

import com.openicu.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 代理工厂
 * @author: 云奇迹
 * @date: 2024/5/4
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    private Map<Method,MapperMethod> methodCache = new ConcurrentHashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Map<Method,MapperMethod> getMethodCache(){
        return methodCache;
    }

    public T newInstance(SqlSession sqlSession){
        final MapperProxy<T> mapperProxy = (MapperProxy<T>) new MapperProxy<>(sqlSession, mapperInterface,methodCache);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),new Class[]{mapperInterface},mapperProxy);
    }
}
