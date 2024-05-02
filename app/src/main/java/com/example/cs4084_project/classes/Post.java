package com.example.cs4084_project.classes;

import com.google.firebase.Timestamp;

public class Post implements Comparable<Post> {

    private String posterId;
    private String profilePicturePath;
    private String username;
    private String title;
    private String description;
    private String imagePath;
    private Timestamp date;
    private Cafe cafe;
    private int rating;

    public Post() {}

    public Post(String posterId, String profilePicturePath, String username, String title, String description, Timestamp date) {
        this.posterId = posterId;
        this.profilePicturePath = profilePicturePath;
        this.username = username;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public Post(String profilePicturePath, String username, String title, String description, String imagePath, Timestamp date, String posterId) {
        this(posterId, profilePicturePath, username, title, description, date);
        this.imagePath = imagePath;
    }

    public Post(String profilePicturePath, String username, String title, String description, Timestamp date, String posterId, Cafe cafe, int rating) {
        this(posterId, profilePicturePath, username, title, description, date);
        this.cafe = cafe;
        this.rating = rating;
    }

    public Post(String profilePicturePath, String username, String title, String description, String imagePath, Timestamp date, String posterId, Cafe cafe, int rating) {
        this(profilePicturePath, username, title, description, imagePath, date, posterId);
        this.cafe = cafe;
        this.rating = rating;
    }


    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Cafe getCafe() {
        return cafe;
    }

    public void setCafe(Cafe cafe) {
        this.cafe = cafe;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getPosterId() {
        return posterId;
    }

    @Override
    public int compareTo(Post p) {
        return getDate().compareTo(p.getDate());
    }
}
