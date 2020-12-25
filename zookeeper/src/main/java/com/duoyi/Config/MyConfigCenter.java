package com.duoyi.Config;

import com.duoyi.watcher.ZKConnectionWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class MyConfigCenter implements Watcher {

    String IP = "49.232.151.218:2181";
    CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zooKeeper;

    //用于本地化存储配置信息
    private String url;
    private String username;
    private String password;

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
            }else if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                // 当配置信息发生变化时 从新加载
                initValue();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public MyConfigCenter(){
        initValue();
    }


    // 连接zookeeper服务器，读取配置信息
    public void initValue(){
        try{
            // 创建连接对象
            zooKeeper = new ZooKeeper(IP, 5000, this);
            //阻塞线程 等待连接创建
            countDownLatch.await();
            // 读取配置信息
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try{
            MyConfigCenter myConfig = new MyConfigCenter();
            for (int i = 1; i <= 10; i++) {
                Thread.sleep(5000);
                System.out.println("url:"+myConfig.getUrl());
                System.out.println("username:"+myConfig.getUsername());
                System.out.println("password:"+myConfig.getPassword());
                System.out.println("====================================");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
