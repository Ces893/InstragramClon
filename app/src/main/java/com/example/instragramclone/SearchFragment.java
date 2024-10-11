package com.example.instragramclone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.adapter.SearchAdapter;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchAdapter searchAdapter;
    private List<User> users = new ArrayList<>();

    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Boolean showbtn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        LinearLayout linearLayout = view.findViewById(R.id.linerLyUserName);
        TextView txtUserName = view.findViewById(R.id.txtUserName);

        linearLayout.setVisibility(View.GONE);
        ImageView imgback = view.findViewById(R.id.imgVback);

        Bundle bundle = getArguments();
        ArrayList<String> listSeguidos = new ArrayList<>();
        ArrayList<String> listSeguidores = new ArrayList<>();
        String nombreUser="";
        if (bundle != null) {
            listSeguidos = bundle.getStringArrayList("ListSeguidos");
            listSeguidores = bundle.getStringArrayList("ListSeguidores");
            nombreUser = bundle.getString("nombreUser");
        }

        if (listSeguidos != null && !listSeguidos.isEmpty()) {
            getUsersByIdList(listSeguidos);
            linearLayout.setVisibility(View.VISIBLE);
            txtUserName.setText(nombreUser+" - Seguidores");
            showbtn = true;
        } else if (listSeguidores != null && !listSeguidores.isEmpty()) {
            getUsersByIdList(listSeguidores);
            linearLayout.setVisibility(View.VISIBLE);
            txtUserName.setText(nombreUser+" - Seguidos");
            showbtn = true;
        } else {
            getAllUsers();
            getAllPosts();
        }
        setUpRecyclerView(view, showbtn);

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }


    private void setUpRecyclerView(View view, boolean showbtn) {
        RecyclerView rvContentSearch = view.findViewById(R.id.rvContentSearch);
        rvContentSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        searchAdapter = new SearchAdapter(users,getContext(),showbtn);
        postAdapter = new PostAdapter(posts);

        rvContentSearch.setAdapter(searchAdapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
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
                return true;
            }
        });
    }

    private void getAllUsers() {
        firestore.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            User user = document.toObject(User.class);
                            users.add(user);
                        }
                        searchAdapter.notifyDataSetChanged(); // Asegúrate de inicializar el adaptador
                    } else {
                        Log.d("Firestore", "No se encontraron usuarios.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener los usuarios", e);
                });
    }

    private void getUsersByIdList(List<String> userIdList) {
        firestore.collection("users").whereIn("id", userIdList).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<User> userList = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        searchAdapter.updateUsers(userList);
                        searchAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "No se encontraron usuarios con los IDs proporcionados.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener usuarios por lista de IDs", e);
                });
    }

    private void getAllPosts() {
        firestore.collection("posts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Post post = document.toObject(Post.class);
                            posts.add(post);
                        }
                        postAdapter.notifyDataSetChanged(); // Asegúrate de inicializar el adaptador
                    } else {
                        Log.d("Firestore", "No se encontraron posts.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al obtener los posts", e);
                });
    }
}