package 算法.简单;

import java.util.HashMap;
import java.util.Map;

public class 罗马数字转整数 {

    public static void main(String[] args) {
        System.out.println(romanToInt("MCMXCIV"));
    }

    public static int romanToInt(String s) {
        // 定义好数字对应的整数
        Map<String,Integer> map = new HashMap<>();
        map.put("I",1);
        map.put("V",5);
        map.put("X",10);
        map.put("L",50);
        map.put("C",100);
        map.put("D",500);
        map.put("M",1000);
        //---------------
        map.put("IV",4);
        map.put("IX",9);
        map.put("XL",40);
        map.put("XC",90);
        map.put("CD",400);
        map.put("CM",900);

        char[] chars = s.toCharArray();
        Integer sum = 0;

        for (int i = 0; i < chars.length; i++) {
            String str = "";
            if (i < chars.length -1){
                str = String.valueOf(chars[i]) + String.valueOf(chars[i+1]);
            }

            // 如果存在该值,相加，并将下标往后移
            if (map.containsKey(str)){
                sum += map.get(str);
                i++;
            }else {
                sum += map.get(String.valueOf(chars[i]));
            }
        }

        return sum;
    }

}
