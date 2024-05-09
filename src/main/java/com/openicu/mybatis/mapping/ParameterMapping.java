package com.openicu.mybatis.mapping;

import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.type.JdbcType;

/**
 * @description: 参数映射 #{property,javaType=int,jdbcType=NUMERIC}
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public class ParameterMapping {

    private Configuration configuration;

    private String property;
    // javaType=int
    private Class<?> javaType = Object.class;
    // jdbcType=NUMERIC
    private JdbcType jdbcType;

    private ParameterMapping() {
    }

    public static class Builder {

        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
        }

        public Builder javaType(Class<?> javaType) {
            parameterMapping.javaType = javaType;
            return this;
        }

        public Builder jdbcType(JdbcType jdbcType) {
            parameterMapping.jdbcType = jdbcType;
            return this;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getProperty() {
        return property;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }
}
