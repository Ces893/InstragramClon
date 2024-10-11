package com.example.instragramclone.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.MainActivity;
import com.example.instragramclone.R;
import com.example.instragramclone.clases.Follow;
import com.example.instragramclone.clases.Like;
import com.example.instragramclone.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText etCorreo, etPass, etNombre, etUserName;
    Button btnCreate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCorreo = findViewById(R.id.etCorreo);
        etPass = findViewById(R.id.etPass);
        etNombre = findViewById(R.id.etNombre);
        etUserName = findViewById(R.id.etUserN);

        btnCreate = findViewById(R.id.btnCrearCuenta);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etCorreo.getText().toString();
                String pass = etPass.getText().toString();
                String nombre = etNombre.getText().toString();
                String userN = etUserName.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(userN) || pass.length() < 6){
                    etCorreo.setError("Correo es requerido");
                    etPass.setError("Contraseña es requerido o Tiene que tener un minimo de 6 caracteres");
                    etNombre.setError("Nombre es requerido");
                    etUserName.setError("UserName es requerido"); //
                }

                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Usuario Creado", Toast.LENGTH_SHORT).show();

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(RegisterActivity.this,"Verifique su Correo", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Error","Error "+e.getMessage());
                                }
                            });

                            String uid = firebaseAuth.getCurrentUser().getUid();
                            User user = new User(uid,userN,email,pass,nombre,"https://firebasestorage.googleapis.com/v0/b/instagramclone-21e5f.appspot.com/o/defaultuserIMG.jpg?alt=media&token=da6edbe3-c0d0-45c9-877e-073f402ddbaf","");
                            DocumentReference documentReference = firestore.collection("users").document(uid);
                            documentReference.set(user);

                            DocumentReference followRef = firestore.collection("follows").document();
                            String followId = followRef.getId();
                            Follow follow = new Follow(followId,uid);
                            followRef.set(follow) .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Follow creado con ID: " +followId);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error al crear el Follow", e);
                                    });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}