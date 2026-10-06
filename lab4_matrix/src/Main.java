import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n;
        do {
            System.out.print("Введите порядок квадратной матрицы n: ");
            n = in.nextInt();
        } while (n < 1);

        int[][] matrix = new int[n][n];
        System.out.println("Введите матрицу " + n + "x" + n + " по строкам:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = in.nextInt();
            }
        }

        // ищем строки с наибольшим и наименьшим произведением
        int maxRow = 0;
        int minRow = 0;
        long maxProduct = product(matrix[0]);
        long minProduct = maxProduct;
        for (int i = 1; i < n; i++) {
            long p = product(matrix[i]);
            if (p > maxProduct) {
                maxProduct = p;
                maxRow = i;
            }
            if (p < minProduct) {
                minProduct = p;
                minRow = i;
            }
        }

        System.out.println("Строка " + (maxRow + 1) + " с наибольшим "
                + "произведением: " + rowToString(matrix[maxRow]));
        System.out.println("Произведение элементов: " + maxProduct);
        System.out.println("Строка " + (minRow + 1) + " с наименьшим "
                + "произведением: " + rowToString(matrix[minRow]));
        System.out.println("Произведение элементов: " + minProduct);
    }

    static long product(int[] row) {
        long p = 1;
        for (int x : row) {
            p *= x;
        }
        return p;
    }

    static String rowToString(int[] row) {
        String s = "";
        for (int x : row) {
            s += x + " ";
        }
        return s.trim();
    }
}
