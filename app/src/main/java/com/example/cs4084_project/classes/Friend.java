package com.example.cs4084_project.classes;

public class Friend {
    private String uid;
    private String username;
    private String profilePic;

    public Friend() {}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfilePic(String profile_pic) {
        this.profilePic = profile_pic;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }

}
