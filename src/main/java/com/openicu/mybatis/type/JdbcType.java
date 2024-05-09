package com.openicu.mybatis.type;

import cn.hutool.core.lang.hash.Hash;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: JDBC 类型枚举
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public enum JdbcType {

    INTEGER(Types.INTEGER),
    DOUBLE(Types.DOUBLE),
    DECIMAL(Types.DECIMAL),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP);

    public final int type_code;

    private static Map<Integer,JdbcType> codeLookup = new HashMap<>();

    JdbcType(int code){
        this.type_code = code;
    }

    public static JdbcType forCode(int code){
        return codeLookup.get(code);
    }

}
