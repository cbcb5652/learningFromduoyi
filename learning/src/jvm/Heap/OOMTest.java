package jvm.Heap;

import java.util.ArrayList;

/**
 *
 */
public class OOMTest {

    public static void main(String[] args) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        while (true){
            arrayList.add(11);
        }

    }

}
