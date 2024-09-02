package com.example.instragramclone;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.adapter.PostAdapter;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Post> elementos = new ArrayList<>();
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        RecyclerView rvPost = findViewById(R.id.rvPost);
        rvPost.setLayoutManager(new LinearLayoutManager(this));

        //User userId, String imgUrl, String description
        User user = new User("Usuario1","usuario1");
        User user2 = new User("Usuario2","usuario2");

        elementos.add(new Post(user,"Description Prueba",3300,1000,"usuario4"));
        elementos.add(new Post(user2,"Description Prueba2", 2000,967,"usuario3"));

        adapter = new PostAdapter(elementos);
        rvPost.setAdapter(adapter);
    }
}