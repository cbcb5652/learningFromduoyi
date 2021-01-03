package 设计模式.单例模式;

/**
 * 不安全的单例
 *
 * 在这个类里面，假设A线程执行代码1的同时，B线程执行代码2。
 * 此时，线程A可能会看到instance引用的对象还没有完成初始化
 */
public class UnsafeLazyInitalization {
    private static UnsafeLazyInitalization instance;
    public static UnsafeLazyInitalization getInstance(){
        if (instance == null){                                  //1. A线程执行
            instance = new UnsafeLazyInitalization();           //2. B线程执行
        }
        return instance;
    }

}
