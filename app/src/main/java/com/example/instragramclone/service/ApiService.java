package com.example.instragramclone.service;

import com.example.instragramclone.clases.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService
{
    @GET("/User")
    Call < List<User> > getAll();
}
