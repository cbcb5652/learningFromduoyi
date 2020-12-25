package 设计模式.单例模式;

public class InstanceFactory {
    private static class InstanceHolder{
        public static InstanceFactory instance = new InstanceFactory();
    }

    public static InstanceFactory getInstance(){
        return InstanceHolder.instance;    // 这里将导致InstanceHolder 类被初始化
    }

}
