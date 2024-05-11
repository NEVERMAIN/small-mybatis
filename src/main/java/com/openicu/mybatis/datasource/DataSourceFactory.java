package com.openicu.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @description: 数据源工厂
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public interface DataSourceFactory {

    /**
     * 设置属性信息。
     * 该方法用于配置和更新数据源相关的属性信息。
     * @param properties 包含数据源配置属性的Properties对象。
     */
    void setProperties(Properties properties);

    /**
     * 获取数据源。
     * 该方法用于获取配置好的数据源实例。
     * @return DataSource 返回配置好的数据源实例。
     */
    DataSource getDataSource();
}
