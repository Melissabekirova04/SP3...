package util;

public class Series extends Media {

    public Series(String title, int releaseDate, int endDate, double rating, String category, int duration) {
        super(title, releaseDate, endDate, rating, category, duration);
    }

    @Override
    public void play(User user) {
        System.out.println(getTitle() + " is now playing... (" + getDuration() + " minutes)");
    }
}
