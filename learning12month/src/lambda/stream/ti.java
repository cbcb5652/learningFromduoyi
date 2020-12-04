package lambda.stream;

import lambda.filter.Employee;

import java.util.Arrays;
import java.util.List;

public class ti {

    private static List<Employee> employees = Arrays.asList(
            new Employee("张三",18,99.99, Employee.Status.BUSY),
            new Employee("李四",20,55.99, Employee.Status.FREE),
            new Employee("王五",12,66.99, Employee.Status.BUSY),
            new Employee("赵六",21,77.99, Employee.Status.VOCATION),
            new Employee("田七",23,88.99, Employee.Status.BUSY)
    );

    public static void main(String[] args) {

        /**
         * 1. 给定一个数字列表，返回列表中的所有数字的平方
         */
        Integer[] nums = new Integer[]{1,2,3,4,5};
        Arrays.stream(nums).map((x) -> x*x).forEach(System.out::println);

        /**
         * 2. 怎么用map和reduce 计算有多少个Employee呢
         */
        System.out.println(employees.stream().map((e) -> 1).reduce(Integer::sum).get());

        /**
         * 3.
         */

    }
}
