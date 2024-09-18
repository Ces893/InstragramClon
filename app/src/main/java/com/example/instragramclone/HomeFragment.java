package com.example.instragramclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66e47472d2405277ed145ab4.mockapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        service.getAllPost().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful()){
                    Log.d("API_RESPONSE", "Datos recibidos: " + response.body().size());
                    //elementos.clear();
                    //elementos.addAll(response.body());
                    adapter.setData(response.body());
                    adapter.notifyDataSetChanged();
                }else {
                    Log.e("API_ERROR", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable throwable) {
                Log.e("MAIN_APP", throwable.getMessage());
            }
        });

        setUpRecyclerView(view);
        return (view);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));

//        //User userId, String imgUrl, String description
//        User user = new User("Usuario1","usuario1");
//        User user2 = new User("Usuario2","usuario2");
//        User user3 = new User("Usuario3","usuario3");
//
//        elementos.add(new Post(user,"Description Prueba",3300,1000,"usuario4","et1"));
//        elementos.add(new Post(user2,"Description Prueba2", 2000,967,"usuario3", "et2"));
//        elementos.add(new Post(user3,"Description Prueba3", 200,20,"usuario1", "et3"));

        adapter = new PostAdapter(new ArrayList<>());
        rvPost.setAdapter(adapter);
    }
}