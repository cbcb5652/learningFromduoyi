package juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 题目： 三个售票员     卖出        30张票
 *
 * 多线程编程的企业级套路+模板
 *
 * 1. 在高聚合低耦合的前提下，线程                操作(对外暴露的接口)           资源类
 */
public class Demo01 {
    public static void main(String[] args) {

        Ticket ticket = new Ticket();

/*        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 40; i++) {
                    ticket.saleTicket();
                }
            }
        },"A").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 40; i++) {
                    ticket.saleTicket();
                }
            }
        },"B").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 40; i++) {
                    ticket.saleTicket();
                }
            }
        },"C").start();*/

        new Thread(() ->{ for (int i = 0; i < 40; i++)  ticket.saleTicket(); },"A").start();
        new Thread(() ->{ for (int i = 0; i < 40; i++)  ticket.saleTicket(); },"B").start();
        new Thread(() ->{ for (int i = 0; i < 40; i++)  ticket.saleTicket(); },"C").start();

    }
}

// 资源类
class Ticket{
    private int number = 30;
     // ReentranLock 的粒度比synchronized小，能更进一步控制方法内部的
    private Lock lock = new ReentrantLock();

    public  void saleTicket(){
        lock.lock();
        try {
            if (number > 0){
                System.out.println(Thread.currentThread().getName()+"\t卖出第:"+(number--)+"\t还剩:"+number);
            }
        }catch (Exception e){
          e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
