package jvm.栈;

/**
 * 栈溢出
 *
 * 默认情况下：count : 15262
 * 设置栈的大小 ： -Xss256k    ----->   会使count的值变小
 */
public class StackErrorTest {
    private static int count = 1;
    public static void main(String[] args) {
        System.out.println(count++);
        main(args);
    }
}
