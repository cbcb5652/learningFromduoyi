package lambda.stream;

import lambda.filter.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class TestStreamAPI2 {
    private static List<Employee> employees = Arrays.asList(
            new Employee("张三",18,99.99, Employee.Status.BUSY),
            new Employee("李四",20,55.99, Employee.Status.FREE),
            new Employee("王五",12,66.99, Employee.Status.BUSY),
            new Employee("赵六",21,77.99, Employee.Status.VOCATION),
            new Employee("田七",23,88.99, Employee.Status.BUSY)
    );
    public static void main(String[] args) {
        /**
         *  查找与匹配
         *  allMatch -- 检查是否匹配所有元素
         *  anyMatch -- 检查是否至少匹配一个元素
         *  noneMatch -- 检查是否没有匹配所有元素
         *  findFirst -- 返回第一个元素
         *  count -- 返回流中元素的总个数
         *  max -- 返回流中最大值
         *  min -- 返回流中最小值
         */

        // allMatch
        System.out.println(employees.stream().allMatch((e) -> e.getStatus().equals(Employee.Status.BUSY)));
        // anyMatch
        System.out.println(employees.stream().anyMatch((e) -> e.getStatus().equals(Employee.Status.BUSY)));
        // noneMatch
        System.out.println(employees.stream().noneMatch((e) -> e.getStatus().equals(Employee.Status.BUSY)));
        // findFirst
        Optional<Employee> op = employees.stream().sorted((e1, e2) -> Double.compare(e1.getSlary(), e2.getSlary())).findFirst();
        System.out.println(op.get());
        // findAny
        Optional<Employee> op2 = employees.parallelStream().filter((e) -> e.getStatus().equals(Employee.Status.FREE)).findAny();
        System.out.println(op2.get());
        // count
        System.out.println(employees.stream().count());
        // max
        Optional<Employee> op3 = employees.stream().max((e1, e2) -> Double.compare(e1.getSlary(), e2.getSlary()));
        System.out.println(op3.get());
        // min
        Optional<Double> op4 = employees.stream().map(Employee::getSlary).min(Double::compareTo);
        System.out.println(op4.get());

        /**
         * 规约
         * reduce(T identity,BinaryOperator) / reduce(BinaryOperator)  - 可以将流中元素反复结合起来，得到一个值
         */
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9);
        Integer sum = list.stream()
                .reduce(0,(x,y) -> x+y);
        System.out.println(sum);

        Optional<Double> op5 = employees.stream().map(Employee::getSlary).reduce(Double::sum);
        System.out.println(op5.get());

        // 收集 collection 将流转换为其他形式。接收一个Collection接口的实现，用于给Stream中元素汇总的方法
        employees.stream().map(Employee::getName).collect(Collectors.toList()).forEach(System.out::println);
        employees.stream().map(Employee::getName).collect(Collectors.toSet()).forEach(System.out::println);
        employees.stream().map(Employee::getName).collect(Collectors.toCollection(HashSet::new)).forEach(System.out::println);
        // 总数
        System.out.println(employees.stream().collect(Collectors.counting()));
        // 平均值
        System.out.println(employees.stream().collect(Collectors.averagingDouble(Employee::getSlary)));
        // 总和
        System.out.println(employees.stream().collect(Collectors.summingDouble(Employee::getSlary)));
        // 最大值
        Optional<Employee> op6 = employees.stream().collect(Collectors.maxBy((e1, e2) -> Double.compare(e1.getSlary(), e2.getSlary())));
        System.out.println(op6.get());
        // 最小值
        System.out.println(employees.stream().map(Employee::getSlary).collect(Collectors.minBy(Double::compare)).get());
        /**
         * 分组
         */
        Map<Employee.Status, List<Employee>> map = employees.stream().collect(Collectors.groupingBy(Employee::getStatus));
        map.forEach((k,v) ->{
            System.out.println("key:"+k+",value:"+v);
        });
        // 多级分组
        Map<Employee.Status, Map<String, List<Employee>>> map1 = employees.stream().collect(Collectors.groupingBy(Employee::getStatus, Collectors.groupingBy((e) -> {
            if (((Employee) e).getAge() <= 35) {
                return "青年";
            } else if (((Employee) e).getAge() <= 50) {
                return "中年";
            } else {
                return "老年";
            }
        })));
        System.out.println(map1);

        // 分区
        Map<Boolean, List<Employee>> map3 = employees.stream().collect(Collectors.partitioningBy((e) -> e.getSlary() > 8000));
        System.out.println(map3);

        // 获取多种值
        DoubleSummaryStatistics dss = employees.stream().collect(Collectors.summarizingDouble(Employee::getSlary));
        System.out.println(dss.getSum());
        System.out.println(dss.getAverage());
        System.out.println(dss.getMax());

        // 收集
        String str = employees.stream().map(Employee::getName).collect(Collectors.joining(",","!!!","~~~"));
        System.out.println(str);

    }

}
