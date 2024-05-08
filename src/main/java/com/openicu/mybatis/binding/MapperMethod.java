package com.openicu.mybatis.binding;

import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.mapping.SqlCommandType;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * @description: 映射器方法
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class MapperMethod {

    private final SqlCommand command;

    public MapperMethod(Class<?> mapperInterface,Method method,Configuration configuration){
        this.command = new SqlCommand(configuration,mapperInterface,method);
    }

    public Object execute(SqlSession sqlSession,Object[] args){
        Object result = null;
        switch (command.getType()){
            case INSERT:
                break;
            case UPDATE:
                break;
            case DELETE:
                break;
            case SELECT:
                result = sqlSession.selectOne(command.getName(),args);
                break;
            default:
                throw new RuntimeException("Unknown execution method for:"+command.getName());
        }
        return result;
    }

    /**
     * SQL 指令
     */
    public static class SqlCommand{

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            MappedStatement ms = configuration.getMappedStatement(statementName);
            this.name = ms.getId();
            this.type = ms.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }

}
