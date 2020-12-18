package lambda.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class main {

    public static void main(String[] args) {
        List<Apple> list = new ArrayList<>();
        Apple apple = new Apple();
        apple.setColor("red");
        apple.setWeight(50);

        Apple apple1 = new Apple();
        apple1.setColor("green");
        apple1.setWeight(100);

        Apple apple2 = new Apple();
        apple2.setColor("green");
        apple2.setWeight(150);

        Apple apple3 = new Apple();
        apple3.setColor("red");
        apple3.setWeight(101);

        Apple apple4 = new Apple();
        apple4.setColor("green");
        apple4.setWeight(1001);

        list.add(apple);
        list.add(apple1);
        list.add(apple2);
        list.add(apple3);
        list.add(apple4);

        // 返回整个对象
        List<Apple> apples = list.stream().filter(new Predicate<Apple>() {
            @Override
            public boolean test(Apple apple) {
                return apple.getColor() == "green" && apple.getWeight() >= 100;
            }
        }).collect(Collectors.toList());
        apples.forEach(System.out::println);

        // 只返回某个值
        List<String> strs = list.stream().filter(new Predicate<Apple>() {
            @Override
            public boolean test(Apple apple) {
                return apple.getColor() == "green" && apple.getWeight() >= 100;
            }
        }).map(ap -> {
            return ap.getColor();
        }).collect(Collectors.toList());
        strs.forEach(System.out::println);

        // 返回两个字段封装在Map中
        Map<Integer, String> mapApp = list.stream().filter(new Predicate<Apple>() {
            @Override
            public boolean test(Apple apple) {
                return apple.getColor() == "green" && apple.getWeight() >= 100;
            }
        }).collect(Collectors.toMap(Apple::getWeight, Apple::getColor));
        mapApp.forEach((k,v) ->{
            System.out.println("weight:"+k+",color:"+v);
        });





    }

}
