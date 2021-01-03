package com.duoyi.demo;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.TimeUnit;

public class ZkLock {

    @SneakyThrows
    public static void main(String[] args) {

        final String connectString = "49.232.151.218:2181";

        // 重试策略，初始化每次重试之间需要等待的时间，基准等待时间为1秒
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        // 使用默认的会话时间(60秒) 和连接超时时间(15秒) 来创建zookeeper客户端
        @Cleanup
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(15 * 1000)
                .sessionTimeoutMs(60 * 100)
                .retryPolicy(retryPolicy)
                .build();

        // 启动客户端
        client.start();

        final String lockNode = "/lock_node";
        InterProcessMutex lock = new InterProcessMutex(client, lockNode);
        try {
            lock.acquire();

            if (lock.acquire(60, TimeUnit.SECONDS)) {
                Stat stat = client.checkExists().forPath(lockNode);
                if (null != stat) {
                    System.out.println("同步操作~~~");
                }
            }

        } finally {
            if (lock.isAcquiredInThisProcess()) {
                lock.release();
            }
        }


    }

}
