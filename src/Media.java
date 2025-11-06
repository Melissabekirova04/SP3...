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
    }


}
