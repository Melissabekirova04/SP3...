package util;

public abstract class Media {

    // Fields should be final since they never change
    private final String title;
    private final int releaseDate;
    private final double rating;
    private final String category;
    private final int duration;

    // Only used for series (end year)
    private final Integer endDate;

    // Full constructor (for Series)
    public Media(String title, int releaseDate, int endDate, double rating, String category, int duration) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.endDate = endDate;
        this.rating = rating;
        this.category = category;
        this.duration = duration;
    }

    // Simplified constructor (for Movies)
    public Media(String title, int releaseDate, double rating, String category, int duration) {
        this(title, releaseDate, -1, rating, category, duration); // -1 meaning "no end date"
    }

    // getters
    public String getTitle() { return title; }
    public int getReleaseDate() { return releaseDate; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }
    public int getDuration() { return duration; }

    // endDate only relevant for Series
    public Integer getEndDate() { return endDate; }

    public abstract void play(User user);

    @Override
    public String toString() {
        return title + " (" + releaseDate + ") "
                + "Category: " + category + " ⭐ " + rating;
    }
}
