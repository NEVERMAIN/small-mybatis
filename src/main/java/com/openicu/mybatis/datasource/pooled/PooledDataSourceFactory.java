package com.openicu.mybatis.datasource.pooled;

import com.openicu.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @description: 有连接池的数据源工厂
 * @author: 云奇迹
 * @date: 2024/5/10
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(props.getProperty("driver"));
        pooledDataSource.setUrl(props.getProperty("url"));
        pooledDataSource.setUsername(props.getProperty("username"));
        pooledDataSource.setPassword(props.getProperty("password"));
        return pooledDataSource;
    }
}
