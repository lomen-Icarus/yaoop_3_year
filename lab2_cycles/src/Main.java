import java.util.Scanner;

public class Main {
    static final int DAYS_YEAR = 365;
    static final int[] DAYS_IN_MONTH =
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    static final String[] MONTHS = {"января", "февраля", "марта", "апреля",
            "мая", "июня", "июля", "августа", "сентября", "октября",
            "ноября", "декабря"};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int dayOfYear;

        // повторяем ввод, пока номер дня не попадёт в диапазон
        do {
            System.out.print("Введите номер дня в году (1-"
                    + DAYS_YEAR + "): ");
            dayOfYear = in.nextInt();
        } while (dayOfYear < 1 || dayOfYear > DAYS_YEAR);

        // вычитаем длины месяцев, пока остаток не поместится в текущий месяц
        int day = dayOfYear;
        int month = 0;
        while (day > DAYS_IN_MONTH[month]) {
            day = day - DAYS_IN_MONTH[month];
            month++;
        }

        System.out.println(dayOfYear + "-й день года - "
                + day + " " + MONTHS[month]);
    }
}
