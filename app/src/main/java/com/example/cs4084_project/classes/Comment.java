package com.example.cs4084_project.classes;

import com.google.firebase.Timestamp;

public class Comment implements Comparable<Comment> {

    private String commenterId;
    private String message;
    private Timestamp date;

    public Comment() {}

    public Comment(String commenterId, String message, Timestamp date) {
        this.commenterId = commenterId;
        this.message = message;
        this.date = date;
    }

    public String getCommenterId() {
        return commenterId;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getDate() {
        return date;
    }

    @Override
    public int compareTo(Comment c) {
        return getDate().compareTo(c.getDate());
    }
}
