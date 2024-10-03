package com.example.instragramclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instragramclone.activity.LoginActivity;
import com.example.instragramclone.adapter.MyPostAdapter;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PerfilFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private MyPostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


        ImageView userImage = view.findViewById(R.id.userImage);
        TextView userName = view.findViewById(R.id.userName);
        TextView fullName = view.findViewById(R.id.fullName);
        TextView postNum = view.findViewById(R.id.postsCount);

        Button btnLogOut = view.findViewById(R.id.btnLogOut);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Info User //
        String uid = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        userName.setText(user.getUserName());
                        fullName.setText(user.getNombre());

                        Picasso.get()
                                .load(user.getImgUser())
                                .placeholder(R.drawable.ic_rounded_account_circle_24)
                                .error(R.drawable.ic_launcher_background)
                                .into(userImage);
                    }
                } else {
                    Toast.makeText(getContext(), "Error: El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Posts //
        RecyclerView rvPosts = view.findViewById(R.id.rvPostsUserP);
        rvPosts.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        rvPosts.setLayoutManager(gridLayoutManager);

        firestore.collection("posts")
                .whereEqualTo("userId", uid)
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
                            adapter = new MyPostAdapter(postsUsuario,getContext());
                            rvPosts.setAdapter(adapter);
                            //
                            int numPosts = postsUsuario.size();
                            postNum.setText(String.valueOf(numPosts));
                            //
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("Firestore", "Error al obtener los posts", task.getException());
                        }
                    }
                });



        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                //finish();
            }
        });
        return view;
    }
}