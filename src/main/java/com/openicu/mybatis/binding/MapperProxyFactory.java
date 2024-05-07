package com.openicu.mybatis.binding;

import com.openicu.mybatis.session.SqlSession;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @description: 代理工厂
 * @author: 云奇迹
 * @date: 2024/5/4
 */
public class MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession){
        final MapperProxy<T> mapperProxy = (MapperProxy<T>) new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),new Class[]{mapperInterface},mapperProxy);
    }
}
