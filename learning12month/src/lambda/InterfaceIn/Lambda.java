package lambda.InterfaceIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Java8 内置了四大核心函数式接口
 * Consumer<T>: 消费型接口
 *         void accept(T t)
 *
 * Supplier<T>: 供给型接口
 *          T get();
 * Function<T,R>: 函数型接口
 *          R apply(T t);
 * Predicate<T>: 断言型接口
 *          boolean test(T t);
 */
public class Lambda {
    public static void main(String[] args) {
        // Consumer<T>  消费型接口
        happy(10000,(m) -> System.out.println("哈哈:"+m));

        System.out.println("=========================供给型接口============================");
        // 供给型接口
        getNumList(10,()-> (int)(Math.random()*100)).forEach(System.out::println);

        System.out.println("=====================函数型接口==========================");
        // 函数型接口
        System.out.println(strHandler("\t\t\t\t 我在多益", (str) -> str.trim()));

        System.out.println("===================断言型接口=================");
        // 断言型接口
        List<String> list = Arrays.asList("Hello","duoyi","chenbin","www","wok");
        filterStr(list,(s -> s.length()>3)).forEach(System.out::println);

    }

    // 将满足条件的字符串，放入到集合中
    public static List<String> filterStr(List<String> list, Predicate<String> pre){
        ArrayList<String> strList = new ArrayList<>();
        for (String str : list) {
            if (pre.test(str)){
                strList.add(str);
            }
        }
        return strList;
    }

    // 用户处理字符串
    public static String strHandler(String str, Function<String,String> fun){
        return fun.apply(str);
    }

    public static List<Integer> getNumList(int num, Supplier<Integer> sup){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Integer n = sup.get();
            list.add(n);
        }
        return list;
    }
    public static void happy(double money, Consumer<Double> con){
        con.accept(money);
    }
}
