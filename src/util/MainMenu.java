package util;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    // Hjælpeklasser
    private final TextUI ui = new TextUI();
    private final FileIO io = new FileIO(); // bruges til at læse/skrives CSV

    private final ArrayList<User> users = new ArrayList<>();
    private User user;

    // Film fra CSV
    private final List<Movies> movies = MovieCSVLoader.load("movies.csv");
    private final List<Series> series = SerieCSVLoader.load("Series.csv");


    // Filnavn til at gemme brugere
    private static final String USERS_CSV = "users.csv";
    private static final String USERS_HEADER = "username";



    // Indlæser alle brugere fra users.csv
    private void loadUsersFromCsv() {

        ArrayList<String> lines = io.readData(USERS_CSV);
        users.clear();
        for (String line : lines) {
            String name = line.trim();
            if (!name.isEmpty()) {
                users.add(new User(name));
            }
        }
    }

    // Gemmer alle brugernavne til users.csv
    private void saveUsersToCsv() {
        ArrayList<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.getName());
        }
        io.saveData(lines, USERS_CSV, USERS_HEADER);
    }


    // Opretter bruger i hukommelsen og gemmer den i CSV
    public void createUser(String name) {

        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name)) {
                ui.displayMsg("User already exists. Please choose another name.");
                return;
            }
        }
        this.user = new User(name);
        this.users.add(this.user);
        saveUsersToCsv(); // gem efter oprettelse
    }

    // Startmenu: vælg at oprette eller logge ind
    public void startMenu() {
        // indlæser eksisterende brugere fra CSV ved start
        loadUsersFromCsv();

        ui.displayMsg("Welcome to MJO!");
        ui.displayMsg("1) Create new user");
        ui.displayMsg("2) Log in with existing user");

        String choice = ui.promptText("Choose an option: ");

        switch (choice) {
            case "1": {
                String newName = ui.promptText("Create username: ");
                createUser(newName);
                // hvis oprettet ok, fortsæt
                if (this.user != null && this.user.getName().equalsIgnoreCase(newName)) {
                    ui.displayMsg("Username has been created: " + this.user.getName());
                    runMJO();
                }
                break;
            }
            case "2": {
                if (this.users.isEmpty()) {
                    ui.displayMsg("No users exist, please create one.");
                    return;
                }
                String existingName = ui.promptText("Enter your username: ");
                User found = null;
                for (User u : this.users) {
                    if (u.getName().equalsIgnoreCase(existingName)) {
                        found = u;
                        break;
                    }
                }
                if (found == null) {
                    ui.displayMsg("User not found. Please try again.");
                    return;
                }
                this.user = found;
                ui.displayMsg("Welcome back, " + this.user.getName() + "!");
                runMJO();
                break;
            }
            default:
                ui.displayMsg("Invalid choice.");
        }
    }

    // Hovedmenu efter login
    public void runMJO() {
        ui.displayMsg("Welcome to MJO, " + this.user.getName() + "!");
        ui.displayMsg("Loaded " + this.movies.size() + " movies. ");
        ui.displayMsg("Loaded " + series.size() + " Series.");
        ui.displayMsg("Registered users: " + this.users.size() + "\n");


        boolean running = true;
        while (running) {
            ui.displayMsg("----- MAIN MENU -----");
            ui.displayMsg("1) Search by title");
            ui.displayMsg("2) Search by category");
            ui.displayMsg("0) Exit the program");

            String opt = ui.promptText("Choose an option: ");
            switch (opt) {
                case "1":
                    String type = ui.promptText("Do you want to search for a movie or a series? ");
                    searchByTitle(type);
                    break;
                case "2":
                    searchByCategory();
                    break;
                case "0":
                    ui.displayMsg("Goodbye, " + this.user.getName() + "!");
                    running = false;
                    break;
                default:
                    ui.displayMsg("Invalid choice. Please try again.");
            }
        }
    }

    // --------- Søgninger og afspilning ---------

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
        ArrayList<Movies> found = new ArrayList<>();
        for (Movies m : movies) {
            if (m.getCategory().toLowerCase().contains(category.toLowerCase())) {
                found.add(m);
            }
        }
        if (found.isEmpty()) {
            ui.displayMsg("No movies found in the category '" + category + "'.");
        } else {
            showMoviesAndPlay(found);
        }
    }

    // Viser søgeresultater og afspil valgt film
    private void showMoviesAndPlay(List<Movies> list) {
        ui.displayMsg("\n----- SEARCH RESULTS -----");
        for (int i = 0; i < list.size(); i++) {
            Movies m = list.get(i);
            System.out.println((i + 1) + ") " + m.getTitle() + " (" + m.getReleaseDate() + ") "
                    + m.getCategory() + " ⭐" + m.getRating());
        }
        String choice = ui.promptText("Enter a number to play a movie (or 0 to go back): ");
        try {
            int num = Integer.parseInt(choice);
            if (num == 0) return;
            Movies selected = list.get(num - 1);
            selected.play();
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


