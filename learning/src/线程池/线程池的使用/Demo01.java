package 线程池.线程池的使用;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Demo01 {

    public static void main(String[] args) {
        //自定义线程池！ 工作中只会使用 ThreadPoolExecutor

        /**
         * 最大线程该如何定义（线程池的最大的大小如何设置！）
         * 1、CPU  密集型，几核，就是几，可以保持CPU的效率最高！
         */

        //获取电脑CPU核数
        System.out.println(Runtime.getRuntime().availableProcessors());    //8核

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                2,                                 //核心线程池大小
                Runtime.getRuntime().availableProcessors(),   //最大核心线程池大小（CPU密集型，根据CPU核数设置）
                3,                               //超时了没有人调用就会释放
                TimeUnit.SECONDS,                             //超时单位
                new LinkedBlockingDeque<>(3),                 //阻塞队列
                Executors.defaultThreadFactory(),             //线程工厂，创建线程的，一般不用动
                new ThreadPoolExecutor.AbortPolicy());        //银行满了，还有人进来，不处理这个人的，抛出异常

        try {
            //最大承载数，Deque + Max    (队列线程数+最大线程数)
            //超出 抛出 RejectedExecutionException 异常
            for (int i = 1; i <= 9; i++) {
                //使用了线程池之后，使用线程池来创建线程
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName()+" ok");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //线程池用完，程序结束，关闭线程池
            threadPool.shutdown();      //（为确保关闭，将关闭方法放入到finally中）
        }
    }

}
