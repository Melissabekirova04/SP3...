package util;

import java.util.ArrayList;

public class User {
    private String name;
    private ArrayList<Media>savedForLater = new ArrayList<>();
    private ArrayList<Media>watchedMedia = new ArrayList<>();

    public User(String name) { this.name = name; }

    public String getName() { return name; }

    public ArrayList<Media> getSavedForLater() {
        return savedForLater;
    }

    public ArrayList<Media> getWatchedMedia() {
        return watchedMedia;
    }

    public void addWatchedMedia(Media media) {
        this.watchedMedia.add(media);
    }

    public void addSavedForLater(Media media) {this.savedForLater.add(media);}

    public void removeSavedForLater(Media media) {this.savedForLater.remove(media);}

}