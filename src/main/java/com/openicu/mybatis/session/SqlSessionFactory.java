package com.openicu.mybatis.session;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/7
 */
public interface SqlSessionFactory {

    /**
     * 打开一个 session
     * @return SqlSession
     */
    SqlSession openSession();
}
