package com.openicu.mybatis.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @description: 通过类加载器获得 resource 的辅助类
 * @author: 云奇迹
 * @date: 2024/5/8
 */
public class Resources {

    public static Reader getResourceAsReader(String resource) {
        return new InputStreamReader(getResourceAsStream(resource));
    }

    private static InputStream getResourceAsStream(String resource) {
        ClassLoader[] classLoaders = getClassLoader();
        for (ClassLoader classLoader : classLoaders) {
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (null != inputStream) {
                return inputStream;
            }
        }
        throw new RuntimeException("Could not find resource " + resource);
    }

    /**
     * 获取当前环境下的类加载器数组。
     * <p>该方法不接受任何参数。
     * @return 返回一个包含系统类加载器和当前线程上下文类加载器的数组。
     */
    private static ClassLoader[] getClassLoader() {
        // 创建并返回一个包含系统类加载器和当前线程上下文类加载器的数组
        return new ClassLoader[]{
                // 系统类加载器
                ClassLoader.getSystemClassLoader(),
                // 当前线程的上下文类加载器
                Thread.currentThread().getContextClassLoader()
        };
    }


    /**
     * 加载类
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

}
