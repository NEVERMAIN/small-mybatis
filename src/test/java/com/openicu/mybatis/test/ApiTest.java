package com.openicu.mybatis.test;

import com.openicu.mybatis.binding.MapperProxyFactory;
import com.openicu.mybatis.test.dao.IUserDao;
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

    public static void main(String[] args) {
        MapperProxyFactory<IUserDao> factory = new MapperProxyFactory<>(IUserDao.class);
        HashMap<String, String> sqlSession = new HashMap<>();

        sqlSession.put("com.openicu.mybatis.test.dao.IUserDao.queryUserName","模拟执行 Mapper.xml 中 SQL 语句的操作:查询用户姓名");
        sqlSession.put("com.openicu.mybatis.test.dao.IUserDao.queryUserAge","模拟执行 Mapper.xml 中 SQL 语句的操作:查询用户年纪");

        IUserDao userDao = factory.newInstance(sqlSession);

        String res = userDao.queryUserName("10001");
        System.out.println(res);

    }
}
