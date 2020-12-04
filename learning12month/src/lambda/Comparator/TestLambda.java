package lambda.Comparator;

import java.util.Comparator;
import java.util.TreeSet;

public class TestLambda {
    public static void main(String[] args) {
        // 第一种写法
        Comparator<Integer> com = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1,o2);
            }
        };
        TreeSet<Integer> ts =  new TreeSet<>(com);

        // 第二种写法
        Comparator<Integer> com1 = (x,y) -> Integer.compare(x,y);
        TreeSet<Integer> ts1 = new TreeSet<>(com1);
    }
}
