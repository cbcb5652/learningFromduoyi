package 算法.简单;

public class 最长公共前缀 {

    public static String longestCommonPrefix(String[] strs) {
        // 排除空的测试用例
        if (strs.length == 0){
            return "";
        }

        String result = strs[0];

        if (strs.length == 1){
            return strs[0];
        }

        for (int i = 0; i < strs.length; i++) {
            // 如果不以这个result为开头，从后往前切割，跟自己比较的时候不会进入这个if
            if (!strs[i].startsWith(result)){
                result = result.substring(0,result.length() -1);
                i --;
            }

        }

        return result;
    }

}
