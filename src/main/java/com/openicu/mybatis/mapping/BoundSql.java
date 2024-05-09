package com.openicu.mybatis.mapping;

import java.util.Map;

/**
 * @description: 绑定的 SQL,是从 SqlSource 而来，将动态内容都处理完成得到的 SQL 语句字符串，其中包括?,还有绑定的参数
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public class BoundSql {

    private String sql;
    private Map<Integer,String> parameterMapping;
    private String parameterType;
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> parameterMapping, String parameterType, String resultType) {
        this.sql = sql;
        this.parameterMapping = parameterMapping;
        this.parameterType = parameterType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public String getResultType() {
        return resultType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public Map<Integer, String> getParameterMapping() {
        return parameterMapping;
    }
}
