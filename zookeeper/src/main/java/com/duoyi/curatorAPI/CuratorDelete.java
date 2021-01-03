package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorDelete {

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
    public void delete1() throws Exception{
        // 删除节点
        client.delete().forPath("/node1");
        System.out.println("结束");
    }


    @Test
    public void delete2() throws Exception{
        client.delete()
                // 版本号
                .withVersion(4).forPath("/node1");
        System.out.println("结束");
    }


    @Test
    public void delete3()throws Exception{
        // 删除包含子节点的节点
        client.delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1).forPath("/node1");
        System.out.println("结束");
    }

    @Test
    public void delete4() throws Exception{
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
