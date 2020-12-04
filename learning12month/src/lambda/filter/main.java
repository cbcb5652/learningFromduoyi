package lambda.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class main {

    public static void main(String[] args) {

        List<Employee> employees = Arrays.asList(
                new Employee("张三",18,99.99),
                new Employee("李四",20,55.99),
                new Employee("王五",12,66.99),
                new Employee("赵六",21,77.99),
                new Employee("田七",23,88.99)
        );

        // 测试筛选方法
        List<Employee> list = filterEmployee(employees,new FilterEmployeeByAge());
        list.forEach(System.out::println);
        System.out.println("----------------------------------------------------");
        List<Employee> list1 = filterEmployee(employees, new FilterEmployeeBySalary());
        list1.forEach(System.out::println);

        // 优化方式2 匿名内部类，不需要再写类
        List<Employee> list2 = filterEmployee(employees, new MyPredicate<Employee>() {
            @Override
            public boolean test(Employee employee) {
                return employee.getSlary() <= 5000;
            }
        });
        list2.forEach(System.out::println);

        // 优化方式3 使用lambda表达式
        List<Employee> list3 = filterEmployee(employees, (e) -> e.getSlary() <= 5000);
        list3.forEach(System.out::println);

        // 优化方式4 使用流的方式
        employees.stream().filter((e)-> e.getSlary() >= 5000).limit(3).forEach(System.out::println);
        employees.stream().map(Employee::getName).forEach(System.out::println);




    }

    // 一开始的过滤
    public static List<Employee> filterEmployees(List<Employee> list){
        List<Employee> emps = new ArrayList<>();
        for (Employee emp : emps) {
            if (emp.getAge() >= 20){
                emps.add(emp);
            }
        }
        return emps;
    }



    // lambda之后的过滤
    public static List<Employee> filterEmployee(List<Employee> list,MyPredicate<Employee>mp){
        List<Employee> emps = new ArrayList<>();
        for (Employee employee : list) {
            if (mp.test(employee)){
                emps.add(employee);
            }
        }
        return emps;
    }



}
