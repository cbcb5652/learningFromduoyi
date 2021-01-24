package 算法.简单;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class 有效的括号 {

    public static boolean isValid(String s) {

        Map<String ,String> map = new HashMap<>();
        map.put("(",")");
        map.put("<",">");
        map.put("[","]");
        map.put("{","}");

        int i = 0;
        Stack<String> stack = new Stack<>();

        while (i < s.length()){

            String deep = s.substring(i, i + 1);

            if (stack.isEmpty()){
                i++;
                stack.add(deep);
                continue;
            }

            // 如果这个字符跟栈中的匹配，则弹出

            System.out.println(stack.peek());
            System.out.println(map.get(deep));

            if (stack.peek().equals(map.get(deep))){
                stack.pop();
            }else {
                // 如果栈中为空,或者不匹配，则直接插入
                stack.add(deep);
            }
            i++;
        }

//        System.out.println(stack);

        return stack.isEmpty() ? true: false;
    }

    public static void main(String[] args) {
        System.out.println(isValid("()"));

        

    }



}
