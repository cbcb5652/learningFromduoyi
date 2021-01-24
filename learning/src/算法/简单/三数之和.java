package 算法.简单;

import java.util.*;

public class 三数之和 {


    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        int n = nums.length;
        Set<List<Integer>> res = new HashSet<>();

        for (int i = 0; i < n; i++) {
            int l = i + 1;
            int r = n - 1;
            while (l < r) {
                if (nums[i] + nums[l] + nums[r] == 0) {
                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    l++;
                    r--;
                } else if ((nums[i] + nums[l] + nums[r] < 0)) {
                    l++;
                } else {
                    r--;
                }

            }
        }

        List<List<Integer>> ans = new ArrayList<>();
        ans.addAll(res);
        return ans; 
    }

}
