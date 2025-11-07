package util;

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

    // Indlæser film ved start
    public MainMenu() {
        movies = MovieCSVLoader.load("movies.csv");
    }

    // Opretter en ny bruger og tilføj til listen
    public void createUser(String name) {
        user = new User(name);
        users.add(user);
    }

    // Hovedløkken for programmet
    public void runMJO() {
        ui.displayMsg("Welcome to MJO, " + user.getName() + "!");
        ui.displayMsg("Loaded " + movies.size() + " movies from MJO.");
        ui.displayMsg("Registered users: " + users.size() + "\n"); // ← nu “queries” vi users-listen

        boolean running = true;
        while (running) {
            // Vis menu
            ui.displayMsg("----- MAIN MENU -----");
            ui.displayMsg("1) Search by title");
            ui.displayMsg("2) Search by category");
            ui.displayMsg("0) Exit the program");

            String choice = ui.promptText("Choose an option: ");

            //switch case
            switch (choice) {
                case "1":
                    searchByTitle();
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
    private void searchByTitle() {
        // Bed brugeren om søgetekst
        String query = ui.promptText("Enter a movie title or part of a title: ");
        List<Movies> found = new ArrayList<>();

        // Finder alle film der matcher
        for (Movies movie : movies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                found.add(movie);
            }
        }

        // Ingen resultater?
        if (found.isEmpty()) {
            ui.displayMsg("No movies found with '" + query + "'.");
            return;
        }

        showMoviesAndPlay(found);
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
}
