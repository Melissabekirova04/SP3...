package util;

public class Movies extends Media {

    public Movies(String title, int releaseDate, double rating, String category, int duration) {
        super(title, releaseDate, rating, category, duration);
    }

    @Override
    public void play() {
        System.out.println(getTitle() + " is now playing... (" + getDuration() + " minutes)");
    }
}

