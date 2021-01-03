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

public class ZKGet {

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
    public void get1() throws Exception{
        //arg1  节点的路径
        //arg3  读取节点属性的对象
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData("/get/node1", false, stat);
        // 打印数据
        System.out.println(new String(data));
        // 版本信息
        System.out.println(stat.getVersion());
    }


    @Test
    public void get2() throws Exception{
        //异步方式
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                //0 代表读取成功
                System.out.println(rc);
                // 节点的路径
                System.out.println(path);
                //  数据
                System.out.println(new String(data));
                // 属性对象
                System.out.println(stat.getVersion());

            }
        },"I am context");
        Thread.sleep(1000);
        System.out.println("结束");
    }


}
