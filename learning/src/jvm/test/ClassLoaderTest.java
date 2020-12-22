package jvm.test;

public class ClassLoaderTest {
    public static void main(String[] args) {
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);          //jdk.internal.loader.ClassLoaders$AppClassLoader@2f0e140b

        // 获取其上层的：扩展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader);             // jdk.internal.loader.ClassLoaders$PlatformClassLoader@16b98e56

        // 获取其上层的 根加载器    获取不到引导类加载器
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println(bootstrapClassLoader);       // null

        // 对于用户自定义类的加载器: 使用系统类加载器
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader);                // jdk.internal.loader.ClassLoaders$AppClassLoader@2f0e140b

        // String 引导类加载器
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println(classLoader1);               // null

    }

}
