package com.example.instragramclone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.adapter.ComentsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComentsActivity extends AppCompatActivity {

    EditText addcoment;
    ImageView image_profile;
    TextView post;

    String postid;
    String punlisherid;

    List<String> commentsList;
    ComentsAdapter commentsAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comentarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addcoment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        punlisherid = intent.getStringExtra("publisherid");

        // Inicializa la lista de comentarios
        commentsList = new ArrayList<>();
        commentsList.add("Gran post");
        commentsList.add("Que imagen tan genial!");
        commentsList.add("que chevereeeee");

        // Configura el RecyclerView
        commentsAdapter = new ComentsAdapter(commentsList);
        recyclerView.setAdapter(commentsAdapter);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcoment.getText().toString().equals("")) {
                    Toast.makeText(ComentsActivity.this, "Tu no puedes poner un comentario vacío", Toast.LENGTH_SHORT).show();
                } else {
                    addComent();
                }
            }
        });
    }

    private void addComent() {
        // Agrega el comentario a la lista y actualiza el RecyclerView
        String newComment = addcoment.getText().toString();
        commentsList.add(newComment);
        addcoment.setText("");
        commentsAdapter.notifyDataSetChanged();
    }
}