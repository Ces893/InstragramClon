package com.example.instragramclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.clases.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>{

    private List<Post> data;

    public MyPostAdapter(List<Post> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyPostAdapter.MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_imgpost,parent,false);

        return new MyPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostAdapter.MyPostViewHolder holder, int position) {
        View view = holder.itemView;

        Post item = data.get(position);

        ImageView imgPost = view.findViewById(R.id.postimg);

        Picasso.get()
                .load(item.imgUrl)
                .placeholder(R.drawable.ic_rounded_account_circle_24)
                .error(R.drawable.ic_launcher_background)
                .into(imgPost);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
