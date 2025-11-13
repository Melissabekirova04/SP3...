package util;

import java.util.ArrayList;

public class User {
    private String name;
    private ArrayList<Media> savedForLater = new ArrayList<>();
    private ArrayList<Media> watchedMedia = new ArrayList<>();
    private String password;

    public String getName() {
        return name;
    }

    public ArrayList<Media> getSavedForLater() {
        return savedForLater;
    }

    public ArrayList<Media> getWatchedMedia() {
        return watchedMedia;
    }

    public void addWatchedMedia(Media media) {
        this.watchedMedia.add(media);
    }

    public void addSavedForLater(Media media) {
        this.savedForLater.add(media);
    }

    public void removeSavedForLater(Media media) {
        this.savedForLater.remove(media);
    }

    // constructor til username og password
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    //Gennembruger gamlle constructor. password = tomt, hvis ikke angivet
    public User(String name) {
        this(name, "");
    }
}