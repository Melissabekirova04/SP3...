package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class MainMenu {
    // Hjælpeklasser
    private final TextUI ui = new TextUI();
    private final FileIO io = new FileIO(); // bruges til at læse/skrives CSV

    private final ArrayList<User> users = new ArrayList<>();
    private User user;

    // Film og serier fra CSV
    private final List<Movies> movies = MovieCSVLoader.load("movies.csv");
    private final List<Series> series = SerieCSVLoader.load("Series.csv");

    // Filnavn til at gemme brugere
    private static final String USERS_CSV = "users.csv";
    private static final String USERS_HEADER = "username;password";

    // Indlæser alle brugere fra users.csv
    private void loadUsersFromCsv() {

        ArrayList<String> lines = io.readData(USERS_CSV);
        users.clear();

        // bruger trimmed for at undgå unødvendige mellemrum
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.equalsIgnoreCase(USERS_HEADER)) {
                continue; // springer tom linje eller header over
            }

            String[] parts = trimmed.split(";"); // Deler linjen op ved semikolon(Name og password bliver delt op)
            String name = parts [0].trim();

            String password;
            if(parts.length > 1){ // tjekker om der findes en anden del (password) efter semikolon
                password = parts[1].trim(); // hvis ja, gem den som password
            } else {
                password = ""; // ellers så sæt password til tom tekst
            }
            if (!name.isEmpty()){
                users.add(new User(name, password)); // tilføjer brugeren med navn og password til listen
            }
        }
    }

    // Gemmer alle brugernavne + passwords til users.csv
    private void saveUsersToCsv() {
        ArrayList<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.getName() + ";" + u.getPassword());
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
        String password = ui.promptText("Create passwords: ");

        this.user = new User(name, password);
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
                String password = ui.promptText("Enter your password: ");

                User found = null;
                for (User u : this.users) {
                    if (u.getName().equalsIgnoreCase(existingName) && u.getPassword().equals(password)) {
                        found = u;
                        break;
                    }
                }
                if (found == null) {
                    ui.displayMsg("Wrong username or password. Please try again.");
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
            ui.displayMsg("3) Show list of: Saved for later");
            ui.displayMsg("4) Show list of: Already watched");
            ui.displayMsg("5) Filter by type/category/rating/year");
            ui.displayMsg("0) Exit the program");

            String opt = ui.promptText("Choose an option: ");
            switch (opt) {
                case "1":
                    String type = ui.promptText("Do you want to search for:\n1) Movies\n2) Series");
                    searchByTitle(type);
                    break;
                case "2":
                    searchByCategory();  // ← opdateret nedenfor med sortering
                    break;
                case "3":
                    showSavedForLater();
                    break;
                case "4":
                    showAlreadyWatched();
                    break;
                case "5":
                    filterMenu();        // ← sorterer ud fra valgte filtre
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

    // Søger efter titel (nu med mulighed for at filtrere og sortere de fundne resultater)
    private void searchByTitle(String searchOption) {
        switch (searchOption.toLowerCase()) {
            case "1": {
                String query = ui.promptText("Enter a movie title or part of a title: ");
                List<Movies> found = new ArrayList<>();
                for (Movies movie : movies) {
                    if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        found.add(movie);
                    }
                }
                if (found.isEmpty()) {
                    ui.displayMsg("No movies found with '" + query + "'.");
                    return;
                }

                // Valgfri filtrering + sortering på de fundne film
                if (ui.promptText("Apply filters to these results? (y/n): ").trim().equalsIgnoreCase("y")) {
                    String category = ui.promptText("Category contains (leave empty for any): ").trim();
                    String minRatingStr = ui.promptText("Minimum rating (e.g. 7.5) (leave empty for any): ").trim();
                    String yearStr = ui.promptText("Year (exact, e.g. 1994) (leave empty for any): ").trim();

                    Double minRating = null;
                    if (!minRatingStr.isEmpty()) {
                        try { minRating = Double.parseDouble(minRatingStr.replace(',', '.')); }
                        catch (NumberFormatException e) { ui.displayMsg("Invalid rating format. Ignoring."); }
                    }

                    Integer year = null;
                    if (!yearStr.isEmpty()) {
                        try { year = Integer.parseInt(yearStr); }
                        catch (NumberFormatException e) { ui.displayMsg("Invalid year format. Ignoring."); }
                    }

                    found = filterMoviesOnSource(found, category, minRating, year);
                    // sortér baseret på hvilke filtre der blev brugt
                    sortMovies(found, minRating, year);

                    if (found.isEmpty()) {
                        ui.displayMsg("No movies matched your filters.");
                        return;
                    }
                }

                showMoviesAndPlay(found);
                break;
            }
            case "2": {
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

                // Valgfri filtrering + sortering på de fundne serier
                if (ui.promptText("Apply filters to these results? (y/n): ").trim().equalsIgnoreCase("y")) {
                    String category = ui.promptText("Category contains (leave empty for any): ").trim();
                    String minRatingStr = ui.promptText("Minimum rating (e.g. 7.5) (leave empty for any): ").trim();
                    String yearStr = ui.promptText("Year (exact, e.g. 2010) (leave empty for any): ").trim();

                    Double minRating = null;
                    if (!minRatingStr.isEmpty()) {
                        try { minRating = Double.parseDouble(minRatingStr.replace(',', '.')); }
                        catch (NumberFormatException e) { ui.displayMsg("Invalid rating format. Ignoring."); }
                    }

                    Integer year = null;
                    if (!yearStr.isEmpty()) {
                        try { year = Integer.parseInt(yearStr); }
                        catch (NumberFormatException e) { ui.displayMsg("Invalid year format. Ignoring."); }
                    }

                    foundSeries = filterSeriesOnSource(foundSeries, category, minRating, year);
                    // sortér baseret på hvilke filtre der blev brugt
                    sortSeries(foundSeries, minRating, year);

                    if (foundSeries.isEmpty()) {
                        ui.displayMsg("No series matched your filters.");
                        return;
                    }
                }

                showSeriesAndPlay(foundSeries);
                break;
            }
            default:
                ui.displayMsg("Search option '" + searchOption + "' is invalid. Please choose 'movie' or 'series'.");
        }
    }

    // Søger efter kategori (nu med valg af type, valgfri filter og sortering)
    private void searchByCategory() {
        String category = ui.promptText("Enter a category (e.g. Drama, War, Crime): ");
        String type = ui.promptText("Search in 'movie' or 'series'?: ").trim();

        if (type.equalsIgnoreCase("movie")) {
            ArrayList<Movies> found = new ArrayList<>();
            for (Movies m : movies) {
                if (m.getCategory().toLowerCase().contains(category.toLowerCase())) {
                    found.add(m);
                }
            }

            if (found.isEmpty()) {
                ui.displayMsg("No movies found in the category '" + category + "'.");
                return;
            }

            String filterChoice = ui.promptText(
                    "Apply additional filters to these movies? (y/n)\n" +
                            "→ Example: rating 8.0, year 1994 (press Enter to skip)"
            ).trim();

            if (filterChoice.equalsIgnoreCase("y")) {
                String minRatingStr = ui.promptText("Minimum rating (e.g. 7.5). Leave empty to skip: ").trim();
                String yearStr = ui.promptText("Exact release year (e.g. 2001). Leave empty to skip: ").trim();

                Double minRating = null;
                if (!minRatingStr.isEmpty()) {
                    try { minRating = Double.parseDouble(minRatingStr.replace(',', '.')); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid rating format, ignoring filter."); }
                }

                Integer year = null;
                if (!yearStr.isEmpty()) {
                    try { year = Integer.parseInt(yearStr); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid year format, ignoring filter."); }
                }

                found = filterMoviesOnSource(found, category, minRating, year);
                // sortér efter valgte filtre
                sortMovies(found, minRating, year);

            } else {
                // ingen ekstra filtre -> alfabetisk på titel
                found.sort(Comparator.comparing(Movies::getTitle, String.CASE_INSENSITIVE_ORDER));
            }

            showMoviesAndPlay(found);

        } else if (type.equalsIgnoreCase("series")) {
            ArrayList<Series> found = new ArrayList<>();
            for (Series s : series) {
                if (s.getCategory().toLowerCase().contains(category.toLowerCase())) {
                    found.add(s);
                }
            }

            if (found.isEmpty()) {
                ui.displayMsg("No series found in the category '" + category + "'.");
                return;
            }

            String filterChoice = ui.promptText(
                    "Apply additional filters to these series? (y/n)\n" +
                            "→ Example: rating 8.5, year 2015 (press Enter to skip)"
            ).trim();

            if (filterChoice.equalsIgnoreCase("y")) {
                String minRatingStr = ui.promptText("Minimum rating (e.g. 7.0). Leave empty to skip: ").trim();
                String yearStr = ui.promptText("Exact start year (e.g. 2010). Leave empty to skip: ").trim();

                Double minRating = null;
                if (!minRatingStr.isEmpty()) {
                    try { minRating = Double.parseDouble(minRatingStr.replace(',', '.')); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid rating format, ignoring filter."); }
                }

                Integer year = null;
                if (!yearStr.isEmpty()) {
                    try { year = Integer.parseInt(yearStr); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid year format, ignoring filter."); }
                }

                found = filterSeriesOnSource(found, category, minRating, year);
                // sortér efter valgte filtre
                sortSeries(found, minRating, year);

            } else {
                // ingen ekstra filtre -> alfabetisk på titel
                found.sort(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER));
            }

            showSeriesAndPlay(found);

        } else {
            ui.displayMsg("Invalid type. Please choose 'movie' or 'series'.");
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
            selected.play(user);
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

        String choice = ui.promptText("Enter the number of the series to play (or 0 to go back): ");

        try {
            int number = Integer.parseInt(choice);
            if (number == 0) return;
            Series selectedSeries = seriesList.get(number - 1);
            selectedSeries.play(user);
        } catch (Exception e) {
            ui.displayMsg("Invalid choice. Please try again.");
        }
    }

    private void showSavedForLater () {
        if (user.getSavedForLater().size() >= 1) { //Hvis brugerens antal af set medie er større end 0 vis listen.
            ui.displayMsg("-------------Saved for later-------------");
            for (int i=0; i<user.getSavedForLater().size(); i++) {
                Media m = user.getSavedForLater().get(i);
                ui.displayMsg(i+1 + ") " + m.getTitle() + " (" + m.getReleaseDate() + "), " + m.getCategory() + ", " + m.getRating());
            }
            String choice = ui.promptText("Chose the number of a movie / show you want to select or press 0 to go back to the main menu");
            try {
                int number = Integer.parseInt(choice);
                if (number == 0) return;
                Media selectedMedia = user.getSavedForLater().get(number - 1);
                selectedMedia.play(user);
            } catch (Exception e) {
                ui.displayMsg("Invalid choice. Please try again.");
            }

        } else {
            ui.promptText("Nothing added to watch later... Press any button to return to the main menu");
            runMJO();
        }
    }

    private void showAlreadyWatched() {
        if (user.getWatchedMedia().size() >= 1) { //Hvis brugerens antal af set medie er større end 0 vis listen.
            ui.displayMsg("-------------Already watched-------------");
            for (int i=0; i<user.getWatchedMedia().size(); i++) {
                Media m = user.getWatchedMedia().get(i);
                ui.displayMsg(i+1 + ") " + m.getTitle() + " (" + m.getReleaseDate() + "), " + m.getCategory() + ", " + m.getRating());
            }
            String choice = ui.promptText("Chose the number of a movie / show you want to select or press 0 to go back to the main menu");
            try {
                int number = Integer.parseInt(choice);
                if (number == 0) return;
                Media selectedMedia = user.getWatchedMedia().get(number - 1);
                selectedMedia.play(user);
            } catch (Exception e) {
                ui.displayMsg("Invalid choice. Please try again.");
            }

        } else {
            ui.promptText("Nothing added to watch later... Press any button to return to the main menu");
            runMJO();
        }
    }

    // -------------------- HJÆLPE-METODER TIL FILTER & SORT --------------------

    // Generel filter-menu
    private void filterMenu() {
        String type = ui.promptText("Filter what? Type 'movie' or 'series' (or empty to cancel): ").trim();
        if (type.isEmpty()) return;

        String filterChoice = ui.promptText("Filter by: 'category', 'rating' or 'year': ").trim().toLowerCase();

        if (type.equalsIgnoreCase("movie")) {
            ArrayList<Movies> result = new ArrayList<>(movies);

            switch (filterChoice) {
                case "category": {
                    String category = ui.promptText("Enter category (e.g. Drama, War, Crime): ").trim();
                    // filter
                    result.removeIf(m -> !m.getCategory().toLowerCase().contains(category.toLowerCase()));
                    // sortér alfabetisk (A→Z)
                    result.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                    break;
                }
                case "rating": {
                    // ingen ekstra input – vis bare bedst ratede først
                    result.sort((a, b) -> Double.compare(b.getRating(), a.getRating())); // høj → lav
                    break;
                }
                case "year": {
                    String yearStr = ui.promptText("Enter year (e.g. 1994): ").trim();
                    Integer year = null;
                    try { year = Integer.parseInt(yearStr); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid year format. Try again."); return; }

                    // filter
                    Integer finalYear = year;
                    result.removeIf(m -> m.getReleaseDate() != finalYear);
                    // sortér år stigende (giver mening hvis der alligevel er flere)
                    result.sort((a, b) -> Integer.compare(a.getReleaseDate(), b.getReleaseDate()));
                    break;
                }
                default:
                    ui.displayMsg("Invalid filter type. Please choose 'category', 'rating' or 'year'.");
                    return;
            }

            if (result.isEmpty()) {
                ui.displayMsg("No movies matched your filters.");
            } else {
                showMoviesAndPlay(result);
            }

        } else if (type.equalsIgnoreCase("series")) {
            ArrayList<Series> result = new ArrayList<>(series);

            switch (filterChoice) {
                case "category": {
                    String category = ui.promptText("Enter category (e.g. Drama, War, Crime): ").trim();
                    // filter
                    result.removeIf(s -> !s.getCategory().toLowerCase().contains(category.toLowerCase()));
                    // sortér alfabetisk (A→Z)
                    result.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                    break;
                }
                case "rating": {
                    // vis bedst ratede først
                    result.sort((a, b) -> Double.compare(b.getRating(), a.getRating())); // høj → lav
                    break;
                }
                case "year": {
                    String yearStr = ui.promptText("Enter start year (e.g. 2010): ").trim();
                    Integer year = null;
                    try { year = Integer.parseInt(yearStr); }
                    catch (NumberFormatException e) { ui.displayMsg("Invalid year format. Try again."); return; }

                    // filter (vi bruger startår = getReleaseDate())
                    Integer finalYear = year;
                    result.removeIf(s -> s.getReleaseDate() != finalYear);
                    // sortér år stigende
                    result.sort((a, b) -> Integer.compare(a.getReleaseDate(), b.getReleaseDate()));
                    break;
                }
                default:
                    ui.displayMsg("Invalid filter type. Please choose 'category', 'rating' or 'year'.");
                    return;
            }

            if (result.isEmpty()) {
                ui.displayMsg("No series matched your filters.");
            } else {
                showSeriesAndPlay(result);
            }

        } else {
            ui.displayMsg("Invalid type. Please choose 'movie' or 'series'.");
        }
    }


    // Filtrér film på en given liste (bruges af både menu 5 og søgning)
    private ArrayList<Movies> filterMoviesOnSource(List<Movies> source, String category, Double minRating, Integer year) {
        ArrayList<Movies> result = new ArrayList<>();
        boolean hasCategory = category != null && !category.isEmpty();

        for (Movies m : source) {
            if (hasCategory && !m.getCategory().toLowerCase().contains(category.toLowerCase())) continue;
            if (minRating != null && m.getRating() < minRating) continue;
            if (year != null && m.getReleaseDate() != year) continue;
            result.add(m);
        }
        return result;
    }

    // Filtrér serier på en given liste (bruges af både menu 5 og søgning)
    private ArrayList<Series> filterSeriesOnSource(List<Series> source, String category, Double minRating, Integer year) {
        ArrayList<Series> result = new ArrayList<>();
        boolean hasCategory = category != null && !category.isEmpty();

        for (Series s : source) {
            if (hasCategory && !s.getCategory().toLowerCase().contains(category.toLowerCase())) continue;
            if (minRating != null && s.getRating() < minRating) continue;
            if (year != null && s.getReleaseDate() != year) continue;
            result.add(s);
        }
        return result;
    }


    // Film: rating valgt -> rating, ellers år valgt -> år  ellers alfabetisk
    private void sortMovies(List<Movies> list, Double minRating, Integer year) {
        if (minRating != null) {
            list.sort(Comparator.comparingDouble(Movies::getRating).reversed());
        } else if (year != null) {
            list.sort(Comparator.comparingInt(Movies::getReleaseDate));
        } else {
            list.sort(Comparator.comparing(Movies::getTitle, String.CASE_INSENSITIVE_ORDER));
        }
    }

    // Serier: rating valgt -> rating, ellers år valgt -> år, ellers alfabetisk
    private void sortSeries(List<Series> list, Double minRating, Integer year) {
        if (minRating != null) {
            list.sort(Comparator.comparingDouble(Series::getRating).reversed());
        } else if (year != null) {
            list.sort(Comparator.comparingInt(Series::getReleaseDate)); // startår
        } else {
            list.sort(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER));
        }
    }
}



