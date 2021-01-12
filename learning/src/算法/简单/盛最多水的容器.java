package 算法.简单;

import java.util.Arrays;

public class 盛最多水的容器 {

    public static void main(String[] args) {
        int[] arr = {1,2,1};
        System.out.println(maxArea(arr));
    }

    public static int maxArea(int[] height) {

        if (height == null || height.length == 0){
            return 0;
        }

        int maxArea = 0;

        for (int i = 0; i < height.length; i++) {
            for (int j = i; j < height.length; j++) {
                int hight =height[i] > height[j] ? height[j] : height[i];
                int area = hight * (j - i);
                maxArea = area > maxArea ? area: maxArea;
            }
        }

        return maxArea;
    }

}
