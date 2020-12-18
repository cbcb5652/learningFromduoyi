package lambda.stream;

import lambda.filter.Employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 创建stream流的三大步骤
 * 1. 创建Stream
 * 2. 中间操作
 * 3. 终止操作( 终端操作 )
 */
public class TestStreamApi {

    private static  List<Employee> employees = Arrays.asList(
            new Employee("张三",18,99.99),
            new Employee("李四",20,55.99),
            new Employee("王五",12,66.99),
            new Employee("赵六",21,77.99),
            new Employee("田七",23,88.99)
    );

    public static void main(String[] args) {
        // 创建Stream
        //1、可以通过Colletion 系列提供的stream() 或parallelStream()
        List<String> list = new ArrayList<>();
        Stream<String> stream = list.stream();

        //2. 通过Arrays的静态方法stream() 获取数组liu
        Employee[] emps = new Employee[10];
        Stream<Employee> stream1 = Arrays.stream(emps);

        //3. 通过Stream类中的静态方法 of()
        Stream<String> stream2 = Stream.of("aa", "bb", "cc");

        //4. 创建无限流  迭代
        Stream<Integer> stream3 = Stream.iterate(0, (x) -> x + 2);
        stream3.limit(10).forEach(System.out::println);

        //5. 生成
        Stream.generate(() -> Math.random())
                .limit(5)
                .forEach(System.out::println);

        Stream<Employee> stream4 = employees.stream().filter((e) ->{
            System.out.println("Stream API的中间操作");
            return e.getAge() > 35;
        });
        stream4.forEach(System.out::println);

        // limit 操作
        employees.stream().filter((e) -> {
            System.out.println("短路！");
            return e.getSlary() > 5000;
        }).limit(2).forEach(System.out::println);

        // 跳过前两个取后面的
        employees.stream().filter((e) ->e.getSlary() > 5000).skip(2).forEach(System.out::println);

        // 去重, 需要重写hashCode和equals方法
        employees.stream().filter((e) ->e.getSlary() > 5000).skip(2).distinct().forEach(System.out::println);

        /**
         * 映射:
         * map 接收lambda,将元素转换成其他形式或提取信息。接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。
         * flatMap-接收一个函数作为参数，将流中的每个值换成另一个流，然后把所有流连接成一个流
         */
        List<String> list1 = Arrays.asList("aaa","bbb","ccc","ddd");
        list1.stream()
                .map((str) -> str.toUpperCase())
                .forEach(System.out::println);
        System.out.println("--------------使用map取出对象的名字-------------------------");
        employees.stream()
                .map((emp) -> emp.getName())
                .forEach(System.out::println);



    }

}
