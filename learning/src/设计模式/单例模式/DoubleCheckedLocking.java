package 设计模式.单例模式;

/**
 * 双重检查锁
 *
 * 第一次检查的时候如果发现instance不为null，那就不需要执行下面的枷锁和初始化操作。（可以大幅度降低sychnorized带来的性能开销）
 *   volatile是为了保证在第13行的时候，出现的指令重排序问题。
 *   1. 分配对象的内存空间
 *   2. 初始化对象
 *   3. 设置instance指向刚分配的内存地址
 *   以上的执行顺序除了 123 还有可能是 132
 *
 *  单线程执行是没有问题，但是多线程执行的时候，B线程可能看到一个还没被初始化的对象
 */
public class DoubleCheckedLocking {

    private volatile static DoubleCheckedLocking instance;
    public static DoubleCheckedLocking getInstance(){
        if (instance == null){
            synchronized (DoubleCheckedLocking.class){
                if (instance == null){
                    instance = new DoubleCheckedLocking();
                }
            }
        }
        return instance;
    }

}
