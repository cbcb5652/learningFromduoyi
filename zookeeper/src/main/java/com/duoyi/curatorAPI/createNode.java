package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

public class createNode {

    /**
     * 创建永久节点
     */
    @Test
    public void createZnode() throws Exception {
        //1. 定制一个重试策略
        /*
        param1: 重试的时间间隔
        param2: 重试的最大次数
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,1);
        //2. 获取客户端对象
        /*
         param1: 要连接的zookeeper服务器列表
         param2: 会话的超时时间
         param3: 连接的超时时间
         param4: 重试策略
         */
        String connectionStr = "192.168.73.100:2181,192.168.73.110:2181,192.168.73.120:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionStr, 8000, 8000, retryPolicy);

        //3. 开启客户端
        client.start();

        //4. 创建节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/hello","world".getBytes());

        //5. 关闭客户端
        client.close();
    }

    /**
     * 创建临时节点
     */
    @Test
    public void createTmpZnode() throws Exception {
        //1. 定制一个重试策略
        /*
        param1: 重试的时间间隔
        param2: 重试的最大次数
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,1);
        //2. 获取客户端对象
        /*
         param1: 要连接的zookeeper服务器列表
         param2: 会话的超时时间
         param3: 连接的超时时间
         param4: 重试策略
         */
        String connectionStr = "192.168.73.100:2181,192.168.73.110:2181,192.168.73.120:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionStr, 8000, 8000, retryPolicy);

        //3. 开启客户端
        client.start();

        //4. 创建节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/hello2","world2".getBytes());

        Thread.sleep(10000);

        //5. 关闭客户端
        client.close();
    }


    /**
     * 修改节点
     */
    @Test
    public void setZnodeData() throws Exception {
        //1. 定制一个重试策略
        /*
        param1: 重试的时间间隔
        param2: 重试的最大次数
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,1);
        //2. 获取客户端对象
        /*
         param1: 要连接的zookeeper服务器列表
         param2: 会话的超时时间
         param3: 连接的超时时间
         param4: 重试策略
         */
        String connectionStr = "192.168.73.100:2181,192.168.73.110:2181,192.168.73.120:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionStr, 8000, 8000, retryPolicy);

        //3. 开启客户端
        client.start();

        //4. 创建节点
        client.setData().forPath("/hello2","zookeeper".getBytes());

        //5. 关闭客户端
        client.close();
    }


    /**
     * huoqu
     * @throws Exception
     */
    @Test
    public void getZnodeData() throws Exception {
        //1. 定制一个重试策略
        /*
        param1: 重试的时间间隔
        param2: 重试的最大次数
         */
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,1);
        //2. 获取客户端对象
        /*
         param1: 要连接的zookeeper服务器列表
         param2: 会话的超时时间
         param3: 连接的超时时间
         param4: 重试策略
         */
        String connectionStr = "192.168.73.100:2181,192.168.73.110:2181,192.168.73.120:2181";
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionStr, 8000, 8000, retryPolicy);

        //3. 开启客户端
        client.start();

        //4. 创建节点
        byte[] bytes = client.getData().forPath("/hello2");
        System.out.println(new String(bytes));

        //5. 关闭客户端
        client.close();
    }


}
