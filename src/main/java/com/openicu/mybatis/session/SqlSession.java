package com.openicu.mybatis.session;

/**
 * @description: SqlSession 用来执行SQL,获取映射器,管理事务
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public interface SqlSession {

    /**
     * 根据指定的 sqlID 获取一条记录的封装对象
     * @param statement
     * @return
     * @param <T>
     */
    <T> T selectOne(String statement);

    /**
     * 根据指定的 sqlID 获取一条记录的封装对象,只不过这个方法允许我们可以给 sql传递一些参数
     * 一般在实际使用中,这个参数传递的是 pojo , 或者是 Map
     * @param statement
     * @param parameter
     * @return
     * @param <T>
     */
    <T> T selectOne(String statement,Object parameter);

    /**
     * 得到映射器
     * @param type
     * @return
     * @param <T>
     */
    <T> T getMapper(Class<T> type);

    /**
     * 得到配置类
     * @return
     */
    Configuration getConfiguration();


}
