package jvm.java1;

/**
 * 结束的方式有两种：
 *  - 抛出异常
 *  - 正常结束
 */
public class StackFrameTest {
    public static void main(String[] args) throws Exception {
        StackFrameTest stackFrameTest = new StackFrameTest();
        stackFrameTest.method1();
        System.out.println("正常结束、、");
    }

    public void method1(){
        System.out.println("method1()_ 执行。。。。");
        method2();
        System.out.println("method1() 执行结束。。。");
        System.out.println(10 / 0);
    }

    private int method2() {
        System.out.println("method2开始执行。。。。");
        int i = 10;
        int m = (int) method3();
        System.out.println("method2 即将结束。。。");
        return i + m;
    }

    private double method3() {
        System.out.println("method3开始执行。。。");
        double j = 20.0;
        System.out.println("method3 即将结束");
        return j;
    }

}
