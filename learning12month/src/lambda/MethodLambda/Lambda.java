package lambda.MethodLambda;

import lambda.filter.Employee;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 若Lambda体中已经有实现了，我们可以使用方法引用（可以理解为Lambda表达式的另一种方式）
 * 主要有三种语法格式：
 *  对象::实例方法
 *  类::静态方法名
 *  类::实例方法名
 *
 *  注意：
 *  ①Lambda 体中调用方法的参数列表与返回值类型，要与函数式接口中抽象方法的函数列表的返回值类型保持一致！
 *  ②若Lambda 参数列表中的第一参数是实例方法的调用者，而第二个参数是实例方法的参数时，可以使用ClassName::method
 *
 *  构造器引用：
 *   格式：ClassName:new
 *
 *   注意：需要调用的构造器的参数列表要与函数式接口中抽象方法的参数列表保持一致！
 *
 *  数组引用：
 *      Type::new
 */
public class Lambda {

    public static void main(String[] args) {
        Consumer<String> con = (x) -> System.out.println(x);

        BiPredicate<String,String> bp = (x,y) -> x.equals(y);
        // ②调用第一参数的方法，传入第二参数
        BiPredicate<String,String> bp2 = String::equals;

        // 构造器引用方式(无参数的构造器)   --->  构造器列表与参数列表保持一致
        Supplier<Employee> sup2 = Employee::new;
        // 调用两个参数的构造器
        Function<Integer,Employee> fun = (x) -> new Employee(x);
        Function<Integer,Employee> fun2 = Employee::new;
        Employee emp = fun2.apply(101);
        System.out.println(emp.getAge());

        // 数组引用
        Function<Integer,String[]> fun3 = (x) -> new String[x];
        String[] strs = fun3.apply(10);
        System.out.println(strs.length);

        Function<Integer,String[]> fun4 = String[]::new;
        String[] strs2 = fun4.apply(20);
        System.out.println(strs2.length);


    }


}
