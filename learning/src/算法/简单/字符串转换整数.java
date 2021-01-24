package 算法.简单;


public class 字符串转换整数 {

    public static void main(String[] args) {
        System.out.println(myAtoi("-   234"));

    }

    public static int myAtoi(String s) {

        String noEmpty = s.replaceAll(" ", "");

        if (noEmpty.length() == 0){
            return 0;
        }


        char[] chars = noEmpty.toCharArray();

        int res = 0;
        int i = 0;
        int flag = 1;
        if (String.valueOf(chars[i]).equals("-")){
            flag = -1;
        }

        if (String.valueOf(chars[i]).equals("-") || String.valueOf(chars[i]).equals("+")){
            i++;
        }


        while (i < chars.length && isNum(String.valueOf(chars[i]))){
            int r = chars[i] - '0';

            if (res > Integer.MAX_VALUE /10 || (res == Integer.MAX_VALUE /10 && r > 7)){
                return flag > 0?Integer.MAX_VALUE   : Integer.MIN_VALUE ;
            }
            res = res *10 +r;
            i++;
        }
        return flag > 0?res :-res;
    }

    public static  boolean isNum(String str){

        try{
            Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }



}
