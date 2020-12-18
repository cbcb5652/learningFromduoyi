package juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程之间调度： 实现A --> B --> C
 *  三个线程启动，要求如下：
 *
 *  AA 打印5次，BB打印10次，CC打印15次
 *  接着
 *  AA 打印5次，BB打印10次，CC打印15次
 *  。。。来十轮
 */

class ShareResource{

    private int number = 1;   // A: 1    B: 2   C: 3
    private Lock lock = new ReentrantLock();
    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    public void print5(){
        lock.lock();
        try{
            //1. 判断
            while (number != 1){
                condition1.await();
            }
            //2. 干活
            for (int i = 1; i <= 5; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" +i);
            }
            //3. 通知
            number = 2;
            condition2.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print10(){
        lock.lock();
        try{
            //1. 判断
            while (number != 2){
                condition2.await();
            }
            //2. 干活
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" +i);
            }
            //3. 通知
            number = 3;
            condition3.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();
        try{
            //1. 判断
            while (number != 3){
                condition3.await();
            }
            //2. 干活
            for (int i = 1; i <= 15; i++) {
                System.out.println(Thread.currentThread().getName() + "\t" +i);
            }
            //3. 通知
            number = 1;
            condition1.signal();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}


public class ThreadOrderAccess {

    public static void main(String[] args) {
        ShareResource shareResource = new ShareResource();

        new Thread(() ->{
            for (int i = 1; i <= 10; i++) {
                shareResource.print5();
            }
        },"A").start();

        new Thread(() ->{
            for (int i = 1; i <= 10; i++) {
                shareResource.print10();
            }
        },"B").start();


        new Thread(() ->{
            for (int i = 1; i <= 15; i++) {
                shareResource.print15();
            }
        },"C").start();


    }

}
