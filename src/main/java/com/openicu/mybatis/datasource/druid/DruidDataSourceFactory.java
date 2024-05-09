package com.openicu.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.openicu.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @description: 数据源工厂
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public class DruidDataSourceFactory implements DataSourceFactory {

    private  Properties props;

    @Override
    public void setProperties(Properties props) {
        this.props = props;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(props.getProperty("driver"));
        dataSource.setUrl(props.getProperty("url"));
        dataSource.setUsername(props.getProperty("username"));
        dataSource.setPassword(props.getProperty("password"));
        return dataSource;
    }
}
