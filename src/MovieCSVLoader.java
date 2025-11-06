package util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MovieCSVLoader {


    public static List<Movies> load(String path) {
        List<Movies> movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                String title = parts[0].trim();
                String yearStr = parts[1].trim();
                String category = parts[2].trim();
                String ratingStr = parts[3].trim().replace(',', '.');

                int year;
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    continue; // spring linjen over hvis år ikke kan læses
                }

                double rating;
                try {
                    rating = Double.parseDouble(ratingStr);
                } catch (NumberFormatException e) {
                    rating = 0.0;
                }

                int duration = 0; // CSV'en har ingen varighed
                Movies m = new Movies(title, year, rating, category, duration);
                movies.add(m);
            }

        } catch (Exception e) {
            System.out.println("❌ Kunne ikke læse CSV: " + e.getMessage());
        }

        return movies;
    }
}
