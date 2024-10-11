package com.example.instragramclone.clases;

import java.util.ArrayList;
import java.util.List;

public class Like {
    public String id;
    public String postId;
    public List<String> likersUser;

    public Like() {
        this.likersUser = new ArrayList<>();
    }

    public Like(String id, String postId) {
        this.id = id;
        this.postId = postId;
        this.likersUser = new ArrayList<>();;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<String> getLikersUser() {
        return likersUser;
    }

    public void setLikersUser(List<String> likersUser) {
        this.likersUser = likersUser;
    }
}
