package my.linkin.dp;


import java.util.*;

/**
 * 找零钱算法
 * 有面值为1,2,5,11,20,50 问最少需要多少个硬币可以找出总值为N的零钱
 */
public class ChangeAlgorithm {
    /**
     * 设面值数组为v[] = [1,2,4,11,20,50];
     * 各个面值对应的个数为 n[] = [n1,n2,n4,n11,n20m n50];
     * 求 min(sum(n1,n2,n11,n20,n50)), 记为 min(N)
     * 其中，存在以下约束条件：
     * n1 * 1 + n2 * 2 + n4 * 4 + n11 * 11 + n20 * 20 + n50 * 50 = N
     * <p>
     * min(N) = min{min(N-1) +1 ， min(N-2) + 1, min(N-4) + 1...}
     * min(N - 1) = min{min(N - 2) + 1, min(N-3) + 1...}
     * ...
     * ...
     * ,,,
     * min(4) = min{min(4-1)+1, min(4-2)+1, min(4-4)+ 1}=1
     * min(3) = min{min(3-1) + 1, min(3-2)+1} = 2
     * min(2) = 1
     * min(1) = 1
     * min(0) = 0
     */

    // 缓存结果 即缓存 N的面值需要多少个coins
    private static Map<Integer, Integer> cache = new HashMap<>();
    // 面值结果缓存


    /*
     * 硬币面值大小
     * */
    private static List<Integer> coins = Arrays.asList(1, 2, 4, 11, 20, 50);

    public static Integer min(int n) {
        if (n == 1 || n == 2 || n == 4 || n == 11 || n == 20 || n == 50) {
            return 1;
        }
        if (cache.containsKey(n)) {
            return cache.get(n);
        }
        List<Integer> result = new ArrayList<>();
        for (Integer c : coins) {
            if (n > c) {
                result.add(min(n - c) + 1);
            }
        }
        int min = result.parallelStream().sorted(Comparator.comparing(Integer::intValue)).findFirst().get();
        if (!cache.containsKey(n)) {
            cache.put(n, min);
        }
        return min;
    }
    public static void main(String[] args) {
        System.out.println("coins:");
        coins.stream().forEach(e -> System.out.print(e + ","));
        System.out.println();
//        System.out.println("when n = 12, need coins: " + min(12));
//        System.out.println("when n = 101, need coins: " + min(101));
//        System.out.println("when n = 150, need coins: " + min(150));
//        System.out.println("when n = 23, need coins: " + min(23));
//        System.out.println("when n = 34, need coins: " + min(34));
//        System.out.println("when n = 99, need coins: " + min(99));
        System.out.println("when n = 199, need coins: " + min(199));
        cache.entrySet().forEach(e -> System.out.println(e.getValue()));
//        System.out.println("when n = 51, need coins: " + min(51));
//        System.out.println("when n = 22, need coins: " + min(22));
    }
}
