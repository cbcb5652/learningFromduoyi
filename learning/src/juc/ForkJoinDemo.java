package juc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

class MyTask extends RecursiveTask<Integer>{

    public static final Integer ADJUST_VALUE = 10;

    private int begin;
    private int end;
    private int result;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        // 太小 直接计算
        if ((end -begin) <= ADJUST_VALUE){
            for (int i = begin; i <= end ; i++) {
                result = result + i;
            }
        }else {
            int middle = (end + begin) / 2;
            MyTask myTask01 = new MyTask(begin,middle);
            MyTask myTask02 = new MyTask(middle + 1, end);

            myTask01.fork();
            myTask02.fork();

            result = myTask01.join() + myTask02.join();

        }

        return result;
    }
}

public class ForkJoinDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MyTask myTask = new MyTask(0,100);
        ForkJoinPool threadPool = new ForkJoinPool();
        ForkJoinTask<Integer> forkJoin = threadPool.submit(myTask);

        System.out.println(forkJoin.get());

        threadPool.shutdown();

    }

}
