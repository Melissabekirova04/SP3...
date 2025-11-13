package util;

public class Series extends Media {
    private final TextUI ui = new TextUI();

    public Series(String title, int releaseDate, int endDate, double rating, String category, int duration) {
        super(title, releaseDate, endDate, rating, category, duration);
    }

    @Override
    public void play(User user) {   // Tilføjet user som argument, så mediet bliver tilføjet til brugeren.
        boolean running = true;

        while (running) {
            String choice = ui.promptText(
                    "Do you want to:\n" +
                            "1) Play " + getTitle() + "\n" +
                            "2) Add to watch later\n" +
                            "3) Delete from watch later\n" +
                            "0) Go back to main menu"
            );

            switch (choice) {
                case "1" -> {
                    // Tilføj til already watched og fjern fra watch later (hvis den var der)
                    user.addWatchedMedia(this);
                    user.removeSavedForLater(this);
                    ui.displayMsg(getTitle() + " is now playing... (" + getDuration() + " minutes)");
                    running = false; // tilbage til menuen der kaldte play()
                }

                case "2" -> {
                    if (user.getSavedForLater().contains(this)) { // Tjekker om den allerede er gemt
                        ui.displayMsg(getTitle() + " is already in your 'watch later' list.");
                    } else {
                        user.addSavedForLater(this);
                        ui.promptText("Added " + getTitle() + " to watch later. Press any key to return to the main menu.");
                        running = false;
                    }
                }

                case "3" -> {
                    if (user.getSavedForLater().contains(this)) {
                        user.removeSavedForLater(this);
                        ui.promptText(getTitle() + " was removed from your watch later list.\nPress any key to return to the main menu.");
                        running = false;
                    } else {
                        ui.displayMsg(getTitle() + " could not be removed because it was never added to the list.");
                        // her bliver vi i while-løkken, så brugeren kan vælge noget andet
                    }
                }

                case "0" -> {
                    ui.displayMsg("Returning to main menu.");
                    running = false;
                }

                default -> ui.displayMsg("Invalid choice, please try again.");
            }
        }
    }
}
