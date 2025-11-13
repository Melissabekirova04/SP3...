package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class MainMenu {
    // Hjælpeklasser
    private final TextUI ui = new TextUI();
    private final FileIO io = new FileIO(); // bruges til at læse/skrives CSV

    private final List<User> users = new ArrayList<>();
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
            String name = parts[0].trim();

            String password;
            if (parts.length > 1) {
                password = parts[1].trim();
            } else {
                password = "";
            }
            if (!name.isEmpty()) {
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
        String password = ui.promptText("Create password: ");

        this.user = new User(name, password);
        this.users.add(this.user);
        saveUsersToCsv(); // gemmer efter oprettelse
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
                User found = null;
                while (found == null) { // while loop der sørger for at programmet ikke lukker ned hvis man taster forkert
                    String existingName = ui.promptText("Enter your username: ");
                    String password = ui.promptText("Enter your password: ");

                    for (User u : this.users) { // et for each loop som tjekker en user af gangen
                        // if statement der sammenligner brugerens navn og password i listen, ift. hvad brugeren skriver
                        if (u.getName().equalsIgnoreCase(existingName) && u.getPassword().equals(password)) {
                            found = u; // hvis dette er sandt, har vi fundet brugeren
                            break;
                        }
                    }
                    if (found == null) {
                        ui.displayMsg("Wrong username or password. Please try again.");
                    }
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
        ui.displayMsg("Loaded " + this.movies.size() + " movies.");
        ui.displayMsg("Loaded " + series.size() + " series.");
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
                    String type = ui.promptText("Do you want to search for:\n1) Movies\n2) Series\nChoose 1 or 2: ");
                    searchByTitle(type);
                    break;
                case "2":
                    searchByCategory();
                    break;
                case "3":
                    showSavedForLater();
                    break;
                case "4":
                    showAlreadyWatched();
                    break;
                case "5":
                    filterMenu();
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

    // --------- HJÆLPEMETODER TIL PARSING ---------

    private Double parseRating(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(input.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            ui.displayMsg("Invalid rating format. Ignoring rating filter.");
            return null;
        }
    }

    private Integer parseYear(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            ui.displayMsg("Invalid year format. Ignoring year filter.");
            return null;
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
                    String minRatingStr = ui.promptText("Minimum rating (7.5) (leave empty for any): ").trim();
                    String yearStr = ui.promptText("Year  1994) (leave empty for any): ").trim();

                    Double minRating = parseRating(minRatingStr);
                    Integer year = parseYear(yearStr);

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
                    String minRatingStr = ui.promptText("Minimum rating (7.5) (leave empty for any): ").trim();
                    String yearStr = ui.promptText("Year (2010) (leave empty for any): ").trim();

                    Double minRating = parseRating(minRatingStr);
                    Integer year = parseYear(yearStr);

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
                ui.displayMsg("Search option '" + searchOption + "' is invalid. Please choose '1' for movies or '2' for series.");
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
                String minRatingStr = ui.promptText("Minimum rating (7.5). Leave empty to skip: ").trim();
                String yearStr = ui.promptText("Exact release year (2001). Leave empty to skip: ").trim();

                Double minRating = parseRating(minRatingStr);
                Integer year = parseYear(yearStr);

                // kategori er allerede filtreret i 'found', så vi sender null for kategori her
                found = filterMoviesOnSource(found, null, minRating, year);
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
                    "Apply additional filters to these series? (yes/no)\n" +
                            "→ Example: rating 8.5, year 2015 (press Enter to skip)"
            ).trim();

            if (filterChoice.equalsIgnoreCase("y")) {
                String minRatingStr = ui.promptText("Minimum rating (7.0). Leave empty to skip: ").trim();
                String yearStr = ui.promptText("Exact start year (2010). Leave empty to skip: ").trim();

                Double minRating = parseRating(minRatingStr);
                Integer year = parseYear(yearStr);

                // kategori er allerede filtreret i 'found', så vi sender null for kategori her
                found = filterSeriesOnSource(found, null, minRating, year);
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
            ui.displayMsg((i + 1) + ") " + m.getTitle() + " (" + m.getReleaseDate() + ") "
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
            ui.displayMsg((i + 1) + ") " + serie.getTitle() + " (" + serie.getReleaseDate() + ") "
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

    private void showSavedForLater() {
        if (user.getSavedForLater().size() >= 1) { //Hvis brugerens antal af set medie er større end 0 vis listen.
            ui.displayMsg("-------------Saved for later-------------");
            for (int i = 0; i < user.getSavedForLater().size(); i++) {
                Media m = user.getSavedForLater().get(i);
                ui.displayMsg((i + 1) + ") " + m.getTitle() + " (" + m.getReleaseDate() + "), " + m.getCategory() + ", " + m.getRating());
            }
            String choice = ui.promptText("Choose the number of a movie/show you want to select or press 0 to go back to the main menu: ");
            try {
                int number = Integer.parseInt(choice);
                if (number == 0) return;
                Media selectedMedia = user.getSavedForLater().get(number - 1);
                selectedMedia.play(user);
            } catch (Exception e) {
                ui.displayMsg("Invalid choice. Please try again.");
            }

        } else {
            ui.promptText("Nothing added to watch later... Press any key to return to the main menu.");
            return;
        }
    }

    private void showAlreadyWatched() {
        if (user.getWatchedMedia().size() >= 1) { //Hvis brugerens antal af set medie er større end 0 vis listen.
            ui.displayMsg("-------------Already watched-------------");
            for (int i = 0; i < user.getWatchedMedia().size(); i++) {
                Media m = user.getWatchedMedia().get(i);
                ui.displayMsg((i + 1) + ") " + m.getTitle() + " (" + m.getReleaseDate() + "), " + m.getCategory() + ", " + m.getRating());
            }
            String choice = ui.promptText("Choose the number of a movie/show you want to select or press 0 to go back to the main menu: ");
            try {
                int number = Integer.parseInt(choice);
                if (number == 0) return;
                Media selectedMedia = user.getWatchedMedia().get(number - 1);
                selectedMedia.play(user);
            } catch (Exception e) {
                ui.displayMsg("Invalid choice. Please try again.");
            }

        } else {
            ui.promptText("Nothing watched yet... Press any key to return to the main menu.");
            return;
        }
    }

    // -------------------- HJÆLPE-METODER TIL FILTER & SORT --------------------

    // Generel filter-menu: lader brugeren vælge type (film/serie) og filter (kategori/rating/år)
    private void filterMenu() {
        // Spørger hvad der skal filtreres: film eller serier
        String type = ui.promptText("Filter what? Type 'movie' or 'series' (or empty to cancel): ").trim();
        if (type.isEmpty()) return; // tomt svar = afbryd

        // Spørger hvilken slags filter der skal bruges
        String filterChoice = ui.promptText("Filter by: 'category', 'rating' or 'year': ").trim().toLowerCase();

        // Hvis der filtreres på film
        if (type.equalsIgnoreCase("movie")) {
            // Starter med en kopi af alle film
            ArrayList<Movies> result = new ArrayList<>(movies);

            // Vælger filter-type for film
            switch (filterChoice) {
                case "category": {
                    // Bruger skriver en kategori-tekst, fx "Drama"
                    String category = ui.promptText("Enter category (e.g. Drama, War, Crime): ").trim();
                    // Filtrer: fjern alle film som ikke indeholder kategorien i deres kategori-tekst
                    result.removeIf(m -> !m.getCategory().toLowerCase().contains(category.toLowerCase()));
                    // Sorter alfabetisk efter titel (A → Z)
                    result.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                    break;
                }
                case "rating": {
                    // Sorter efter rating, højeste rating først
                    result.sort((a, b) -> Double.compare(b.getRating(), a.getRating())); // høj → lav
                    break;
                }
                case "year": {
                    // Spørger efter årstal
                    String yearStr = ui.promptText("Enter year (e.g. 1994): ").trim();
                    Integer year = parseYear(yearStr); // prøver at lave det om til et tal
                    if (year == null) return; // hvis ugyldigt input, stoppes metoden

                    // Filtrer: behold kun film hvor release year = valgt år
                    Integer finalYear = year;
                    result.removeIf(m -> m.getReleaseDate() != finalYear);
                    // Sorter stigende efter år (hvis der mod forventning er flere år)
                    result.sort((a, b) -> Integer.compare(a.getReleaseDate(), b.getReleaseDate()));
                    break;
                }
                default:
                    // Hvis brugeren skrev noget andet end category/rating/year
                    ui.displayMsg("Invalid filter type. Please choose 'category', 'rating' or 'year'.");
                    return;
            }

            // Hvis ingen film matcher, sig det til brugeren
            if (result.isEmpty()) {
                ui.displayMsg("No movies matched your filters.");
            } else {
                // Ellers vis listen og lad brugeren vælge noget at afspille
                showMoviesAndPlay(result);
            }

            // Hvis der filtreres på serier
        } else if (type.equalsIgnoreCase("series")) {
            // Starter med en kopi af alle serier
            ArrayList<Series> result = new ArrayList<>(series);

            // Vælger filter-type for serier
            switch (filterChoice) {
                case "category": {
                    // Bruger skriver en kategori-tekst
                    String category = ui.promptText("Enter category (e.g. Drama, War, Crime): ").trim();
                    // Filtrer: fjern serier der ikke matcher kategorien
                    result.removeIf(s -> !s.getCategory().toLowerCase().contains(category.toLowerCase()));
                    // Sorter alfabetisk efter titel
                    result.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                    break;
                }
                case "rating": {
                    // Sorter serier efter rating, bedste først
                    result.sort((a, b) -> Double.compare(b.getRating(), a.getRating())); // høj → lav
                    break;
                }
                case "year": {
                    // Spørger efter startår for serien
                    String yearStr = ui.promptText("Enter start year (e.g. 2010): ").trim();
                    Integer year = parseYear(yearStr);
                    if (year == null) return;

                    // Filtrer: behold kun serier der starter i det år
                    Integer finalYear = year;
                    result.removeIf(s -> s.getReleaseDate() != finalYear);
                    // Sorter stigende efter år
                    result.sort((a, b) -> Integer.compare(a.getReleaseDate(), b.getReleaseDate()));
                    break;
                }
                default:
                    // Ugyldigt filtervalg
                    ui.displayMsg("Invalid filter type. Please choose 'category', 'rating' or 'year'.");
                    return;
            }

            // Hvis ingen serier matcher, sig det
            if (result.isEmpty()) {
                ui.displayMsg("No series matched your filters.");
            } else {
                // Ellers vis liste og lad brugeren vælge en serie
                showSeriesAndPlay(result);
            }

        } else {
            // Hvis brugeren ikke skrev "movie" eller "series"
            ui.displayMsg("Invalid type. Please choose 'movie' or 'series'.");
        }
    }

    // Filtrér film på en given liste (bruges af både menu 5 og søgning)
    // Her kan vi filtrere på kategori, minimum rating og år (alle er valgfrie)
    private ArrayList<Movies> filterMoviesOnSource(List<Movies> source, String category, Double minRating, Integer year) {
        ArrayList<Movies> result = new ArrayList<>();
        boolean hasCategory = category != null && !category.isEmpty(); // tjekker om kategori-filteret er aktivt

        for (Movies m : source) {
            // Hvis vi filtrerer på kategori og filmen ikke matcher, spring den over
            if (hasCategory && !m.getCategory().toLowerCase().contains(category.toLowerCase())) continue;
            // Hvis vi filtrerer på minimum rating, og filmen har lavere rating, spring den over
            if (minRating != null && m.getRating() < minRating) continue;
            // Hvis vi filtrerer på år, og filmen ikke har det år, spring den over
            if (year != null && m.getReleaseDate() != year) continue;
            // Hvis alle filtre er passeret, tilføj filmen til resultatlisten
            result.add(m);
        }
        return result;
    }

    // Filtrér serier på en given liste (bruges af både menu 5 og søgning)
    // Samme logik som filterMoviesOnSource, bare for Series
    private ArrayList<Series> filterSeriesOnSource(List<Series> source, String category, Double minRating, Integer year) {
        ArrayList<Series> result = new ArrayList<>();
        boolean hasCategory = category != null && !category.isEmpty(); // tjekker om kategori-filteret er aktivt

        for (Series s : source) {
            // Kategori-filter
            if (hasCategory && !s.getCategory().toLowerCase().contains(category.toLowerCase())) continue;
            // Rating-filter
            if (minRating != null && s.getRating() < minRating) continue;
            // Års-filter (releaseDate bruges som startår)
            if (year != null && s.getReleaseDate() != year) continue;
            // Hvis alle filtre gik igennem, tilføjes serien
            result.add(s);
        }
        return result;
    }

    // Sorterer film-listen alt efter hvilke filtre der er brugt:
    // hvis der er valgt minRating → sorter efter rating
    // ellers hvis der er valgt år → sorter efter år
    // ellers sorter alfabetisk efter titel
    private void sortMovies(List<Movies> list, Double minRating, Integer year) {
        if (minRating != null) {
            // Sortér efter rating (bedst først)
            list.sort(Comparator.comparingDouble(Movies::getRating).reversed());
        } else if (year != null) {
            // Sortér efter år (stigende)
            list.sort(Comparator.comparingInt(Movies::getReleaseDate));
        } else {
            // Standard: sorter alfabetisk efter titel
            list.sort(Comparator.comparing(Movies::getTitle, String.CASE_INSENSITIVE_ORDER));
        }
    }

    // Samme som sortMovies, bare for serier
    private void sortSeries(List<Series> list, Double minRating, Integer year) {
        if (minRating != null) {
            // Sortér efter rating (bedst først)
            list.sort(Comparator.comparingDouble(Series::getRating).reversed());
        } else if (year != null) {
            // Sortér efter startår for serien
            list.sort(Comparator.comparingInt(Series::getReleaseDate)); // startår
        } else {
            // Standard: alfabetisk på titel
            list.sort(Comparator.comparing(Series::getTitle, String.CASE_INSENSITIVE_ORDER));
        }
    }
}
