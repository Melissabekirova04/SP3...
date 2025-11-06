import util.FileIO;
import util.TextUI;
import util.MovieCSVLoader;
import util.Movies;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    // Hjælpeklasser til input/output og filhåndtering
    TextUI ui = new TextUI();
    FileIO io = new FileIO();

    // Lister til brugere og film
    private ArrayList<User> users = new ArrayList<>();
    private User user; // den bruger, der er logget ind
    private List<Movies> movies; // listen med film fra CSV-filen

    // programmet starter, indlæser filmene fra CSV-filen
    public MainMenu() {
        movies = MovieCSVLoader.load("movies.csv"); // Sørg for at filen ligger i projektmappen
    }

    // Opretter en ny bruger
    public void createUser(String name) {
        user = new User(name);
        users.add(user);
    }

    // Starter hovedprogrammet for den aktuelle bruger
    public void runMJO() {
        ui.displayMsg("Welcome to MJO, " + user.getName() + "!");
        ui.displayMsg("Loaded " + movies.size() +  ", movies for you to watch");

        boolean running = true;
        while (running) {
            // Viser hovedmenuen for brugeren
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

    // --- Søg efter titel ---
    private void searchByTitle() {
        // Beder brugeren om at skrive en titel eller del af titel
        String query = ui.promptText("Enter a movie title or part of a title: ");
        List<Movies> found = new ArrayList<>();

        // Gennemgår alle film og finder dem, der matcher søgningen
        for (Movies movie : movies) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                found.add(movie);
            }
        }

        // Hvis ingen film matcher søgningen
        if (found.isEmpty()) {
            ui.displayMsg("No movies found with '" + query + "'.");
        } else {
            showMoviesAndPlay(found);
        }
    }

    // --- Søg efter kategori ---
    private void searchByCategory() {
        // Beder brugeren om at skrive en kategori (f.eks. Drama, War, Crime)
        String category = ui.promptText("Enter a category (e.g. Drama, War, Crime): ");
        List<Movies> found = new ArrayList<>();

        // Finder alle film i den valgte kategori
        for (Movies movie : movies) {
            if (movie.getCategory().toLowerCase().contains(category.toLowerCase())) {
                found.add(movie);
            }
        }

        // Hvis ingen film matcher kategorien
        if (found.isEmpty()) {
            ui.displayMsg("No movies found in the category '" + category + "'.");
        } else {
            showMoviesAndPlay(found);
        }
    }

    // Viser søgeresultater og lader brugeren vælge en film at afspille
    private void showMoviesAndPlay(List<Movies> movieList) {
        ui.displayMsg("\n----- SEARCH RESULTS -----");

        // Viser alle fundne film med nummer foran
        for (int i = 0; i < movieList.size(); i++) {
            Movies movie = movieList.get(i);
            System.out.println((i + 1) + ") " + movie.getTitle() + " (" + movie.getReleaseDate() + ") "
                    + movie.getCategory() + " ⭐" + movie.getRating());
        }

        // Lader brugeren vælge en film ud fra nummeret
        String choice = ui.promptText("Enter a number to play a movie (or 0 to go back): ");

        try {
            int number = Integer.parseInt(choice);
            if (number == 0) {
                return; // Gå tilbage til hovedmenuen
            }
            Movies selectedMovie = movieList.get(number - 1);
            selectedMovie.play(); // Afspiller den valgte film
        } catch (Exception e) {
            ui.displayMsg("Invalid choice. Please try again.");
        }
    }
}


