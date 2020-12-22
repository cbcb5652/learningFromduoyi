package jvm;

public class 类加载器 {

    public static void main(String[] args) {

        Object object = new Object();
        System.out.print(object.getClass().getClassLoader());
        System.out.println("---------------------------------");
        类加载器 classLoader = new 类加载器();
        System.out.println(classLoader.getClass().getClassLoader());

        System.out.println("--------------------------------------------");
        System.out.println(classLoader.getClass().getClassLoader().getParent().getParent());
        System.out.println(classLoader.getClass().getClassLoader().getParent());
        System.out.println(classLoader.getClass().getClassLoader());

    }

}
