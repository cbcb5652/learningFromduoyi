package jvm.双亲委派;

public class StringTest {

    public static void main(String[] args) {
        java.lang.String str = new java.lang.String();
        System.out.println("hello world");

        StringTest stringTest = new StringTest();
        System.out.println(stringTest.getClass().getClassLoader());

    }

}
