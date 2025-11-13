package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class SerieCSVLoader {

    // Utility-klasse: ingen grund til at kunne instantiere den
    private SerieCSVLoader() {
    }

    public static List<Series> load(String path) {
        List<Series> series = new ArrayList<>();

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Could not find CSV file at: " + path);
            return series;
        }

        try (Scanner scan = new Scanner(file)) {

            // Læser linje for linje
            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                // Springer tomme linjer over
                if (line.isEmpty()) continue;

                // Deler linjen op i dele adskilt af semikolon
                String[] parts = line.split(";");
                if (parts.length < 4) {
                    // Hvis ikke nok dele, spring linjen over
                    System.out.println("Skipping invalid line (not enough columns): " + line);
                    continue;
                }

                // Henter data fra de enkelte dele
                String title = parts[0].trim();
                String yearStr = parts[1].trim();
                String category = parts[2].trim();
                String ratingStr = parts[3].trim().replace(',', '.'); // lav komma til punktum

                // Konverter tekst til tal
                int startYear = 0;
                int endYear = 0;
                double rating = 0.0;

                // Parse år (håndterer fx "2010-2014" eller "2010-" eller "2010")
                try {
                    if (yearStr.contains("-")) {
                        String[] yearParts = yearStr.split("-");
                        // start-år
                        if (yearParts.length > 0 && !yearParts[0].trim().isEmpty()) {
                            startYear = Integer.parseInt(yearParts[0].trim());
                        }

                        // slut-år (kan være tom ved fx "2010-")
                        if (yearParts.length > 1 && !yearParts[1].trim().isEmpty()) {
                            endYear = Integer.parseInt(yearParts[1].trim());
                        } else {
                            endYear = 0; // 0 = stadig i gang / ukendt slut-år
                        }
                    } else {
                        startYear = Integer.parseInt(yearStr.trim());
                        endYear = 0;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Could not read year for: " + title + " (" + yearStr + ")");
                }

                // Parse rating
                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException e) {
                    System.out.println("Could not read rating for: " + title + " (" + ratingStr + ")");
                }

                // Opretter et Series-objekt og tilføjer til listen
                int duration = 0; // vi har ingen spilletid i CSV'en
                Series serie = new Series(title, startYear, endYear, rating, category, duration);
                series.add(serie);
            }

        } catch (Exception e) {
            System.out.println("Could not read the CSV file: " + e.getMessage());
        }

        return series;
    }
}
