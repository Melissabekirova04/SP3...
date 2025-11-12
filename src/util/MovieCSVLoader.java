package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieCSVLoader {

    public static List<Movies> load(String path) {
        List<Movies> movies = new ArrayList<>();

        try {
            // Opretter en fil og en scanner til at læse den
            File file = new File(path);
            Scanner scan = new Scanner(file);

            // Læser linje for linje
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                // Springer tomme linjer over
                if (line.isEmpty()) continue;

                // Deler linjen op i dele adskilt af semikolon
                String[] parts = line.split(";");
                if (parts.length < 4) continue; // Hvis ikke nok dele, spring linjen over

                // Henter data fra de enkelte dele
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

                // NYT: prøv at læse varighed som kolonne 5 (hvis den findes)
                int duration = 0; // fallback hvis CSV ikke har varighed
                if (parts.length >= 5) {
                    String durStr = parts[4].trim();
                    // tillad formater som "175", "175 min", "175min"
                    durStr = durStr.replace("min", "").trim();

                    if (!durStr.isEmpty()) {
                        try {
                            duration = Integer.parseInt(durStr);
                        } catch (NumberFormatException e) {
                            // hvis vi ikke kan parse, beholder vi 0
                            System.out.println(" Could not read duration for: " + title + " -> '" + parts[4].trim() + "'");
                        }
                    }
                }

                // Opretter et Movie-objekt og tilføj til listen
                Movies movie = new Movies(title, year, rating, category, duration);
                movies.add(movie);
            }

            scan.close();

        } catch (Exception e) {
            System.out.println("Could not read the CSV file: " + e.getMessage());
        }

        return movies;
    }
}


