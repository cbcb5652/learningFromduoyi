package jvm.java.lang;

public class String{
    static{
        System.out.println("我是自定义的string的静态代码块");
    }

    // 运行不了！！！
    public static void main(String[] args) {
        System.out.println("hello world!");
    }
}
