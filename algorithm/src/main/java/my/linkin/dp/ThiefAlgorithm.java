package my.linkin.dp;


import java.util.HashMap;
import java.util.Map;

/**
 * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
 * <p>
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你在不触动警报装置的情况下，能够偷窃到的最高金额。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,1]
 * 输出: 4
 * 解释: 偷窃 1 号房屋 (金额 = 1) ，然后偷窃 3 号房屋 (金额 = 3)。
 *      偷窃到的最高金额 = 1 + 3 = 4 。
 * 示例 2:
 * <p>
 * 输入: [2,7,9,3,1]
 * 输出: 12
 * 解释: 偷窃 1 号房屋 (金额 = 2), 偷窃 3 号房屋 (金额 = 9)，接着偷窃 5 号房屋 (金额 = 1)。
 *      偷窃到的最高金额 = 2 + 9 + 1 = 12 。
 */
public class ThiefAlgorithm {

    public static int rob(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        return robCache(nums, nums.length, new HashMap<>());
    }

    public static int robCache(int[] nums, int tail, Map<Integer, Integer> cache) {
        int maxValue = 0;
        if (cache.containsKey(tail)) {
            return cache.get(tail);
        }
        if (tail == 1) {
            maxValue = nums[0];
        }
        if (tail == 2) {
            maxValue = nums[0] > nums[1] ? nums[0] : nums[1];
        }
        if (tail == 3) {
            maxValue = nums[0] + nums[2] > nums[1] ? nums[0] + nums[2] : nums[1];
        }

        if (tail > 3) {
            int f0 = robCache(nums, tail - 2, cache) + nums[tail - 1], f1 = robCache(nums, tail - 1, cache);
            maxValue = f0 > f1 ? f0 : f1;
        }
        if (!cache.containsKey(tail)) {
            cache.put(tail, maxValue);
        }
        return maxValue;
    }


    public static void main(String[] args) {
        int[] nums = {1, 1, 1, 1};
        System.out.println(rob(nums));
    }
}
