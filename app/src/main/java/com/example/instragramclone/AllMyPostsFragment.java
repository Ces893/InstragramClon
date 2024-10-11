package com.example.instragramclone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.clases.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllMyPostsFragment extends Fragment {

    FirebaseFirestore firestore;
    private PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_my_posts, container, false);

        Bundle bundle = getArguments();
        String userId = null;
        if (bundle != null) {
            userId = bundle.getString("userId");
        }

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Post> postsUsuario = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Post post = documentSnapshot.toObject(Post.class);
                                postsUsuario.add(post);
                            }
                            adapter.setData(postsUsuario);
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("Firestore", "Error al obtener los posts", task.getException());
                        }
                    }
                });
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view) {
        RecyclerView rvAllPosts = view.findViewById(R.id.rvAllPostUser);
        rvAllPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PostAdapter(new ArrayList<>());
        rvAllPosts.setAdapter(adapter);
    }
}