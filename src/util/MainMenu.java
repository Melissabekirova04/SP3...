package util;

import util.SerieCSVLoader;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    // Hjælpeklasse til tekst I/O
    private final TextUI ui = new TextUI();

    // Liste over brugere
    private final ArrayList<User> users = new ArrayList<>();

    // Den aktuelle bruger der er logget ind
    private User user;

    // Film indlæst fra CSV
    private final List<Movies> movies;
    private final List<Series> series;


    // Indlæser film ved start
    public MainMenu() {
        movies = MovieCSVLoader.load("movies.csv");
        series = SerieCSVLoader.load("series.csv");
    }

    // Opretter en ny bruger og tilføj til listen
    public void createUser(String name) {
        user = new User(name);
        users.add(user);
    }

    // Hovedløkken for programmet
    public void runMJO() {
        ui.displayMsg("Welcome to MJO, " + user.getName() + "!");
        ui.displayMsg("Loaded " + movies.size() + " movies from the CSV file.");
        ui.displayMsg("Loaded " + series.size() + " movies from the CSV file.");
        ui.displayMsg("Registered users: " + users.size() + "\n"); // ← nu “queries” vi users-listen

        boolean running = true;
        while (running) {
            // Vis menu
            ui.displayMsg("----- MAIN MENU -----");
            ui.displayMsg("1) Search by title");
            ui.displayMsg("2) Search by category");
            ui.displayMsg("0) Exit the program");

            String choice = ui.promptText("Choose an option: ");
            String searchOption = ui.promptText("Choose to search in movie or series: ");

            //switch case
            switch (choice) {
                case "1":
                    searchByTitle(searchOption);
                    break;
                case "2":
                    searchByCategory();
                    break;
                case "3":
                    ui.displayMsg("Goodbye, " + user.getName() + "!");
                    running = false;
                    break;
                default:
                    ui.displayMsg("Invalid choice. Please try again.");
            }
        }
    }

    // Søger efter titel
    private void searchByTitle(String searchOption) {
        switch (searchOption.toLowerCase()) {
            case "movie":
                // Ask for search text
                String query = ui.promptText("Enter a movie title or part of a title: ");
                List<Movies> found = new ArrayList<>();

                // Find all matching movies
                for (Movies movie : movies) {
                    if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        found.add(movie);
                    }
                }

                // No results?
                if (found.isEmpty()) {
                    ui.displayMsg("No movies found with '" + query + "'.");
                    return;
                }

                showMoviesAndPlay(found);
                break;

            case "series":
                String seriesQuery = ui.promptText("Enter a series title or part of a title: ");
                List<Series> foundSeries = new ArrayList<>();

                for (Series serie : series) {
                    if (serie.getTitle().toLowerCase().contains(seriesQuery.toLowerCase())) {
                        foundSeries.add(serie);
                    }
                }

                if (foundSeries.isEmpty()) {
                    ui.displayMsg("No series found with '" + seriesQuery + "'.");
                    return;
                }

                showSeriesAndPlay(foundSeries);
                break;

            default:
                ui.displayMsg("Search option '" + searchOption + "' is invalid. Please choose 'movie' or 'series'.");
                break;
        }
    }


    // Søger efter kategori
    private void searchByCategory() {
        String category = ui.promptText("Enter a category (e.g. Drama, War, Crime): ");
        List<Movies> found = new ArrayList<>();

        for (Movies movie : movies) {
            if (movie.getCategory().toLowerCase().contains(category.toLowerCase())) {
                found.add(movie);
            }
        }

        if (found.isEmpty()) {
            ui.displayMsg("No movies found in the category '" + category + "'.");
            return;
        }

        showMoviesAndPlay(found);
    }

    // Viser søgeresultaterne og afspil valgt film
    private void showMoviesAndPlay(List<Movies> movieList) {
        ui.displayMsg("\n----- SEARCH RESULTS -----");

        for (int i = 0; i < movieList.size(); i++) {
            Movies movie = movieList.get(i);
            System.out.println((i + 1) + ") " + movie.getTitle() + " (" + movie.getReleaseDate() + ") "
                    + movie.getCategory() + " ⭐" + movie.getRating());
        }

        String choice = ui.promptText("Enter a number to play a movie (or 0 to go back): ");

        try {
            int number = Integer.parseInt(choice);
            if (number == 0) return;
            Movies selectedMovie = movieList.get(number - 1);
            selectedMovie.play();
        } catch (Exception e) {
            ui.displayMsg("Invalid choice. Please try again.");
        }
    }



    private void showSeriesAndPlay(List<Series> seriesList) {
        ui.displayMsg("\n----- SEARCH RESULTS -----");

        for (int i = 0; i < seriesList.size(); i++) {
            Series serie = seriesList.get(i);
            System.out.println((i + 1) + ") " + serie.getTitle() + " (" + serie.getReleaseDate() + ") "
                    + serie.getCategory() + " ⭐" + serie.getRating());
        }

//        String choice = ui.promptText("Enter the number of series you want to play (or 0 to go back): ");
        String choice = ui.promptText(
                "\u001B[31mE\u001B[33mn\u001B[32mt\u001B[34me\u001B[35mr \u001B[31mt\u001B[33mh\u001B[32me \u001B[34mn\u001B[35mu\u001B[31mm\u001B[33mb\u001B[32me\u001B[34mr \u001B[35mo\u001B[31mf \u001B[33ms\u001B[32me\u001B[34mr\u001B[35mi\u001B[31es \u001B[33mw\u001B[32ma\u001B[34mn\u001B[35mt \u001B[31mt\u001B[33mo \u001B[32mp\u001B[34ml\u001B[35ma\u001B[31my \u001B[33m(\u001B[32mo\u001B[34mr 0 \u001B[35mto \u001B[31mg\u001B[33mo \u001B[32mback): \u001B[0m"
        );

        try {
            int number = Integer.parseInt(choice);
            if (number == 0) return;
            Series selectedSeries = seriesList.get(number - 1);
            selectedSeries.play();
        } catch (Exception e) {
            ui.displayMsg("Invalid choice. Please try again.");
        }
    }
}
