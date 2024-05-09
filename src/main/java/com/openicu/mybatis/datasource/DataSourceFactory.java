package com.openicu.mybatis.datasource;

import sun.java2d.loops.ProcessPath;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @description: 数据源工厂
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
