import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Laba5 {
    static final String DATA_FILE = "flights.txt";
    static final String RESULT_FILE = "results.txt";
    static final String HEADER = String.format("%-8s %-18s %-6s %5s",
            "Рейс", "Пункт назначения", "Вылет", "Места");

    static Scanner in = new Scanner(System.in);
    static FlightBase base = new FlightBase(DATA_FILE);

    public static void main(String[] args) throws IOException {
        base.load();
        System.out.println("Загружено рейсов из файла " + DATA_FILE + ": "
                + base.getFlights().size());

        int choice;
        do {
            System.out.println();
            System.out.println("===== Билеты на самолёты =====");
            System.out.println("1 - Показать все рейсы");
            System.out.println("2 - Время отправления самолётов в город");
            System.out.println("3 - Наличие свободных мест на рейс");
            System.out.println("4 - Добавить рейс");
            System.out.println("5 - Удалить рейс");
            System.out.println("6 - Сортировать рейсы");
            System.out.println("0 - Выход");
            choice = readInt("Выберите пункт: ");

            switch (choice) {
                case 1: showAll(); break;
                case 2: departuresToCity(); break;
                case 3: freeSeatsOnFlight(); break;
                case 4: addFlight(); break;
                case 5: removeFlight(); break;
                case 6: sortFlights(); break;
                case 0: System.out.println("Работа завершена"); break;
                default: System.out.println("Нет такого пункта меню");
            }
        } while (choice != 0);
    }

    static void showAll() {
        System.out.println(HEADER);
        for (Flight f : base.getFlights()) {
            System.out.println(f);
        }
    }

    static void departuresToCity() throws IOException {
        String city = readLine("Введите город: ");
        ArrayList<Flight> found = base.findByDestination(city);
        String result;
        if (found.isEmpty()) {
            result = "Рейсов в город " + city + " нет";
        } else {
            result = "Время отправления самолётов в город " + city + ":";
            for (Flight f : found) {
                result += System.lineSeparator() + "  "
                        + f.getDepartureTime() + " - рейс " + f.getNumber();
            }
        }
        System.out.println(result);
        saveResult(result);
    }

    static void freeSeatsOnFlight() throws IOException {
        String number = readLine("Введите номер рейса: ");
        Flight f = base.findByNumber(number);
        String result;
        if (f == null) {
            result = "Рейс " + number + " не найден";
        } else if (f.getFreeSeats() > 0) {
            result = "На рейс " + f.getNumber() + " (" + f.getDestination()
                    + ") есть свободные места: " + f.getFreeSeats();
        } else {
            result = "На рейс " + f.getNumber() + " (" + f.getDestination()
                    + ") свободных мест нет";
        }
        System.out.println(result);
        saveResult(result);
    }

    static void addFlight() throws IOException {
        String number = readLine("Номер рейса: ");
        String city = readLine("Пункт назначения: ");
        LocalTime time = readTime("Время вылета (ЧЧ:ММ): ");
        int seats = readInt("Количество свободных мест: ");
        if (base.add(new Flight(number, city, time, Math.max(seats, 0)))) {
            base.save();
            System.out.println("Рейс добавлен и сохранён в файл");
        } else {
            System.out.println("Рейс с таким номером уже есть");
        }
    }

    static void removeFlight() throws IOException {
        String number = readLine("Номер удаляемого рейса: ");
        if (base.remove(number)) {
            base.save();
            System.out.println("Рейс удалён");
        } else {
            System.out.println("Рейс не найден");
        }
    }

    static void sortFlights() throws IOException {
        int field = readInt("Поле сортировки (1 - номер, "
                + "2 - пункт назначения, 3 - время вылета, 4 - места): ");
        if (field < 1 || field > 4) {
            System.out.println("Нет такого поля");
            return;
        }
        base.sortBy(field);
        base.save();
        showAll();
    }

    // результаты запросов дописываются в конец файла
    static void saveResult(String text) throws IOException {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        Files.write(Paths.get(RESULT_FILE),
                Collections.singletonList("[" + time + "] " + text),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    static String readLine(String prompt) {
        String s;
        do {
            System.out.print(prompt);
            s = in.nextLine().trim();
        } while (s.isEmpty());
        return s;
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Нужно ввести целое число");
            }
        }
    }

    static LocalTime readTime(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(readLine(prompt));
            } catch (DateTimeParseException e) {
                System.out.println("Формат времени: ЧЧ:ММ, например 07:45");
            }
        }
    }
}
