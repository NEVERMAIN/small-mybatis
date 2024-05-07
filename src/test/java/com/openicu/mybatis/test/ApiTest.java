package com.openicu.mybatis.test;

import com.openicu.mybatis.binding.MapperProxyFactory;
import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.session.SqlSession;
import com.openicu.mybatis.session.defaults.DefaultSqlSessionFactory;
import com.openicu.mybatis.test.dao.IUserDao;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/4
 */
public class ApiTest {

    private Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_MapperProxyFactory() {

        // 1.注册 Mapper
        MapperRegister mapperRegister = new MapperRegister();
        mapperRegister.addMapper("com.openicu.mybatis.test.dao");

        // 2.从 sqlSession 工厂获取 Session
        DefaultSqlSessionFactory factory = new DefaultSqlSessionFactory(mapperRegister);
        SqlSession sqlSession = factory.openSession();

        // 3.获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 4.测试验证
        String res = userDao.queryUserName("10001");
        System.out.println(res);
    }
}
