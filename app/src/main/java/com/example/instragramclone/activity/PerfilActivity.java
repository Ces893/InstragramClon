package com.example.instragramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.R;
import com.example.instragramclone.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

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