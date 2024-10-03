package com.example.instragramclone.clases;

public class User {
    public String id;
    public String userName;
    public String email;
    public String pass;
    public String nombre;
    public String imgUser;
    public String bio;

    public User() {
    }

    public User(String userName, String email, String pass, String nombre, String imgUser,String bio) {
        this.userName = userName;
        this.email = email;
        this.pass = pass;
        this.nombre = nombre;
        this.imgUser = imgUser;
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
