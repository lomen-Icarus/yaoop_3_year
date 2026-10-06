import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FlightBase {
    private final Path file;
    private final ArrayList<Flight> flights = new ArrayList<>();

    public FlightBase(String fileName) {
        this.file = Paths.get(fileName);
    }

    public void load() throws IOException {
        flights.clear();
        if (!Files.exists(file)) {
            return;
        }
        try (BufferedReader reader =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    flights.add(Flight.fromFileLine(line));
                }
            }
        }
    }

    public void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Flight f : flights) {
            lines.add(f.toFileLine());
        }
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    public ArrayList<Flight> getFlights() {
        return flights;
    }

    public Flight findByNumber(String number) {
        for (Flight f : flights) {
            if (f.getNumber().equalsIgnoreCase(number)) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<Flight> findByDestination(String city) {
        ArrayList<Flight> result = new ArrayList<>();
        for (Flight f : flights) {
            if (f.getDestination().equalsIgnoreCase(city)) {
                result.add(f);
            }
        }
        result.sort(Comparator.comparing(Flight::getDepartureTime));
        return result;
    }

    public boolean add(Flight flight) {
        if (findByNumber(flight.getNumber()) != null) {
            return false;
        }
        flights.add(flight);
        return true;
    }

    public boolean remove(String number) {
        Flight f = findByNumber(number);
        return f != null && flights.remove(f);
    }

    // 1 - номер, 2 - пункт назначения, 3 - время вылета, 4 - свободные места
    public void sortBy(int field) {
        switch (field) {
            case 1:
                flights.sort(Comparator.comparing(Flight::getNumber));
                break;
            case 2:
                flights.sort(Comparator.comparing(Flight::getDestination));
                break;
            case 3:
                flights.sort(Comparator.comparing(Flight::getDepartureTime));
                break;
            case 4:
                flights.sort(Comparator.comparingInt(Flight::getFreeSeats));
                break;
        }
    }
}
