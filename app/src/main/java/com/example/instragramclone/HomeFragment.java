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
import com.example.instragramclone.clases.Follow;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.example.instragramclone.service.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    private PostAdapter adapter;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        CollectionReference followsCollection = firestore.collection("follows");
        DocumentReference loggedInUserFollowRef = followsCollection.document(uid);

        loggedInUserFollowRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Follow follow = documentSnapshot.toObject(Follow.class);

                if (follow != null) {
                    List<String> followingIds = follow.getFollowing();
                    if (followingIds.isEmpty()) {
                        Log.d("Firestore", "No estás siguiendo a ningún usuario.");
                        return;
                    }
                    CollectionReference postCollection = firestore.collection("posts");
                    postCollection.whereIn("userId", followingIds)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<Post> postList = new ArrayList<>();

                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        Post post = document.toObject(Post.class);
                                        postList.add(post);
                                    }
                                    adapter.setData(postList);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.d("Firestore", "No se encontraron posts.");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w("Firestore", "Error al obtener los posts", e);
                            });
                }
            }
        });

//        CollectionReference postCollection = firestore.collection("posts");
//        postCollection.get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        List<Post> postList = new ArrayList<>();
//
//                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                            Post post = document.toObject(Post.class);
//
//                            postList.add(post);
//                        }
//                        adapter.setData(postList);
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        Log.d("Firestore", "No se encontraron posts.");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.w("Firestore", "Error al obtener los posts", e);
//                });

        setUpRecyclerView(view);
        return (view);
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(new ArrayList<>());
        rvPost.setAdapter(adapter);
    }
}