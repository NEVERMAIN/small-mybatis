package com.openicu.mybatis.session;

/**
 * @description: 工厂模式接口，构建 SqlSession 的工厂
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
