package com.openicu.mybatis.executor;

import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.ResultHandler;
import com.openicu.mybatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * @description: 执行器抽象类
 * @author: 云奇迹
 * @date: 2024/5/14
 */
public abstract class BaseExecutor implements Executor{

    private final Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;

    /**
     * 执行器是否已关闭
     */
    private boolean closed;

    protected BaseExecutor(Configuration configuration,Transaction transaction){
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        if(closed){
            throw new RuntimeException("Executor is closed");
        }
        return doQuery(ms,parameter,resultHandler,boundSql);
    }

    protected abstract <E> List<E> doQuery(MappedStatement ms,Object parameter,ResultHandler resultHandler,BoundSql boundSql);


    @Override
    public Transaction getTransaction() {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        return transaction;
    }

    @Override
    public void commit(boolean required) throws SQLException {
        if(closed){
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        if(required){
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if(!closed){
            if(required){
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try{
                rollback(forceRollback);
            }finally {
                transaction.close();
            }
        } catch (SQLException e) {
            logger.warn("Unexpected exception on closing transaction.  Cause: " + e);
        }finally {
            transaction = null;
            closed = true;
        }
    }
}
