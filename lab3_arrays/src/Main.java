import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int n;
        do {
            System.out.print("Введите количество чисел n (n >= 2): ");
            n = in.nextInt();
        } while (n < 2);

        int[] a = new int[n];
        System.out.print("Введите " + n + " целых чисел: ");
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }
        System.out.print("Введите m: ");
        int m = in.nextInt();

        // перебираем все пары i < j и проверяем сумму
        int count = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (a[i] + a[j] == m) {
                    count++;
                    System.out.println("a" + (i + 1) + " + a" + (j + 1)
                            + " = " + a[i] + " + " + a[j] + " = " + m);
                }
            }
        }

        if (count == 0) {
            System.out.println("Пар с суммой " + m + " нет");
        } else {
            System.out.println("Всего найдено пар: " + count);
        }
    }
}
