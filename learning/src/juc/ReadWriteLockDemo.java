package juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyCache{
    private volatile Map<String,Object> map = new HashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    public void put(String key,Object value) throws InterruptedException {
        // 加写锁
        readWriteLock.writeLock().lock();

        System.out.println(Thread.currentThread().getName()+"\t ---写入数据:"+key);
        // 暂停一会
        TimeUnit.MILLISECONDS.sleep(300);
        map.put(key,value);
        System.out.println(Thread.currentThread().getName()+"\t ---写入完成~");

        readWriteLock.writeLock().unlock();
    }

    public void get(String key) throws InterruptedException {
        // 加读锁
        readWriteLock.readLock().lock();

        System.out.println(Thread.currentThread().getName() + "\t 读取数据");
        TimeUnit.MILLISECONDS.sleep(300);
        Object result = map.get(key);
        System.out.println(Thread.currentThread().getName() + "\t 读取完成"+result);

        readWriteLock.readLock().unlock();
    }

}

/**
 * 多个线程同时去读一个资源类没有任何问题，所以为了满足并发量，读取共享资源应该可以同时进行
 * 但是
 * 如果有线程想去写共享资源，就不应该再有其他线程可以对该资源进行读或写
 * 总结：
 *      - 读-读 能共享
 *        读-写 不能共享
 *        写-写 不能共享
 */

public class ReadWriteLockDemo {
    public static void main(String[] args) {

        MyCache myCache = new MyCache();

        for (int i = 1; i <= 5; i++) {
            final int tempInt = i;
            new Thread(() ->{
                try {
                    myCache.put(tempInt+"" , tempInt + "");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }

        for (int i = 1; i <= 5; i++) {
            final int tempInt = i;
            new Thread(() ->{
                try {
                    myCache.get(tempInt+"" );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },String.valueOf(i)).start();
        }

    }

}
