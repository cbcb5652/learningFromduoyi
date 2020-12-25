package com.duoyi.watcher;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class ZKWatcherExists {

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
                System.out.println("连接对象的参数~");
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
                System.out.println("path="+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
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
    public void watcherExists1() throws Exception{
        zooKeeper.exists("/watcher1",true);
        Thread.sleep(5000);
        System.out.println("结束");
    }

    @Test
    public void watcherExists2() throws Exception{
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("自定义watcher");
                System.out.println("path="+watchedEvent.getPath());
                System.out.println("eventtype="+watchedEvent.getType());
            }
        });
    }


    @Test
    public void watcherExists3() throws Exception {
        // watcher的一次性
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try{
                    System.out.println("自定义watcher");
                    System.out.println("path=" + watchedEvent.getPath());
                    System.out.println("eventtype=" + watchedEvent.getType());
                    // 捕获后再次加入
                    zooKeeper.exists("/watcher1",this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher1",watcher);
        Thread.sleep(8000);
        System.out.println("结束");
    }


    @Test
    public void watcherExists4() throws Exception{
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("1");
                System.out.println("path="+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });

        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("2");
                System.out.println("path="+watchedEvent.getPath());
                System.out.println("eventType="+watchedEvent.getType());
            }
        });
        Thread.sleep(5000);
        System.out.println("结束");

    }

}
