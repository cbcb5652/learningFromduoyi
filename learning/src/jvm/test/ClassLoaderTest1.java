package jvm.test;
// 获取类加载器
public class ClassLoaderTest1 {
    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader classLoader = Class.forName("java.lang.String").getClassLoader();
        System.out.println(classLoader);

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(contextClassLoader);

        ClassLoader classLoader1 = ClassLoader.getSystemClassLoader().getParent();
        System.out.println(classLoader1);
    }
}
