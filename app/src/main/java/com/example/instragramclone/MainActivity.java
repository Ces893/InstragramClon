package com.example.instragramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.Activity.ComentsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton btnComment = findViewById(R.id.bottomComentario);  // Vincula el botón de comentarios

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la ComentsActivity
                Intent intent = new Intent(MainActivity.this, ComentsActivity.class);
                intent.putExtra("postid", "12345"); // Puedes pasar datos reales si los tienes
                intent.putExtra("publisherid", "67890"); // Puedes pasar datos reales si los tienes
                startActivity(intent);
            }
        });

        // Botón de comentario para la segunda publicación
        ImageButton btnComentario2 = findViewById(R.id.bottomComentario2);

        btnComentario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad de comentarios para la segunda publicación
                Intent intent = new Intent(MainActivity.this, ComentsActivity.class);
                intent.putExtra("postid", "54321"); // Puedes pasar el ID real de la publicación
                intent.putExtra("publisherid", "09876"); // Puedes pasar el ID real del publicador
                startActivity(intent);
            }
        });
    }
}