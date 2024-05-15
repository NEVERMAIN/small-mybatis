package com.openicu.mybatis.session.defaults;

import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.executor.Executor;
import com.openicu.mybatis.mapping.Environment;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;
import com.openicu.mybatis.session.SqlSessionFactory;
import com.openicu.mybatis.session.TransactionIsolationLevel;
import com.openicu.mybatis.transaction.Transaction;
import com.openicu.mybatis.transaction.TransactionFactory;

import java.sql.SQLException;

/**
 * @description: 默认的 SqlSessionFactory
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Transaction tx = null;
        try {
            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            tx = transactionFactory.newTransaction(configuration.getEnvironment().getDataSource(), TransactionIsolationLevel.READ_COMMITTED, false);
            // 创建执行器
            final Executor executor = configuration.newExecutor(tx);
            // 创建 DefaultSqlSession
            return new DefaultSqlSession(configuration, executor);

        } catch (Exception e) {
            try {
                assert tx != null;
                tx.close();
            } catch (SQLException ex) {

            }
            throw new RuntimeException("Error opening session.  Cause: " + e);
        }
    }
}
