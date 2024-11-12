package com.example.instragramclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instragramclone.activity.LoginActivity;
import com.example.instragramclone.adapter.MyPostAdapter;
import com.example.instragramclone.adapter.SearchAdapter;
import com.example.instragramclone.clases.Follow;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserPerfilFragment extends Fragment {

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    private MyPostAdapter adapter;
    Button btnFollow;
    Follow follow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_perfil, container, false);

        ImageView userImage = view.findViewById(R.id.userImage);
        TextView userName = view.findViewById(R.id.userName);
        TextView fullName = view.findViewById(R.id.fullName);
        TextView postNum = view.findViewById(R.id.postsCount);
        TextView txtUserN = view.findViewById(R.id.txUserName);
        //
        TextView numFollowers = view.findViewById(R.id.followersCount);
        TextView numFollowing = view.findViewById(R.id.followingCount);

        Button btnLogOut = view.findViewById(R.id.btnLogOutUser);
        Button btnEdit = view.findViewById(R.id.btnEditProfile);
        Button btnCompartir = view.findViewById(R.id.btnShareProfile);
        btnFollow = view.findViewById(R.id.btnFollow);

        //
        LinearLayout linearLySeguidores = view.findViewById(R.id.linelySeguidores);
        LinearLayout linearLySeguidos = view.findViewById(R.id.linerlySeguidos);
        ImageView imgBack = view.findViewById(R.id.imgBackPer);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Bundle bundle = getArguments();
        String userId = "";
        if (bundle != null) {
            userId = bundle.getString("userId");
        }

        String uid = firebaseAuth.getCurrentUser().getUid();

        if (userId == null || userId.isEmpty()) {
            userId = uid;
        }

        if(userId.equals(uid)){
            btnLogOut.setVisibility(view.VISIBLE);
            btnEdit.setVisibility(View.VISIBLE);
            btnCompartir.setVisibility(View.VISIBLE);
            imgBack.setVisibility(view.GONE);
            btnFollow = view.findViewById(R.id.btnFollow);
            btnFollow.setVisibility(view.GONE);
        }else {
            btnLogOut.setVisibility(view.GONE);
            btnEdit.setVisibility(View.GONE);
            btnCompartir.setVisibility(View.GONE);
        }


        DocumentReference documentReference = firestore.collection("users").document(userId);
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
                        txtUserN.setText(user.getUserName());

                        Picasso.get()
                                .load(user.getImgUser())
                                .placeholder(R.drawable.ic_rounded_account_circle_24)
                                .error(R.drawable.ic_launcher_background)
                                .into(userImage);
                    }
                }else {
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


        //
        //loEstaSiguiendo(uid, userId);
        numFollows(userId, numFollowers,numFollowing);


        String userFollow = userId;
        FollowingUsers(uid, userFollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FollowingUsers(uid, userFollow);
            }
        });

        linearLySeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> listSeguidores = follow.getFollowers();
                String nombreUser = userName.getText().toString();
                SearchFragment searchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ListSeguidores", (ArrayList<String>) listSeguidores);
                bundle.putString("nombreUser",nombreUser);
                searchFragment.setArguments(bundle);

                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment).addToBackStack(null).commit();
            }
        });

        linearLySeguidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> listSeguidores = follow.getFollowing();
                String nombreUser = userName.getText().toString();
                SearchFragment searchFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("ListSeguidos", (ArrayList<String>) listSeguidores);
                bundle.putString("nombreUser",nombreUser);
                searchFragment.setArguments(bundle);

                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, searchFragment).addToBackStack(null).commit();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }

    private void loEstaSiguiendo(String uid, String userId){
        DocumentReference logUserFollow = firestore.collection("follows").document(uid);
        logUserFollow.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("FirestoreError", "Error al obtener los datos: ", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                Follow follow = documentSnapshot.toObject(Follow.class);
                if (follow != null && follow.getFollowing().contains(userId)) {
                    btnFollow.setText("Siguiendo");
                } else {
                    btnFollow.setText("Seguir");
                }
            }
        });
    }

    private void FollowingUsers(String uid, String userId){
        CollectionReference followCollection = firestore.collection("follows");
        followCollection
                .whereEqualTo("userId",uid)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error al obtener los datos: ", e);
                        return;
                    }
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot logUserFollowDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Follow logUserFollow = logUserFollowDoc.toObject(Follow.class);

                        // Verificar si ya sigue al usuario
                        if (logUserFollow != null && logUserFollow.getFollowing().contains(userId)) {
                            btnFollow.setText("Siguiendo");
                        } else {
                            btnFollow.setText("Seguir");
                        }

                        // Cambiar el estado del botón al hacer clic
                        btnFollow.setOnClickListener(v -> {
                            // Obtener la referencia del usuario a seguir
                            firestore.collection("follows")
                                    .whereEqualTo("userId", userId)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            DocumentSnapshot userToFollowDoc = querySnapshot.getDocuments().get(0);
                                            DocumentReference logUserFollowRef = logUserFollowDoc.getReference();
                                            DocumentReference userToFollowRef = userToFollowDoc.getReference();

                                            if (btnFollow.getText().toString().equals("Seguir")) {
                                                // Añadir al usuario logeado a la lista de following y followers
                                                logUserFollowRef.update("following", FieldValue.arrayUnion(userId));
                                                userToFollowRef.update("followers", FieldValue.arrayUnion(uid))
                                                        .addOnSuccessListener(aVoid -> btnFollow.setText("Siguiendo"))
                                                        .addOnFailureListener(e2 -> Log.e("FirestoreError", "Error al seguir al usuario: ", e2));
                                            } else {
                                                // Eliminar al usuario logeado de la lista de following y followers
                                                logUserFollowRef.update("following", FieldValue.arrayRemove(userId));
                                                userToFollowRef.update("followers", FieldValue.arrayRemove(uid))
                                                        .addOnSuccessListener(aVoid -> btnFollow.setText("Seguir"))
                                                        .addOnFailureListener(e2 -> Log.e("FirestoreError", "Error al dejar de seguir al usuario: ", e2));
                                            }
                                        }
                                    });
                        });
                    }
                });
    }

    private void numFollows(String iduser, TextView txtnumFollowes, TextView txtnumFollowing){
        CollectionReference followCollection = firestore.collection("follows");

        followCollection.whereEqualTo("userId", iduser) // Filtrar por el ID del usuario
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            follow = document.toObject(Follow.class);
                            if (follow != null) {
                                int numFollower = follow.getFollowers().size();
                                txtnumFollowes.setText(String.valueOf(numFollower));
                                int numFollowing = follow.getFollowing().size();
                                txtnumFollowing.setText(String.valueOf(numFollowing));
                            }
                        }
                    } else {
                        Log.d("Firestore", "No se encontraron documentos");
                    }
                });
    }
}