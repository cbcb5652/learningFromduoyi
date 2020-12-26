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

import java.util.List;

public class CuratorExists {

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
    public void exist1() throws Exception {
       // 判断节点是否存在
        Stat stat = client.checkExists()
                // 节点路径
                .forPath("/node3");
        System.out.println(stat);
    }


    @Test
    public void exist2() throws Exception {
        // 异步方式判断节点是否存在
        client.checkExists().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                // 节点路径
                System.out.println(curatorEvent.getPath());
                // 事件类型
                System.out.println(curatorEvent.getType());
                System.out.println(curatorEvent.getStat().getVersion());
            }
        }).forPath("/node2");
        Thread.sleep(5000);
        System.out.println("结束");
    }

}
