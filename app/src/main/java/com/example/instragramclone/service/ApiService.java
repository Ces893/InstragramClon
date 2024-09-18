package com.example.instragramclone.service;

import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService
{
    @GET("/User")
    Call < List<User> > getAll();

    @GET("/Post")
    Call <List<Post> > getAllPost();

    @POST("/Post")
    Call <Post> create(@Body Post post);
}
