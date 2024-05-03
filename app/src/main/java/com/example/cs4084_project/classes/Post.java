package com.example.cs4084_project.classes;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Post implements Comparable<Post> {

    private String postId;
    private String posterId;
    private String cafeId;
    private String profilePicturePath;
    private String username;
    private String title;
    private String description;
    private String imagePath;
    private Timestamp date;
    private Cafe cafe;
    private int rating;
    private int likes;
    private int dislikes;
    private ArrayList<Comment> comments = new ArrayList<>();

    public Post() {}

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

    public int getLikes() {
        return likes;
    }

    public void addLike() {
        likes++;
    }

    public void removeLike() {
        likes--;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void addDislike() {
        dislikes++;
    }

    public void removeDislike() {
        dislikes--;
    }

    public String getPostId() {
        return postId;
    }

    public String getCafeId() {
        return cafeId;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }
}
