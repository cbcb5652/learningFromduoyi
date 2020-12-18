package juc;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

class MyThread implements Runnable{
    @Override
    public void run() {

    }
}

class MyThread2 implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        return 1024;
    }
}



public class CallableDemo {
    public static void main(String[] args) throws Exception {

        FutureTask futureTask = new FutureTask(new MyThread2());

        new Thread(futureTask,"A").start();

        System.out.println(futureTask.get());

    }
}
