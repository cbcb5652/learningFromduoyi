package lambda.operator;

import lambda.filter.Employee;

import java.util.*;
import java.util.function.Consumer;

/**
 * -> 标识符将lambda表达式分为两部分
 * 左侧： Lambda 表达式的参数列表
 * 右侧： Lambda 表达式中所需执行的功能，即Lambda 体
 *
 * 语法格式一： 无参数，无返回值
 *          () -> System.out.println("Hello Lambda!")
 * 语法格式二：有一个参数，并且无返回值
 *
 * 语法格式三： 有一个参数，小括号可以省略不写
 *
 * 语法格式四： 有两个以上的参数，有返回值，并且Lambda 体中有多条语句
 *
 * 语法格式五： 若Lambda体中只有一条语句， return 和 大括号都可以省略不写
 *
 * 语法格式六： Lambda可以通过推断出 参数的类型
 *          左右遇一括号省
 *          左侧推断类型省
 *
 *  Lambda 表达式需要函数式接口的支持
 *  函数式接口： 接口中只有一个抽象方法的接口，称为函数式接口、可以使用注解@FunctionalInterface 修饰
 *                  可以检查是否是函数式接口
 *
 */
public class Lambda {

    public static void main(String[] args) {
        int num = 0;    //默认加上final
        // 无参函数式接口
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 这里的num局部变量默认加上了final
                System.out.println("Hello World!"+num);
            }
        };
        r.run();
        System.out.println("------------------------------------------------");
        Runnable r1 = () -> System.out.println("Hello Lambda!"+num);
        r1.run();

        System.out.println("=================有一个参数，但是无返回值===================");
        Consumer<String> con = (x) -> System.out.println(x);
        Consumer<String> con1 = x -> System.out.println(x);
        con.accept("我在多益！");

        System.out.println("=================有多个参数===============================");
        Comparator<Integer> com = (x,y) ->{
            System.out.println("函数式接口");
            return Integer.compare(x,y);
        };

        Comparator<Integer> com1 = (x,y) -> Integer.compare(x,y);

        List<Employee> employees = Arrays.asList(
                new Employee("张三",18,99.99),
                new Employee("李四",20,55.99),
                new Employee("王五",12,66.99),
                new Employee("赵六",21,77.99),
                new Employee("田七",23,88.99)
        );

        System.out.println("=================排序==========================");
        Collections.sort(employees,(e1,e2) ->{
            if (e1.getAge() < e2.getAge()){
                return e1.getName().compareTo(e2.getName());
            }else {
                return Integer.compare(e1.getAge(),e2.getAge());
            }
        });

        employees.forEach(employee -> {
            System.out.println(employee);
        });

        String st = strHandler(" 这 是 啥 呀!", (str) -> {
            return str.trim() + "hello World!";
        });
        System.out.println(st);
    }
    //需求：用于处理字符串的方法
    public static String strHandler(String str,MyFunction mf){
        return mf.getValue(str);
    }

}
