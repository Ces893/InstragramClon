package com.example.instragramclone;

import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.clases.Follow;
import com.example.instragramclone.clases.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private PostAdapter adapter;
    private RecyclerView rvPost;
    private ProgressBar progressBar;

    private DocumentSnapshot lastVisible;  // Para rastrear el último documento cargado
    private boolean isLoading = false;     // Controla el estado de carga
    private final int PAGE_SIZE = 5;      // Tamaño de página para la paginación

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progB);
        rvPost = view.findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(new ArrayList<>());
        rvPost.setAdapter(adapter);

        NestedScrollView nestedScrollView = view.findViewById(R.id.scrollView);

        // Listener para el scroll del NestedScrollView
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(0).getBottom() <= (v.getHeight() + v.getScrollY())) {
                    // Se ha llegado al final del scroll, cargar más posts
                    loadMorePosts();
                }
            }
        });

        loadMorePosts();
        return view;
    }

    private void loadMorePosts() {
        if (isLoading) return;  // Evitar cargas dobles si ya se está cargando

        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);

        String uid = firebaseAuth.getCurrentUser().getUid();
        CollectionReference followsCollection = firestore.collection("follows");

        followsCollection.whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Follow follow = documentSnapshot.toObject(Follow.class);

                        List<String> followingIds = follow != null ? follow.getFollowing() : new ArrayList<>();
                        CollectionReference postCollection = firestore.collection("posts");

                        if (followingIds.isEmpty()) {
                            loadPublicityPosts(postCollection);
                        } else {
                            loadFollowingPosts(postCollection, followingIds);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    isLoading = false;
                    new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
                    Log.w("Firestore", "Error al obtener los datos del usuario en follows", e);
                });
    }

    private void loadPublicityPosts(CollectionReference postCollection) {
        Query query = postCollection.whereEqualTo("etiqueta", "#publicidad")
                .limit(PAGE_SIZE);

        if (lastVisible != null) {  // Si no es la primera carga, usar paginación
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<Post> postList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Post post = document.toObject(Post.class);
                    postList.add(post);
                }
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                adapter.addData(postList);

            } else {
                Log.d("Pagination", "No more publicity posts to load");
            }
            isLoading = false;
            new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
        }).addOnFailureListener(e -> {
            isLoading = false;
            new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
            Log.w("Firestore", "Error al cargar posts de publicidad", e);
        });
    }

    private void loadFollowingPosts(CollectionReference postCollection, List<String> followingIds) {
        Query query = postCollection.whereIn("userId", followingIds)
                .orderBy("publicacionDate", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE);

        if (lastVisible != null) {  // Si no es la primera carga, usar paginación
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                List<Post> postList = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Post post = document.toObject(Post.class);
                    postList.add(post);
                }
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                adapter.addData(postList);

            } else {
                Log.d("Pagination", "No more posts from following to load");
            }
            isLoading = false;
            new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
        }).addOnFailureListener(e -> {
            isLoading = false;
            new Handler().postDelayed(() -> progressBar.setVisibility(View.GONE), 2000);
            Log.w("Firestore", "Error al cargar posts de seguidores", e);
        });
    }
}



