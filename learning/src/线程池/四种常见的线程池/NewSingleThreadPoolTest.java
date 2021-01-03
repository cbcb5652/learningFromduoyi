package 线程池.四种常见的线程池;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NewSingleThreadPoolTest {

    public static void main(String[] args) {
        // 一池五个受理线程
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        try{
            // 10个顾客过来银行办理业务，目前池子里面有5个工作人员提供服务
            for (int i = 1; i <= 10; i++) {
                executorService.execute(()->{
                    System.out.println(Thread.currentThread().getName() + "\t 办理业务");
                });
                // 暂停
                TimeUnit.MILLISECONDS.sleep(1000);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }

}
