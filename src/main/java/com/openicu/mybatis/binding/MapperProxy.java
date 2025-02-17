package com.openicu.mybatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @description: 映射器代理类
 * @author: 云奇迹
 * @date: 2024/5/4
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;

    private Map<String,String> sqlSession;

    private final Class<T> mapperInterface;

    public MapperProxy( Map<String, String> sqlSession,Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }else{
            return "你的被代理了!"+sqlSession.get(mapperInterface.getName()+"."+method.getName());
        }
    }
}
