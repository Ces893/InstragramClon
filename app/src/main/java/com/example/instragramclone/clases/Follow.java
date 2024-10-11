package com.example.instragramclone.clases;

import java.util.ArrayList;
import java.util.List;

public class Follow {
    public String id;
    public String userId;
    public List<String> followers;
    public List<String> following;

    public Follow() {
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public Follow(String id,String userId) {
        this.id = id;
        this.userId = userId;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }
}
