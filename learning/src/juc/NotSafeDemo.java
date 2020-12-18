package juc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 集合类的不安全
 */
public class NotSafeDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        for (int i = 1; i < 30; i++) {
            new Thread(() ->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(list);
            },String.valueOf(i)).start();
        }

    }

}
