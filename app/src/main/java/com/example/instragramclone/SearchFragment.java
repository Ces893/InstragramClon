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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        CollectionReference userCollection = firestore.collection("users");
        userCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            User user = document.toObject(User.class);

                            users.add(user);
                        }
                        searchAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "No se encontraron posts.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener los posts", e);
                });

        CollectionReference postCollection = firestore.collection("posts");
        postCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Post post = document.toObject(Post.class);

                            posts.add(post);
                        }
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "No se encontraron posts.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener los posts", e);
                });
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvContentSearch = view.findViewById(R.id.rvContentSearch);
        rvContentSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        searchAdapter = new SearchAdapter(users);
        postAdapter = new PostAdapter(posts);

        rvContentSearch.setAdapter(searchAdapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.startsWith("#")) {
                    postAdapter.filter(query.substring(1));
                } else {
                    searchAdapter.filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                if(text.startsWith("#")){
                    rvContentSearch.setAdapter(postAdapter);
                    postAdapter.filter(text.substring(1));
                }
                else {
                    rvContentSearch.setAdapter(searchAdapter);
                    searchAdapter.filter(text);
                }
                return false;
            }
        });

    }
}