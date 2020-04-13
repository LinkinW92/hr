package my.linkin.dp;

import java.util.HashMap;
import java.util.Map;

/**
 * 有N阶台阶，每一步可以走1步台阶或者2步台阶，求出走到第N阶台阶的方法数。
 */
public class StepAlgorithm {

    private static Map<Integer, Integer> cache = new HashMap<>();

    public static int total(int N) {
        if (N == 1) {
            return 1;
        }
        if(N==2) {
            return 2;
        }
        Integer n1 = 0, n2 = 0, nn = 0;
        if (cache.containsKey(N)) {
            return cache.get(N);
        }
        if (cache.containsKey(N - 1)) {
            n1 = cache.get(N - 1);
        }
        if (cache.containsKey(N - 2)) {
            n2 = cache.get(N - 2);
        }
        if (n1 == 0) {
            n1 = total(N - 1);
            cache.put(N - 1, n1);
        }
        if (n2 == 0) {
            n2 = total(N - 2);
            cache.put(N - 2, n2);
        }
        nn = total(N - 1) + total(N - 2);
        cache.put(N, nn);
        return nn;
    }

    public static void main(String[] args) {
        System.out.println(total(5));
    }
}
