package com.openicu.mybatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 池状态
 * @author: 云奇迹
 * @date: 2024/5/10
 */
public class PoolState {

    protected PooledDataSource pooledDataSource;

    // 空闲连接
    protected final List<PooledConnection> idleConnections = new ArrayList<>();
    // 活跃连接
    protected final List<PooledConnection> activeConnections = new ArrayList<>();

    // 请求次数
    protected long requestCount = 0;
    /**
     * 记录和管理连接池中连接的累计请求时间和检查出时间的相关数据。
     */
    // 累计请求时间，记录所有从连接池获取连接的请求所花费的总时间
    protected long accumulatedRequestTime = 0;
    // 累计检查出时间，记录所有连接从连接池被检查出的总时间
    protected long accumulatedCheckoutTime = 0;
    // 声明为过期的连接计数，记录从连接池中获取到的被标记为过期的连接数量
    protected long claimedOverdueConnectionCount = 0;
    // 过期连接的累计检查出时间，记录所有被标记为过期的连接被检查出的总时间
    protected long accumulatedCheckoutTimeOfOverdueConnections = 0;

    // 总等待时间
    protected long accumulatedWaitTime = 0;
    // 要等待的次数
    protected long hadToWaitCount = 0;
    // 失败连接次数
    protected long badConnectionCount = 0;

    public PoolState(PooledDataSource pooledDataSource){
        this.pooledDataSource = pooledDataSource;
    }

    /**
     * 获取当前请求总数。
     * @return 请求总数。
     */
    public synchronized long getRequestCount(){
        return requestCount;
    }

    /**
     * 计算并返回平均请求时间。
     * 如果请求总数为0，则返回0。
     * @return 平均请求时间。
     */
    public synchronized long getAverageRequestTime(){
        return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
    }

    /**
     * 计算并返回平均等待时间。
     * 如果等待次数为0，则返回0。
     * @return 平均等待时间。
     */
    public synchronized long getAverageWaitTime(){
        return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;
    }

    /**
     * 获取等待次数。
     * @return 等待次数。
     */
    public synchronized long getHadToWaitCount(){
        return hadToWaitCount;
    }

    /**
     * 获取坏连接次数。
     * @return 坏连接次数。
     */
    public synchronized long getBadConnectionCount(){
        return badConnectionCount;
    }

    /**
     * 获取声称过期的连接次数。
     * @return 声称过期的连接次数。
     */
    public synchronized long getClaimedOverdueConnectionCount(){
        return claimedOverdueConnectionCount;
    }

    /**
     * 计算并返回平均过期检查时间。
     * 如果声称过期的连接次数为0，则返回0。
     * @return 平均过期检查时间。
     */
    public synchronized long getAverageOverdueCheckoutTime(){
        return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
    }

    /**
     * 计算并返回平均检查出时间。
     * 如果请求总数为0，则返回0。
     * @return 平均检查出时间。
     */
    public synchronized long getAverageCheckoutTime(){
        return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
    }

    /**
     * 获取当前空闲连接数。
     * @return 空闲连接数。
     */
    public synchronized int getIdleConnectionCount(){
        return idleConnections.size();
    }

    /**
     * 获取当前活跃连接数。
     * @return 活跃连接数。
     */
    public synchronized int getActiveConnectCount(){
        return activeConnections.size();
    }



}
