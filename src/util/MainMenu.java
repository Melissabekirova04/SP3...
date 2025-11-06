package util;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    // Hjælpeklasser til input/output og filhåndtering
    TextUI ui = new TextUI();
    FileIO io = new FileIO();

    // Lister til brugere og film
    private ArrayList<User> users = new ArrayList<>();
    private User user;               // den aktuelle bruger
    private List<Movies> movies;     // film indlæst fra CSV

    // Indlæs film ved start
    public MainMenu() {
        movies = MovieCSVLoader.load("movies.csv"); // fil skal ligge i projekt-roden
    }

    // Opret bruger
    public void createUser(String name) {
        user = new User(name);
        users.add(user);
    }

    // Hovedløkken
    public void runMJO() {
        ui.displayMsg("Welcome to MJO, " + user.getName() + "!");
        ui.displayMsg("Loaded " + movies.size() + " movies from the CSV file.\n");

        boolean running = true;
        while (running) {
            ui.displayMsg("----- MAIN MENU -----");
            ui.displayMsg("1) Search by title");
            ui.displayMsg("2) Search by category");
            ui.displayMsg("0) Exit the program");

            String choice = ui.promptText("Choose an option: ");

            if (choice.equals("1")) {
                searchByTitle();
            } else if (choice.equals("2")) {
                searchByCategory();
            } else if (choice.equals("0")) {
                ui.displayMsg("Goodbye, " + user.getName() + "!");
                running = false;
            } else {
                ui.displayMsg("Invalid choice. Please try again.");
            }
        }
    }

    // Søger efter titel
    private void searchByTitle() {
        String query = ui.promptText("Enter a movie title or part of a title: ");
        List<Movies> found = new ArrayList<>();

        for (Movies movie : movies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                found.add(movie);
            }
        }

        if (found.isEmpty()) {
            ui.displayMsg("No movies found with '" + query + "'.");
        } else {
            showMoviesAndPlay(found);
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
        } else {
            showMoviesAndPlay(found);
        }
    }

    // Viser søgeresultater og afspil valgt film
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


