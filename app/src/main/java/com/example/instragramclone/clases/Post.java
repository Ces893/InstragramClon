package com.example.instragramclone.clases;

import java.util.Date;

public class Post {

    public String postId;
    public String userId;
    public String imgUrl;
    public String description;
    public int likeCount;
    public int commentsCount;
    public Date publicacionDate;
    public String etiqueta;

    public Post(){}

    public Post(String user, String description, int likeCount, int commentsCount, String imgUrl, String etiqueta) {
        this.userId = user;
        this.imgUrl = imgUrl;
        this.description = description;
        this.likeCount = likeCount;
        this.commentsCount = commentsCount;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
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
