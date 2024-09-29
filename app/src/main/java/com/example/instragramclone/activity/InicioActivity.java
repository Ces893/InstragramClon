package com.example.instragramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.MainActivity;
import com.example.instragramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        new CountDownTimer(1000,100){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    // El usuario está autenticado, redirigir a MainActivity
                    startActivity(new Intent(InicioActivity.this, MainActivity.class));
                } else {
                    // El usuario no está autenticado, redirigir a LoginActivity
                    startActivity(new Intent(InicioActivity.this, LoginActivity.class));
                }
                // Finalizar la actividad de inicio
                finish();
            }
        }.start();
    }
}