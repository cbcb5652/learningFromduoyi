package 线程池.线程池测试;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class ThreadPoolDel {

    public static void main(String[] args) {
        // 创建线程池
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 5, 2L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(3), new ThreadPoolExecutor.CallerRunsPolicy());

        try{

            for (int i = 0; i < 10; i++) {
                pool.execute(() ->{
                    System.out.println(Thread.currentThread().getName()+" \t 办理业务");
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            pool.shutdown();
        }


    }

}