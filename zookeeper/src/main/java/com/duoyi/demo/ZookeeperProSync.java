package com.duoyi.demo;

import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperProSync implements Watcher {

    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception  {
        // zookeeper配置数据存放路径
        String path = "/username";
        // 连接zookeeper 并注册一个默认的监听器
        zk = new ZooKeeper("49.232.151.218:2181",5000,new ZookeeperProSync());
        // 等待zk连接成功的通知
        connectedSemaphore.await();
        // 获取path目录节点的配置数据，并注册默认的监听器
        System.out.println(new String(zk.getData(path,true,stat)));

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        // zk连接成功通知事件
        if (KeeperState.SyncConnected == event.getState()){
            if (EventType.None == event.getType() && null == event.getPath()){
                connectedSemaphore.countDown();
            }else if (event.getType() == EventType.NodeDataChanged){
                try{
                    System.out.println("配置已修改，新值为:"+new String(zk.getData(event.getPath(),true,stat)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
