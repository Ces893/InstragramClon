package com.example.instragramclone.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.activity.ComentsActivity;
import com.example.instragramclone.clases.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private List<Post> data;

    public PostAdapter(List<Post> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        View view = holder.itemView;

        Post item = data.get(position);

        TextView userpost = view.findViewById(R.id.publicacion1user);
        TextView contMegusta = view.findViewById(R.id.countMegusta);
        TextView contComet = view.findViewById(R.id.countComentarios);
        TextView descrip = view.findViewById(R.id.descripcionPost);
        TextView hora = view.findViewById(R.id.horaPost);
        ImageView imgPost = view.findViewById(R.id.imgPost);
        ImageView imgUser = view.findViewById(R.id.imgUser);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM", Locale.getDefault());
        String formatDate = dateFormat.format(item.publicacionDate);

        userpost.setText(item.userName.userName);
        contMegusta.setText(""+item.likeCount);
        contComet.setText(Integer.toString(item.commentsCount));
        descrip.setText(item.description);
        hora.setText("Publicado el "+formatDate);

        int imgResoursePost = view.getContext().getResources().getIdentifier(item.imgUrl,"drawable", view.getContext().getPackageName());
        int imgResourseUser = view.getContext().getResources().getIdentifier(item.userName.imgUser,"drawable", view.getContext().getPackageName());

        imgPost.setImageResource(imgResoursePost);
        imgUser.setImageResource(imgResourseUser);

        ImageButton imgButton = view.findViewById(R.id.bottomComentario);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ComentsActivity.class);
                intent.putExtra("postid", "1");
                intent.putExtra("publisherid", "2");
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
    {
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
