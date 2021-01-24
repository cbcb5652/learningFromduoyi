package 算法.中等;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。
 *
 * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
 *
 *
 * 示例:
 *
 * 输入："23"
 * 输出：["ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"].
 *
 */
public class 电话号码的字母组合 {
    private static final HashMap<Integer, String> map;
    static{
        map = new HashMap<>();
        map.put(2,"abc");
        map.put(3,"def");
        map.put(4,"ghi");
        map.put(5,"jkl");
        map.put(6,"mno");
        map.put(7,"pqrs");
        map.put(8,"tuv");
        map.put(9,"wxyz");
    }

    public static List<String> letterCombinations(String digits) {

        List<String> list = new ArrayList<>();

        if (digits.length() == 0){
            return list;
        }

        String pattern = "";
        backtracking(pattern,digits,0,list);

        return list;
    }

    private static void backtracking(String pattern, String digits, int flag,List<String>   list) {

        if (flag == digits.length()){
            // 回溯的条件是 flag指向最后一个数字
            list.add(pattern);
            return;
        }

        // 先获得数字对应的字符串，然后遍历它
        String str = map.get(digits.charAt(flag) - '0');

        for (int i = 0; i < str.length(); i++) {
            backtracking(pattern + str.charAt(i) ,digits,flag + 1 ,list);
        }
    }

    public static void main(String[] args) {
        String num = "";
        System.out.println(letterCombinations(num));
    }

}
