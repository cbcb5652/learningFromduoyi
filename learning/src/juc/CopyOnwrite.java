package juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnwrite {

    public static void main(String[] args) {
        List<String> list = new CopyOnWriteArrayList();
        List<String> list1 = Collections.synchronizedList(new ArrayList<>());

        HashSet hashSet = new HashSet();
        hashSet.add(1);
    }

}
