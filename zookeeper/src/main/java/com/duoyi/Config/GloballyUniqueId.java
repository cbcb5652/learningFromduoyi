package com.duoyi.Config;

import com.duoyi.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class GloballyUniqueId implements Watcher {

    String IP = "49.232.151.218:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String deaultPath = "/uniqueId";
    ZooKeeper zooKeeper;

    @Override
    public void process(WatchedEvent watchedEvent) {
        try{
            // 捕获事件状态
            if (watchedEvent.getType() == Event.EventType.None){
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }else if (watchedEvent.getState() == Event.KeeperState.Disconnected){
                    System.out.println("连接断开");
                }else if (watchedEvent.getState() == Event.KeeperState.Expired){
                    System.out.println("连接超时！");
                    zooKeeper = new ZooKeeper(IP,6000,new ZKConnectionWatcher());
                }else if (watchedEvent.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("验证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 构造方法
    public GloballyUniqueId(){
        try{
            // 打开连接
            zooKeeper = new ZooKeeper(IP,5000,this);
            // 等待连接的创建
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 生成id的方法
    public String getUniqueId(){
        String path = "";
        try{
           path = zooKeeper.create(deaultPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }catch (Exception e){
            e.printStackTrace();
        }
        return path.substring(9);
    }

    public static void main(String[] args) {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();
        for (int i = 1; i <= 5; i++) {
            String id = globallyUniqueId.getUniqueId();
            System.out.println(id);
        }
    }

}
