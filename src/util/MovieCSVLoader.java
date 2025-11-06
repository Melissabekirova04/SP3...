package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieCSVLoader {


    public static List<Movies> load(String path) {
        List<Movies> movies = new ArrayList<>();

        try {
            // Opret en fil og en scanner til at læse den
            File file = new File(path);
            Scanner scan = new Scanner(file);

            // Læs linje for linje
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                // Spring tomme linjer over
                if (line.isEmpty()) continue;

                // Del linjen op i dele adskilt af semikolon
                String[] parts = line.split(";");
                if (parts.length < 4) continue; // Hvis ikke nok dele, spring linjen over

                // Hent data fra de enkelte dele
                String title = parts[0].trim();
                String yearStr = parts[1].trim();
                String category = parts[2].trim();
                String ratingStr = parts[3].trim().replace(',', '.'); // lav komma til punktum

                // Konverter tekst til tal
                int year = 0;
                double rating = 0.0;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    System.out.println(" Could not read year for: " + title);
                }

                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException e) {
                    System.out.println(" Could not read rating for: " + title);
                }

                // Opret et Movie-objekt og tilføj til listen
                int duration = 0; // vi har ingen spilletid i CSV'en
                Movies movie = new Movies(title, year, rating, category, duration);
                movies.add(movie);
            }

            scan.close(); // Luk scanneren når vi er færdige

        } catch (Exception e) {
            System.out.println("Could not read the CSV file: " + e.getMessage());
        }

        return movies;
    }
}

