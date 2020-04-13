package my.linkin.dp;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 0-1背包问题,有n个物品，它们有各自的体积和价值，现有给定容量的背包，如何让背包里装入的物品具有最大的价值总和？
 */
public class Pack01Algorithm {

    /*
     * 给定容量N的背包,求其能装的最大价值, 设f(i) 表示第n件物品放不放入背包，f(i)=1表示放入，否则不放入,那么有
     * f(1) * 2 + f(2) * 3 + f(3) * 4 + f(4) * 5 + f(5) * 9 <= N,
     * 在此条件下求：
     * max(fi*3 + f(2) * 4 + f(3) * 5 +f(4) * 8 + f(5) * 10)， 记为max(N)
     * 缩小问题：假设第五件物品放入的情况下有
     * f(1) * 2 + f(2) * 3 + f(3) * 4 + f(4) * 5 <= N - 9
     * 不放入情况下有：
     * f(1) * 2 + f(2) * 3 + f(3) * 4 + f(4) * 5 <= N
     * 即求max(max(N), max(N-4) + 10)
     *
     * */


    private static Map<Integer, Integer> map = new HashMap<>();
    private static int[] weights = {2, 3, 4, 5, 9};
    private static int[] values = {3, 4, 5, 8, 10};

    static {
        map.put(2, 3);
        map.put(3, 4);
        map.put(4, 5);
        map.put(5, 8);
        map.put(9, 10);
    }

    private static List<Integer> result = new ArrayList<>();

    /*
     * 给定容量N的背包，求其最大价值
     * */
    public static int maxValue(int N, int index) {
        int[] w = {2, 3, 4, 5, 9};
        int[] v = {3, 4, 5, 8, 10};
        int bagV = 8;//背包大小
        int[][] dp = new int[5][9];

        for (int i = 1; i <= 4; i++) {
            for (int j = 1; j <= bagV; j++) {
                if (j < w[i])
                    dp[i][j] = dp[i - 1][j];
                else
                    dp[i][j] = dp[i - 1][j] > dp[i - 1][j - w[i]] + v[i] ? dp[i - 1][j] : dp[i - 1][j - w[i]] + v[i];
            }
        }

        //动态规划表的输出
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(dp[i][j] + ",");
            }
            System.out.println();
        }

        return 0;
    }

    public static void main(String[] args) {
        maxValue(4, 4);
        result.forEach(e -> System.out.println(e));
    }
}
