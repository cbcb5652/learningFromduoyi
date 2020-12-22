package jvm.test;

public class ClassInitTest {

    private static int num = 1;

    static {
        num = 2;
        number = 20;
        System.out.println(num);
//        System.out.println(number);    // 非法的前向引用
    }

    private static int number = 10;   // prepre 阶段已经给number 赋默认值0;

    public static void main(String[] args) {
        System.out.println(ClassInitTest.num);
        System.out.println(ClassInitTest.number);
    }

}
