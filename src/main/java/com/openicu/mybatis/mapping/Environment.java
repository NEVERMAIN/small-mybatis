package com.openicu.mybatis.mapping;

import com.openicu.mybatis.transaction.TransactionFactory;
import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

import javax.sql.DataSource;

/**
 * @description: 环境.和 xml 配置文件对应
 * @author: 云奇迹
 * @date: 2024/5/9
 */
public final class Environment {

    /**
     * 环境 Id
     */
    private final String id;
    /**
     * 事务工厂
     */
    private final TransactionFactory transactionFactory;
    /**
     * 数据源
     */
    private final DataSource dataSource;

    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {

        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder datasource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource);
        }

    }


    public String getId() {
        return id;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }
}
