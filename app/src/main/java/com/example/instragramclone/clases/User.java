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

    public User(String userName, String imgUser) {
        this.userName = userName;
        this.imgUser = imgUser;
    }

    public User(String id, String userName, String email, String pass, String nombre, String imgUser,String bio) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.pass = pass;
        this.nombre = nombre;
        this.imgUser = imgUser;
        this.bio = bio;
    }
}
