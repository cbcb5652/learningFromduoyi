package 注解;

public class T {

    public static void main(String[] args) {
        test();
    }

    // 表示忽略警告信息
    @SuppressWarnings("unused")
    public static void test(){
        int i;
        System.out.println("This is a test.");
    }

    @Override
    public String toString() {
        return "Test class";
    }
}
