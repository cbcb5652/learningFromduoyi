package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.retry.RetryUntilElapsed;

public class CuratorConnection {

    public static void main(String[] args) {

        //3秒后重连
        RetryPolicy retryPolicy = new RetryOneTime(3000);

        // 每3秒重连一次，重连3此
        RetryPolicy retryPolicy1 = new RetryNTimes(3,3000);

        //每3秒重连一次，总等待时间超过10秒后停止重连
        RetryPolicy retryPolicy2 = new RetryUntilElapsed(1000,3000);

        //baseSleepTimeMs * Math.max(1,random.nextInt(1<<(retryCount + 1))
        RetryPolicy retryPolicy3 = new ExponentialBackoffRetry(1000,3);

        // 创建连接对象
        CuratorFramework client = CuratorFrameworkFactory.builder()
                // 地址端口号
                .connectString("49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183")
                // 会话超时时间
                .sessionTimeoutMs(5000)
                // 重连机制
                .retryPolicy(retryPolicy3)
                // 命名空间
                .namespace("create")
                // 构建连接对象
                .build();

        // 打开连接
        client.start();
        System.out.println(client.isStarted());
        // 关闭连接
        client.close();

    }

}
