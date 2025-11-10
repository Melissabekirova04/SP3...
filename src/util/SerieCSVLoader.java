package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SerieCSVLoader {


    public static List<Series> load(String path) {
        List<Series> series = new ArrayList<>();


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
                int startYear = 0;
                int endYear = 0;
                double rating = 0.0;

                try {
                    if (yearStr.contains("-")) {
                        String[] partsYear = yearStr.split("-");

                        int partsSize = partsYear.length;
                        if(partsSize == 2){
                            startYear = Integer.parseInt(partsYear[0].trim());
                            endYear = Integer.parseInt(partsYear[1].trim());
                        }
                        if(partsSize == 1){
                            startYear = Integer.parseInt(partsYear[0].trim());
                            endYear = 0000;
                        }
                    }
                    else {
                        startYear = Integer.parseInt(yearStr.trim());
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Kunne ikke læse år for: " + title + " (" + yearStr + ")");
                }

                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException e) {
                    System.out.println(" Could not read rating for: " + title);
                }

                // Opretter et Movie-objekt og tilføj til listen
                int duration = 0; // vi har ingen spilletid i CSV'en
                Series serie = new Series(title, startYear, endYear, rating, category, duration);
                series.add(serie);
            }

            scan.close();

        } catch (Exception e) {
            System.out.println("Could not read the CSV file: " + e.getMessage());
        }

        return series;
    }
}

