package 算法.简单;

/**
 * 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 */
public class 整数反转 {

    public static void main(String[] args) {
        System.out.println(reverse(-1534236469));
        //9646324351
    }

    public static int reverse(int x) {
        if (x < 10 && x >= 0){
            return x;
        }

        // 符号标记
        boolean flag = false;
        if (x < 0){
            flag = true;
            x = -x;
        }

        String str = String.valueOf(x);

        int length = str.length();
        int cut = 0;

        for (int i = str.length()-1; i >= 0; i--) {
            if (str.substring(i -1,length).equals("0")){
                cut ++;
                continue;
            }
            str = str.substring(0,length -cut);
            break;
        }

        char[] chars = str.toCharArray();
        int peer = 1;
        long Num = 0;


        for (int i = 0 ; i < chars.length; i++) {

            int ch = Integer.parseInt(String.valueOf(chars[i]));
            Num += ((long)ch *(long) peer);

            peer *= 10;
        }


        if (flag){
            Num = -Num;
        }

        return (int) Num;
    }

}
