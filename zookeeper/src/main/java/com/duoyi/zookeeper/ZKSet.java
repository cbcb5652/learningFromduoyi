package com.duoyi.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKSet {

    String IP = "49.232.151.218:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);

        /**
         * arg1: 服务器的ip和端口
         * arg2: 客户端与服务器之间的会话超时时间，以毫秒为单位
         * arg3: 监视器对象
         */
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接创建成功~");
                    countDownLatch.countDown();
                }
            }
        });
        // 主线程阻塞等待连接对象的创建成功
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void set1()throws Exception{
        // arg1： 节点的路径
        // arg2：修改的数据
        // arg3: 数据版本号，-1代表版本号不参与更新
        Stat stat = zooKeeper.setData("/set/node1", "node11".getBytes(), -1);
        System.out.println(stat.getAversion());
    }

    @Test
    public void set2()throws Exception{
        zooKeeper.setData("/set/node2", "node2".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                //0 代表创建成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                // 上下文参数对象
                System.out.println(ctx);
                // 属性描述对象
                System.out.println(stat.getAversion());
            }
        },"I am Context");
        Thread.sleep(10000);
        System.out.println("结束");
    }


}
