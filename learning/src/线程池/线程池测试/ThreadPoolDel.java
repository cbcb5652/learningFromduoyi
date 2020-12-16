package 线程池.线程池测试;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolDel {

    public static void main(String[] args) {
        // 创建线程池
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5), Executors.defaultThreadFactory());
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        for (int i = 0; i < 9; i++) {


        }

    }

}