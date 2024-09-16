package com.example.instragramclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.adapter.SearchAdapter;
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

public class SearchFragment extends Fragment {

    private SearchAdapter searchAdapter;
    private List<User> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66e47472d2405277ed145ab4.mockapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        service.getAll().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    users.addAll(response.body());
                    searchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable throwable) {
                Log.e("MAIN_APP", throwable.getMessage());
            }
        });
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvContentSearch = view.findViewById(R.id.rvContentSearch);
        rvContentSearch.setLayoutManager(new LinearLayoutManager(getContext()));

//        //User userId, String imgUrl, String description
//        User user = new User("ABD","usuario4");
//        User user2 = new User("YUH","usuario2");
//        User user3 = new User("KIL","usuario3");
//        User user4 = new User("DFG","usuario4");
//        User user5 = new User("GAB","usuario4");
//
//        users.add(user);
//        users.add(user2);
//        users.add(user3);
//        users.add(user4);
//        users.add(user5);

        searchAdapter = new SearchAdapter(users);
        rvContentSearch.setAdapter(searchAdapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                searchAdapter.filter(text);
                return false;
            }
        });

    }
}