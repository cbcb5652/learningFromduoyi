package 设计模式.单例模式;

/**
 * 对getInstance() 加sychronized。
 * 但是这样如果被多个线程调用，将导致程序性能下降。
 * 反之，这个延迟初始化方案能提供令人满意的性能
 */
public class SafeLazyInitialization {

    private static SafeLazyInitialization instance;

    public synchronized static SafeLazyInitialization getInstance(){
        if (instance == null){
            instance = new SafeLazyInitialization();
        }
        return instance;
    }


}
