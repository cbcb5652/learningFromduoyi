package 算法.中等;

import java.util.Arrays;

/**
 * 给定一个包括n 个整数的数组nums和 一个目标值target。找出nums中的三个整数，使得它们的和与target最接近。返回这三个数的和。假定每组输入只存在唯一答案。
 *
 * 示例：
 *
 * 输入：nums = [-1,2,1,-4], target = 1
 * 输出：2
 * 解释：与 target 最接近的和是 2 (-1 + 2 + 1 = 2) 。
 *
 */
public class 最接近的三数之和 {

    public static void main(String[] args) {
        int[] num = {-1,2,1,-4};
        int target = 1;
        System.out.println(threeSumClosest(num, target));

    }

    public static int threeSumClosest(int[] nums, int target) {

        int num = 0;
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < nums.length; i++) {
            for (int j = i +1 ; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    int all = nums[i] + nums[j] + nums[k];
                    if (Math.abs(all - target) < Math.abs(min)){
                        min = Math.abs(all - target);
                        num = all;
                    }
                }

            }
        }
        return num;
    }


    public static int threeSumClosest1(int[] nums, int target) {

            int sum = 0;
            int len = nums.length;
            if (len == 3){
                return nums[0] + nums[1] + nums[2];
            }

            // 排序
            Arrays.sort(nums);
            // 双指针
            int start = 0;
            int end = len -1;

            double sub = Integer.MAX_VALUE; // 存储最小差值，定义double 类型防止运算后超出 int 类型取值范围
            int fixIndex = 0;
            for (int i = 0; i < len -2 ; i++) {
                fixIndex = i;
                start = i + 1;
                end = len -1;
                while (start < end){
                    sum = nums[start] + nums[fixIndex] + nums[end] -target;    //三数和与target的差值
                    if (sub == 0){
                        return target;
                    }
                    sub = Math.abs(sub) > Math.abs(sum) ?sum : sub; //sub存储最小与target最小差值
                    if (sum > 0){
                        end --;
                    }else {
                        start++;
                    }

                }
            }

            return (int)(target + sub);
    }

}
