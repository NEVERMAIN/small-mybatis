package com.openicu.mybatis.mapping;

import com.openicu.mybatis.session.Configuration;

import java.util.Map;

/**
 * @description: 映射语句类
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class MappedStatement {

    private Configuration configuration;
    /**
     * 映射语句 id
     */
    private String id;
    /**
     * SQL 语句的类型
     */
    private SqlCommandType sqlCommandType;
    /**
     * 要执行的 SQL 语句
     */
    private BoundSql boundSql;

    MappedStatement(){

    }

    /**
     * 建造者模式
     */
    public static class Builder{

        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration,String id,SqlCommandType sqlCommandType,BoundSql boundSql){
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.boundSql = boundSql;
        }

        public MappedStatement build(){
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public BoundSql getBoundSql() {
        return boundSql;
    }
}
