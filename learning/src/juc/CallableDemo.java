package juc;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

class MyThread implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        System.out.println("come in here *************");
        TimeUnit.SECONDS.sleep(2);
        System.out.println(Thread.currentThread().getName());
        return 1024;
    }
}

public class CallableDemo {
    public static void main(String[] args) throws Exception {

        FutureTask futureTask = new FutureTask(new MyThread());
        // 只会调用一次接口，经过一次计算之后，第二次调用的时候直接服用第一次的结果。   A和B都 有可能第一个进去
        new Thread(futureTask,"A").start();
        new Thread(futureTask,"B").start();

        System.out.println(Thread.currentThread().getName()+"**************计算完成");

        System.out.println(futureTask.get());

    }
}
