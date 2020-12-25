package com.duoyi.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKConnectionWatcher implements Watcher {

    // 计数器对象
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    // 连接对象
    static ZooKeeper zooKeeper;

    public static void main(String[] args) {
        try{
            // 50000 保持会话时间50秒，在50秒之内连接还是会重新连接上的。
            zooKeeper = new ZooKeeper("49.232.151.218:2181",50000,new ZKConnectionWatcher());
            //阻塞线程等待连接的创建
            countDownLatch.await();
            // 会话id
            System.out.println(zooKeeper.getSessionId());
            Thread.sleep(5000);
            zooKeeper.close();
            System.out.println("结束");
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            // 事件类型
            if (watchedEvent.getType() == Event.EventType.None){
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                    System.out.println("连接创建成功~");
                }else if (watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("断开连接~~");
                }else if (watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("会话超时~");
                    zooKeeper = new ZooKeeper("49.232.151.218:2181",50000,new ZKConnectionWatcher());
                }else if (watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("认证失败~");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
