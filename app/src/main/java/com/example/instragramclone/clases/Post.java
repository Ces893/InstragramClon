package com.example.instragramclone.clases;

import java.util.Date;

public class Post {

    public String postId;
    public String userId;
    public String imgUrl;
    public String description;
    public Date publicacionDate;
    public String etiqueta;

    public Post(){}

    public Post(String postId,String user, String description, String imgUrl, String etiqueta) {
        this.postId = postId;
        this.userId = user;
        this.imgUrl = imgUrl;
        this.description = description;
        this.publicacionDate = new Date();
        this.etiqueta = etiqueta;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPublicacionDate() {
        return publicacionDate;
    }

    public void setPublicacionDate(Date publicacionDate) {
        this.publicacionDate = publicacionDate;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
