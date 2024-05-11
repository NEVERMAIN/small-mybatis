package com.openicu.mybatis.test;

import cn.hutool.json.JSONUtil;
import com.openicu.mybatis.builder.xml.XMLConfigBuilder;
import com.openicu.mybatis.io.Resources;
import com.openicu.mybatis.session.Configuration;
import com.openicu.mybatis.session.SqlSession;
import com.openicu.mybatis.session.SqlSessionFactory;
import com.openicu.mybatis.session.SqlSessionFactoryBuilder;
import com.openicu.mybatis.session.defaults.DefaultSqlSession;
import com.openicu.mybatis.test.dao.IUserDao;
import com.openicu.mybatis.test.po.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

/**
 * @description:
 * @author: 云奇迹
 * @date: 2024/5/4
 */
public class ApiTest {

    private final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void test_MapperProxyFactory() {

        // 1.从 SqlSessionFactory 中获取 SqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2.获取映射器对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3. 测试验证
        User user = userDao.queryUserInfoById(1L);
        logger.info("测试结果:{}",JSONUtil.toJsonStr(user));
    }

    @Test
    public void test_selectOne() throws IOException {
        // 解析 XML
        Reader reader = Resources.getResourceAsReader("mybatis-config-datasource.xml");
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();

        // 获取 DefaultSqlSession
        SqlSession sqlSession = new DefaultSqlSession(configuration);

        // 执行查询：默认是一个集合参数
        Object[] req = {1L};
        Object res = sqlSession.selectOne("com.openicu.mybatis.test.dao.IUserDao.queryUserInfoById", req);
        logger.info("测试结果：{}", JSONUtil.toJsonStr(res));
    }


    @Test
    public void test_SqlSessionFactory(){
        // 1.从 SqlSessionFactory 中获取 SqlSession
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config-datasource.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 2.获取映射器
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        // 3.测试验证
        for (int i = 0; i < 50; i++) {
            User user = userDao.queryUserInfoById(1L);
            logger.info("测试结果:{}",JSONUtil.toJsonStr(user));
        }
    }
}
