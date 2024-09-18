package com.example.instragramclone.clases;

import java.util.Date;

public class Post {

    public int postId;
    public User userName;
    public String imgUrl;
    public String description;
    public int likeCount;
    public int commentsCount;
    public Date publicacionDate;
    public String etiqueta;


    public Post(User user, String description, int likeCount, int commentsCount, String imgUrl, String etiqueta) {
        this.userName = user;
        this.imgUrl = imgUrl;
        this.description = description;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
        this.publicacionDate = new Date();
        this.etiqueta = etiqueta;
    }
}
