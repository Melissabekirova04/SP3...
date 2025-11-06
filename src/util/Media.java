package util;

public abstract class Media {
    private String title;
    private int releaseDate;
    private double rating;
    private String category;
    private int duration;

    public Media(String title, int releaseDate, double rating, String category, int duration) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.category = category;
        this.duration = duration;
    }

    // getters
    public String getTitle() { return title; }
    public int getReleaseDate() { return releaseDate; }
    public double getRating() { return rating; }
    public String getCategory() { return category; }
    public int getDuration() { return duration; }

    // skal implementeres i underklasser (fx Movies)
    public abstract void play();
}

