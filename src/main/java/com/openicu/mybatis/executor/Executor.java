package com.openicu.mybatis.executor;

import com.openicu.mybatis.mapping.BoundSql;
import com.openicu.mybatis.mapping.MappedStatement;
import com.openicu.mybatis.session.ResultHandler;
import com.openicu.mybatis.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * @description: 执行器接口
 * @author: 云奇迹
 * @date: 2024/5/14
 */
public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;

    /**
     * 执行查询操作，并返回查询结果。
     *
     * @param ms MappedStatement，代表一个映射语句，用于定义如何从数据库获取数据。
     * @param parameter 参数对象，用于传递给SQL语句进行查询。
     * @param resultHandler 结果处理器，用于自定义结果集的处理方式。
     * @param boundSql 绑定的SQL对象，包含实际执行的SQL语句及其参数。
     * @return 返回查询结果，类型为List<E>，E代表泛型元素类型。
     */
    <E> List<E> query(MappedStatement ms, Object parameter, ResultHandler resultHandler, BoundSql boundSql);

    /**
     * 获取一个事务对象。
     *
     * @return 返回当前环境下的事务对象。
     */
    Transaction getTransaction();

    /**
     * 提交事务。
     *
     * @param required 标识事务是否为必需的，true表示必需，false表示可选。
     * @throws SQLException 如果在提交事务过程中出现错误，则抛出SQLException。
     */
    void commit(boolean required) throws SQLException;

    /**
     * 回滚事务。
     *
     * @param required 标识事务是否为必需的，true表示必需，false表示可选。
     * @throws SQLException 如果在回滚事务过程中出现错误，则抛出SQLException。
     */
    void rollback(boolean required) throws SQLException;

    /**
     * 关闭资源，可以选择是否强制回滚事务。
     *
     * @param forceRollback 是否强制回滚当前事务，true表示强制回滚，false表示根据当前事务状态正常关闭。
     */
    void close(boolean forceRollback);


}
