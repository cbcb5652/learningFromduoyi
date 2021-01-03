package com.duoyi.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 *  创建回话的例子
 */
public class CreateClientExamples {

    public static void main(String[] args) {

        String connectionString ="49.232.151.218:2181";
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000,3,Integer.MAX_VALUE);

        /**
         * connectionString:  zk地址
         * retryPolicy:       重试策略
         * 默认的sessionTimeoutMs为60000
         * 默认的connectionTimeoutMs为15000
         */
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(connectionString,retryPolicy);
        curatorFramework.start();

        /**
         * connectionString zk地址
         * sessionTimeoutMs 会话超时时间
         * connectionTimeoutMs 连接超时时间
         * retryPolicy 重试策略
         */
        CuratorFramework curatorFramework1 = CuratorFrameworkFactory.newClient(connectionString,60000,1500,retryPolicy);
        curatorFramework1.start();

        CuratorFramework curatorFramework2 = CuratorFrameworkFactory.builder().connectString(connectionString)
                .sessionTimeoutMs(60000)
                .connectionTimeoutMs(15000)
                .namespace("/lock_node")
                .retryPolicy(retryPolicy)
                .build();
        curatorFramework2.start();


    }


}
