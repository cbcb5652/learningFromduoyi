package 算法.简单;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class 整数转罗马数字 {

    public static void main(String[] args) {
        System.out.println(intToRoman(1994));
        System.out.println(intToRoman(3));
        System.out.println(intToRoman(4));
        System.out.println(intToRoman(9));
    }

    public static String intToRoman(int num) {

        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");


        // 如果存在额外的值 直接返回
        if (map.containsKey(num)) {
            return map.get(num);
        }

        int chushu = Integer.MAX_VALUE;
        int yushu = Integer.MAX_VALUE;
        String str = "";

        while (yushu != 0){

            for (Integer key : map.keySet()){

                chushu = num / key;
                yushu = num % key;
                // 除数太大跳过
                if (yushu == num){
                    continue;
                }
                //除出来的是几，就循环拼接几次
                for (int i = 0; i < chushu; i++) {
                    str += map.get(key);
                }

                // 如果刚好是那六种情况直接拼接上return
                if (map.containsKey(yushu)){
                    str += map.get(yushu);
                    return str;
                }

                //六种情况之外就将余数复制给num，继续循环
                num = yushu;
            }
        }

        return str;
    }
}
