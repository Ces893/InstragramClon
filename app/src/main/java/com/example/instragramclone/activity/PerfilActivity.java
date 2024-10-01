package com.example.instragramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.adapter.MyPostAdapter;
import com.example.instragramclone.adapter.PostAdapter;
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

import retrofit2.http.POST;

public class PerfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private MyPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView userImage = findViewById(R.id.userImage);
        TextView userName = findViewById(R.id.userName);
        TextView fullName = findViewById(R.id.fullName);

        Button btnLogOut = findViewById(R.id.btnLogOut);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Info User //
        String uid = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    userName.setText(user.userName);
                    fullName.setText(user.nombre);

                    Picasso.get()
                            .load(user.imgUser) // URL de la imagen obtenida de la API
                            .placeholder(R.drawable.ic_rounded_account_circle_24) // Imagen predeterminada mientras carga
                            .error(R.drawable.ic_launcher_background) // Imagen si hay error
                            .into(userImage); // ImageView donde se mostrará la imagen
                }else {
                    Toast.makeText(PerfilActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Posts //

        RecyclerView rvPosts = findViewById(R.id.rvPostsUserP);
        rvPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(PerfilActivity.this,3);
        rvPosts.setLayoutManager(linearLayoutManager);

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

                            // Configurar el adapter con los datos obtenidos
                            adapter = new MyPostAdapter(postsUsuario);
                            rvPosts.setAdapter(adapter);
                            adapter.notifyDataSetChanged();  // Notificar al adapter que los datos han cambiado
                        } else {
                            Log.w("Firestore", "Error al obtener los posts", task.getException());
                        }
                    }
                });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}