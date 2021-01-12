package 算法.简单;

/**
 * 加入输入为：LEETCODEISHIRING   4
 *
 * 按照行标志如下:
 *  0           6           12
 *  1       5   7       11  13
 *  2   4       8   10      14
 *  3           9           15
 *
 *  按照下标如下：
 *  0           0           0
 *  1       1   1       1   1
 *  2   2       2   2       2
 *  3           3           3
 *
 *  其实只需去读取字符串的一个个字符，然后定义一个长度为4的字符数组，从上往下从下往上分别给对应数组添加进去就行了
 *  ==记得过滤掉一些不必要的情况：
 *      - 字符串为空
 *      - 字符串长度等于1
 *      - 行长度等于1
 *      - 行长度大于等于字符串长度
 *  ==
 *
 */
public class Z字形变换 {

    public static void main(String[] args) {
        String str = "AB";
        System.out.println(convert(str, 3));

    }

    public static  String convert(String s, int numRows) {

        if (s == null || s.equals("")){
            return "";
        }

        if (s.length() == 1 || numRows == 1 || numRows >= s.length()){
            return s;
        }

        //按照下表格式返回数据
        String[] strings = new String[numRows];
        char[] chars = s.toCharArray();
        // 转换标志位
        boolean flag = false;
        // 数组下标
        int temp = 0;
        for (int i = 0; i < chars.length; i++) {
            if (flag){
                strings[temp--] += chars[i]+"";
            }else {
                strings[temp++] += chars[i]+"";
            }

            if (temp == numRows -1 ){
                flag = true;
            }

            if (temp == 0){
                flag = false;
            }
        }

        StringBuffer buffer = new StringBuffer();
        for (String string : strings) {
            buffer.append(string.replaceAll("null",""));
        }

        return buffer.toString();
    }

}
