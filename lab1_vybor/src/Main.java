import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in).useLocale(Locale.US);
        System.out.print("Введите числа a, b, c: ");
        double a = in.nextDouble();
        double b = in.nextDouble();
        double c = in.nextDouble();

        // если a < b < c - удваиваем, иначе берём модули
        if (a < b && b < c) {
            a = a * 2;
            b = b * 2;
            c = c * 2;
            System.out.println("Условие a < b < c выполняется, "
                    + "числа удвоены");
        } else {
            a = Math.abs(a);
            b = Math.abs(b);
            c = Math.abs(c);
            System.out.println("Условие a < b < c не выполняется, "
                    + "числа заменены модулями");
        }

        System.out.println("a = " + a + ", b = " + b + ", c = " + c);
    }
}
