package 算法.简单;


public class 最长回文子串 {

    public static void main(String[] args) {

        final String str1 = "babad";
        final String str2 = "cbbd";

        System.out.println(longestPalindrome("esbtzjaaijqkgmtaajpsdfiqtvxsgfvijpxrvxgfumsuprzlyvhclgkhccmcnquukivlpnjlfteljvykbddtrpmxzcrdqinsnlsteonhcegtkoszzonkwjevlasgjlcquzuhdmmkhfniozhuphcfkeobturbuoefhmtgcvhlsezvkpgfebbdbhiuwdcftenihseorykdguoqotqyscwymtjejpdzqepjkadtftzwebxwyuqwyeegwxhroaaymusddwnjkvsvrwwsmolmidoybsotaqufhepinkkxicvzrgbgsarmizugbvtzfxghkhthzpuetufqvigmyhmlsgfaaqmmlblxbqxpluhaawqkdluwfirfngbhdkjjyfsxglsnakskcbsyafqpwmwmoxjwlhjduayqyzmpkmrjhbqyhongfdxmuwaqgjkcpatgbrqdllbzodnrifvhcfvgbixbwywanivsdjnbrgskyifgvksadvgzzzuogzcukskjxbohofdimkmyqypyuexypwnjlrfpbtkqyngvxjcwvngmilgwbpcsseoywetatfjijsbcekaixvqreelnlmdonknmxerjjhvmqiztsgjkijjtcyetuygqgsikxctvpxrqtuhxreidhwcklkkjayvqdzqqapgdqaapefzjfngdvjsiiivnkfimqkkucltgavwlakcfyhnpgmqxgfyjziliyqhugphhjtlllgtlcsibfdktzhcfuallqlonbsgyyvvyarvaxmchtyrtkgekkmhejwvsuumhcfcyncgeqtltfmhtlsfswaqpmwpjwgvksvazhwyrzwhyjjdbphhjcmurdcgtbvpkhbkpirhysrpcrntetacyfvgjivhaxgpqhbjahruuejdmaghoaquhiafjqaionbrjbjksxaezosxqmncejjptcksnoq"));

    }

    public static String longestPalindrome(String s) {

        if (s == null || s.length() == 0){
            return "";
        }

        int length = s.length();
        if (length == 1){
            return s;
        }

        int maxLenght = 0;
        String maxStr = "";


        for (int i = 0; i <= length; i++) {
            for (int j = i; j <= length; j++) {

                String cutStr = s.substring(i, j);
                if (isCircle(cutStr)){
                    int strLength = j -i;
                    if (strLength >= maxLenght){
                        maxStr = cutStr;
                        maxLenght = strLength;
                    }

                }
            }
        }
        return maxStr;
    }


    // 判断回文字符串
    public static  boolean isCircle(String str){

        int mid = str.length() /2;
        int length = str.length() - 1;

        char[] chars = str.toCharArray();

        for (int i = 0; i < mid; i++) {
            if (chars[i] != chars[length -i]){
                return false;
            }
        }

        return true;
    }


    public static String longestPalindromeCopy(String s) {

        if (s == null || s.length() == 0) {
            return "";
        }

        int res = 1;
        int ll = 0;
        int rr = 0;

        for (int i = 0; i < s.length(); i++) {

            int l = i - 1;
            int r = i + 1;
            while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                int len = (r - l + 1);
                if (len > res) {
                    res = len;
                    ll = l;
                    rr = r;
                }
                l--;
                r++;
            }

            l = i;
            r = i + 1;
            while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                int len = (r - l + 1);
                if (len > res) {
                    res = len;
                    ll = l;
                    rr = r;
                }
                l--;
                r++;
            }
        }
        return s.substring(ll, rr + 1);
    }

}
