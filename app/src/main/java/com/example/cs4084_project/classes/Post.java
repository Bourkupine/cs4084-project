package com.example.cs4084_project.classes;

public class Post {
    private String profilePicturePath;
    private String username;
    private String title;
    private String description;
    private String imagePath;
//    private Cafe cafe;
//    private int rating;

    public Post(String profilePicturePath, String username, String title, String description, String imagePath) {
        this.profilePicturePath = profilePicturePath;
        this.username = username;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
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
}
