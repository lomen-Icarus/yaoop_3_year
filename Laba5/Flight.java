import java.time.LocalTime;

public class Flight {
    private String number;
    private String destination;
    private LocalTime departureTime;
    private int freeSeats;

    public Flight(String number, String destination,
                  LocalTime departureTime, int freeSeats) {
        this.number = number;
        this.destination = destination;
        this.departureTime = departureTime;
        this.freeSeats = freeSeats;
    }

    public String getNumber() {
        return number;
    }

    public String getDestination() {
        return destination;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public int getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(int freeSeats) {
        this.freeSeats = freeSeats;
    }

    // строка файла: номер;пункт назначения;время;места
    public String toFileLine() {
        return number + ";" + destination + ";"
                + departureTime + ";" + freeSeats;
    }

    public static Flight fromFileLine(String line) {
        String[] p = line.split(";");
        return new Flight(p[0].trim(), p[1].trim(),
                LocalTime.parse(p[2].trim()), Integer.parseInt(p[3].trim()));
    }

    @Override
    public String toString() {
        return String.format("%-8s %-18s %-6s %5d",
                number, destination, departureTime, freeSeats);
    }
}
