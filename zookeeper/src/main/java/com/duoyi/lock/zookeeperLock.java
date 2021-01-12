package com.duoyi.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class zookeeperLock implements Lock {

    private Logger logger = LoggerFactory.getLogger(zookeeperLock.class);
    private static final String ZK_IP_PORT = "49.232.151.218:2181";
    private static final String LOCK_NODE = "/LOCK";

//    private ZkClient zkClient = new ZkClient(ZK_IP_PORT);
    private CountDownLatch cdl = null;

    @Override
    public void lock() {
        if (tryLock()){
            return ;
        }
        waitForLock();
        lock();
    }

    private void waitForLock() {
        // 给节点加监听

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    /**
     * 阻塞枷锁
     */



}
