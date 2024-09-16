package com.example.instragramclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.example.instragramclone.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private List<Post> elementos = new ArrayList<>();
    private PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setUpRecyclerView(view);
        return (view);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));

        //User userId, String imgUrl, String description
        User user = new User("Usuario1","usuario1");
        User user2 = new User("Usuario2","usuario2");
        User user3 = new User("Usuario3","usuario3");

        elementos.add(new Post(user,"Description Prueba",3300,1000,"usuario4"));
        elementos.add(new Post(user2,"Description Prueba2", 2000,967,"usuario3"));
        elementos.add(new Post(user3,"Description Prueba3", 200,20,"usuario1"));

        adapter = new PostAdapter(elementos);
        rvPost.setAdapter(adapter);
    }
}