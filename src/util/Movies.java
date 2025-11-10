package util;

import java.util.ArrayList;

public class Movies extends Media {
    User currentUser = new User("currentUser");

    public Movies(String title, int releaseDate, double rating, String category, int duration) {
        super(title, releaseDate, rating, category, duration);
    }

    @Override
    public void play(User user) {   //Tilføjet user som argument, så mediet bliver tilføjet til brugeren.
        user.addWatchedMedia(this); //Tilføjer det valgte medie til brugerens already watched arraylist.
        System.out.println(getTitle() + " is now playing... (" + getDuration() + " minutes)");

    }

}

