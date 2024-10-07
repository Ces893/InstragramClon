package com.example.instragramclone.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.activity.ComentsActivity;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private List<Post> filteredData;
    private List<Post> data;
    FirebaseFirestore firestore;

    public PostAdapter(List<Post> data) {
        this.data = data;  // Guarda la lista original
        this.filteredData = new ArrayList<>(data);
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void setData(List<Post> newData) {
        this.data.clear();
        this.data.addAll(newData);
        this.filteredData.clear();
        this.filteredData.addAll(newData);
        notifyDataSetChanged();
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

        Post item = filteredData.get(position);

        Log.d("ADAPTER_DATA", "Mostrando post: " + item.description);

        TextView userpost = view.findViewById(R.id.publicacion1user);
        TextView contMegusta = view.findViewById(R.id.countMegusta);
        TextView contComet = view.findViewById(R.id.countComentarios);
        TextView descrip = view.findViewById(R.id.descripcionPost);
        TextView hora = view.findViewById(R.id.horaPost);
        TextView nombreUser = view.findViewById(R.id.nombreUsuario);
        ImageView imgPost = view.findViewById(R.id.imgPost);
        ImageView imgUser = view.findViewById(R.id.imgUser);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM", Locale.getDefault());
        String formatDate = dateFormat.format(item.publicacionDate);

        userpost.setText(item.userId);
        contMegusta.setText(""+item.likeCount);
        contComet.setText(Integer.toString(item.commentsCount));
        descrip.setText(item.description);
        hora.setText("Publicado el "+formatDate);

        Picasso.get()
                .load(item.imgUrl) // URL de la imagen obtenida de la API
                .placeholder(R.drawable.ic_rounded_account_circle_24) // Imagen predeterminada mientras carga
                .error(R.drawable.ic_launcher_background) // Imagen si hay error
                .into(imgPost); // ImageView donde se mostrará la imagen

        String uid = item.userId;
        DocumentReference documentReference = firestore.collection("users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    userpost.setText(user.getUserName());
                    nombreUser.setText(user.getUserName());
                    Picasso.get()
                            .load(user.getImgUser()) // URL de la imagen obtenida de la API
                            .placeholder(R.drawable.ic_rounded_account_circle_24) // Imagen predeterminada mientras carga
                            .error(R.drawable.ic_launcher_background) // Imagen si hay error
                            .into(imgUser); // ImageView donde se mostrará la imagen
                } else {
                    Log.d("Firestore", "No se encontró el documento del usuario.");
                }
            }
        });


        ImageButton imgButton = view.findViewById(R.id.bottomComentario);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ComentsActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String  query){
        filteredData.clear();  // Limpia la lista filtrada antes de aplicar el filtro
        if (query.isEmpty()) {
            //filteredData.addAll(data);  // Si no hay búsqueda, muestra todos los datos
        } else {
            for (Post post : data) {
                if (post.etiqueta != null && post.etiqueta.toLowerCase().contains(query.toLowerCase())) {
                    filteredData.add(post);
                    Log.d("Filter", "Added Post: " + post.etiqueta); // Añadir los posts que coinciden con la búsqueda
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder
    {
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
