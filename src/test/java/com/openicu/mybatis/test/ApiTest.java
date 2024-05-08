package com.openicu.mybatis.test;

import cn.hutool.json.JSONUtil;
import com.openicu.mybatis.binding.MapperProxyFactory;
import com.openicu.mybatis.binding.MapperRegister;
import com.openicu.mybatis.io.Resources;
import com.openicu.mybatis.session.SqlSession;
import com.openicu.mybatis.session.SqlSessionFactory;
import com.openicu.mybatis.session.SqlSessionFactoryBuilder;
import com.openicu.mybatis.session.defaults.DefaultSqlSessionFactory;
import com.openicu.mybatis.test.dao.IUserDao;
import com.openicu.mybatis.test.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
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

        // 1.从 SqlSessionFactory 中获取 SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2.获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        String res = userDao.queryUserInfoById("10001");
        logger.info("测试结果:{}",res);
    }
}
