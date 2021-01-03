package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CuratorSet {

    String IP = "49.232.151.218:2181,49.232.151.218:2182,49.232.151.218:2183";
    CuratorFramework client;

    @Before
    public void before(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        client = CuratorFrameworkFactory.builder()
                .connectString(IP)
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
    }

    @After
    public void after(){
        client.close();
    }


    @Test
    public void set1() throws Exception{
        // 更新节点
        client.setData()
                // arg1 节点的路径
                // arg2 节点的数据
                .forPath("/node1","node11".getBytes());
    }


    @Test
    public void set2() throws Exception{
        client.setData()
                // 指定版本号
                .withVersion(-1).forPath("/node1","node111".getBytes());
    }


    @Test
    public void set3()throws Exception{
       // 异步方式修改节点数据
        client.setData().withVersion(-1).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                // 节点的路径
                System.out.println(curatorEvent.getPath());
                // 事件类型
                System.out.println(curatorEvent.getType());
            }
        }).forPath("/node1","node1".getBytes());
        Thread.sleep(5000);
        System.out.println("结束");
    }

    @Test
    public void create4() throws Exception{
        // 异步创建
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                // 异步创建  回调接口
        .inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println(curatorEvent.getPath());
                System.out.println(curatorEvent.getType());
            }
        }).forPath("/node4","node4".getBytes());
        Thread.sleep(5000);
        System.out.println("结束");
    }

}
