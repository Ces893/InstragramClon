package com.example.instragramclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;

import java.util.List;

public class ComentsAdapter extends RecyclerView.Adapter<ComentsAdapter.ViewHolder> {

    private List<String> commentsList;

    public ComentsAdapter(List<String> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_coments, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String comment = commentsList.get(position);
        holder.commentText.setText(comment);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView commentText;

        public ViewHolder(View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.add_comment);
        }
    }
}
