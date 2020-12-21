package lambda.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * 题目： 请按照给出的数据，找出同时满足以下条件的用户，也即以下条件全部满足
 *       偶数ID且年龄大于24
 *       且用户名转为大写
 *       且用户字母顺序倒序
 *       只输出一个用户名
 */
public class StreamDemo {
    public static void main(String[] args) {
        User u1 = new User(11,"小a",23);
        User u2 = new User(12,"小b",24);
        User u3 = new User(13,"小c",22);
        User u4 = new User(14,"小d",28);
        User u5 = new User(15,"小e",26);

        List<User> list = Arrays.asList(u1,u2,u3,u4,u5);

       list.stream().filter(s -> { return s.getAge() >= 24 && s.getId() %2 == 0 ;})
               .map( user -> { return user.getUsername().toUpperCase(); } )
               .sorted((x,y) ->{ return y.compareTo(x); })
               .limit(1).forEach(System.out::println);


       /* Function<String,Integer> function =(s) ->{ return 1024;};
        System.out.println(function.apply("abc"));*/
    }

}
