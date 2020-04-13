package my.linkin.algorithm;


import java.util.Arrays;
import java.util.Comparator;

/**
 * 0-1矩阵
 * leetCode：542
 */
public class Metrix01 {
    public static int[][] updateMatrix(int[][] matrix) {
        int i = matrix.length, j = matrix[0].length;
        int[][] temp = new int[i + 2][j + 2];
        for (int m = 0; m < i + 2; m++) {
            temp[m][0] = -1;
            temp[m][j + 1] = -1;
        }
        for (int n = 0; n < j + 2; n++) {
            temp[0][n] = -1;
            temp[i + 1][n] = -1;
        }
        for (int m = 1; m < j + 1; m++) {
            for (int n = 1; n < i + 1; n++) {
                temp[n][m] = matrix[n - 1][m - 1];
            }
        }
        int cnt = 0;
        for (int m = 1; m < i + 1; m++) {
            for (int n = 1; n < j + 1; n++) {
                if (temp[m][n] == 0) continue;
                if (temp[m][n + 1] != 0 && temp[m][n - 1] != 0 && temp[m - 1][n] != 0 && temp[m + 1][n] != 0) {
                    temp[m][n] = -1;
                    cnt++;
                }
            }
        }
        int index = 1;
        while (cnt > 0) {
            for (int m = 1; m < i + 1; m++) {
                for (int n = 1; n < j + 1; n++) {
                    if (temp[m][n] != -1) continue;
                    if (temp[m][n + 1] == -1 && temp[m][n - 1] == -1 && temp[m + 1][n] == -1 && temp[m - 1][n] == -1)
                        continue;
                    int min = min(temp[m][n + 1],
                            temp[m][n - 1],
                            temp[m + 1][n],
                            temp[m - 1][n]);
                    if (min != index) {
                        continue;
                    }
                    matrix[m - 1][n - 1] = min + 1;
                    temp[m][n] = min + 1;
                    cnt--;
                }
            }
            index++;
        }
        return matrix;
    }

    private static int min(int east, int west, int south, int north) {
        return Arrays.asList(east, west, south, north).parallelStream().filter(e -> e > 0).min(Comparator.comparing(Integer::intValue)).get();
    }

    private static void print(int[][] temp) {
        System.out.println("-------------");
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                System.out.print(temp[i][j] + ",");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 0, 0, 1, 1, 0},
                {1, 1, 1, 1, 1, 1, 0, 0, 1, 0},
                {1, 0, 0, 1, 1, 1, 0, 1, 0, 1},
                {0, 0, 1, 0, 0, 1, 1, 0, 0, 1},
                {0, 1, 0, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 1, 1, 0, 1, 1, 1},
                {1, 1, 0, 0, 1, 0, 1, 0, 1, 1}
        };
        System.out.println("输入....");
        print(matrix);
        int[][] temp = updateMatrix(matrix);
        System.out.println("输出....");
        print(temp);
    }

}
