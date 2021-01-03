package com.duoyi.curatorAPI;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CuratorWatcher {

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
    public void watcher1() throws Exception {
        // 监视某个节点的数据变化
        // arg1 连接对象
        // arg2 监视的节点路径
        NodeCache nodeCache = new NodeCache(client,"/watcher1");
        // 启动监视器对象
        nodeCache.start();
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            // 节点变化时回调的方法
            @Override
            public void nodeChanged() throws Exception {
                System.out.println(nodeCache.getCurrentData().getPath());
                System.out.println(new String(nodeCache.getCurrentData().getData()));
            }
        });
        Thread.sleep(100000);
        System.out.println("结束");
        // 关闭监视器对象
        nodeCache.close();
    }


    @Test
    public void watcher2() throws Exception {
        // 监视子节点的变化
        //arg1 连接对象
        //arg2 监视的节点路径
        //arg3 事件中是否可以获取节点的数据
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/watcher1",true);
        // 启动监听
        pathChildrenCache.start();
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //节点的事件类型
                System.out.println(pathChildrenCacheEvent.getType());
                // 节点的路径
                System.out.println(pathChildrenCacheEvent.getData().getPath());
                // 节点数据
                System.out.println(new String(pathChildrenCacheEvent.getData().getData()));
            }
        });

        Thread.sleep(100000);
        System.out.println("结束");
        //关闭监听
        pathChildrenCache.clear();

    }

}
