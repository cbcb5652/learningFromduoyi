package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorGet {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("get")
                .build();
        client.start();
    }

    @After
    public void after() {
        client.close();
    }


    @Test
    public void get1() throws Exception {
        // 读取数据
        byte[] bytes = client.getData().forPath("/node1");
        System.out.println(new String(bytes));
    }


    @Test
    public void get2() throws Exception {
        // 读取节点属性
        Stat stat = new Stat();
        byte[] bytes = client.getData()
                // 读取属性
                .storingStatIn(stat)
                .forPath("/node1");
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }


    @Test
    public void get3() throws Exception {
       // 异步方式读取
        client.getData().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                //节点路径
                System.out.println(curatorEvent.getPath());
                //事件类型
                System.out.println(curatorEvent.getType());
                // 数据
                System.out.println(new String(curatorEvent.getData()));
            }
        }).forPath("/node1");
        Thread.sleep(5000);
        System.out.println("结束");
    }

    @Test
    public void delete4() throws Exception {
        // 异步创建
        client.delete().deletingChildrenIfNeeded()
                .withVersion(-1)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        //节点路径
                        System.out.println(curatorEvent.getPath());
                        //时间类型
                        System.out.println(curatorEvent.getType());
                    }
                }).forPath("/node1");
        Thread.sleep(5000);
        System.out.println("结束");
    }

}
