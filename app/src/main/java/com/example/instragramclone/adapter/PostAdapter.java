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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.activity.ComentsActivity;
import com.example.instragramclone.clases.Like;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private List<Post> filteredData;
    private List<Post> data;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

    public void addData(List<Post> newPosts) {
        int startPosition = data.size();
        data.addAll(newPosts);
        filteredData.addAll(newPosts);
        notifyItemRangeInserted(startPosition, newPosts.size());
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
        descrip.setText(item.description);
        hora.setText("Publicado el "+formatDate);

        Picasso.get()
                .load(item.imgUrl) // URL de la imagen obtenida de la API
                .placeholder(R.drawable.ic_rounded_account_circle_24) // Imagen predeterminada mientras carga
                .error(R.drawable.ic_launcher_background) // Imagen si hay error
                .into(imgPost); // ImageView donde se mostrará la imagen

        String userId = item.userId;
        DocumentReference documentReference = firestore.collection("users").document(userId);
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


        ImageButton btnComments = view.findViewById(R.id.bottomComentario);
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ComentsActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        String uid = firebaseAuth.getCurrentUser().getUid();
        ImageButton btnLike = view.findViewById(R.id.bottomMeGusta);

        //verificarLike(item.getPostId(),uid,btnLike);
        numLikes(item.getPostId(),contMegusta);
        agregarLike(item.getPostId(),uid, btnLike);
    }

    private void verificarLike(String postId, String uid, ImageButton btnLike) {
        CollectionReference likesCollection = firestore.collection("likes");

        likesCollection.whereEqualTo("postId", postId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("FirestoreError", "Error al verificar los likes", e);
                        return;
                    }

                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Obtener el documento de likes
                        DocumentSnapshot likeDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Like like = likeDoc.toObject(Like.class);

                        if (like != null) {
                            // Si el usuario ya dio like, cambiar el ícono a "liked"
                            if (like.getLikersUser().contains(uid)) {
                                btnLike.setImageResource(R.drawable.likeactive);  // Icono de "like activo"
                            } else {
                                // Cambiar a "like inactivo"
                                btnLike.setImageResource(R.drawable.ic_outline_favorite_border_24);  // Icono de "like inactivo"
                            }
                        }
                    } else {
                        // Si no existe el documento de likes, cambiar a "like inactivo"
                        btnLike.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    }
                });
    }

    private void agregarLike(String postId, String uid, ImageButton btnLike) {
        CollectionReference likesCollection = firestore.collection("likes");

        likesCollection.whereEqualTo("postId", postId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error al obtener likes: ", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot likeDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Like like = likeDoc.toObject(Like.class);

                        if (like != null) {
                            DocumentReference likeRef = likeDoc.getReference();

                            // Configura el botón según si el usuario ya ha dado like
                            if (like.getLikersUser().contains(uid)) {
                                // El usuario ya dio like
                                btnLike.setImageResource(R.drawable.likeactive);  // Cambia a "like activo"

                                // Al hacer clic, quitar el like
                                btnLike.setOnClickListener(v -> {
                                    likeRef.update("likersUser", FieldValue.arrayRemove(uid))
                                            .addOnSuccessListener(aVoid -> {
                                                // Actualización automática de UI
                                                btnLike.setImageResource(R.drawable.ic_outline_favorite_border_24);  // Cambia a "like inactivo"
                                            })
                                            .addOnFailureListener(e1 -> {
                                                Log.e("FirestoreError", "Error al quitar like: ", e1);
                                            });
                                });
                            } else {
                                // El usuario no dio like
                                btnLike.setImageResource(R.drawable.ic_outline_favorite_border_24);  // Cambia a "like inactivo"

                                // Al hacer clic, agregar el like
                                btnLike.setOnClickListener(v -> {
                                    likeRef.update("likersUser", FieldValue.arrayUnion(uid))
                                            .addOnSuccessListener(aVoid -> {
                                                // Actualización automática de UI
                                                btnLike.setImageResource(R.drawable.likeactive);  // Cambia a "like activo"
                                            })
                                            .addOnFailureListener(e1 -> {
                                                Log.e("FirestoreError", "Error al agregar like: ", e1);
                                            });
                                });
                            }
                        }
                    } else {
                        // Si no hay documentos de likes, establecer el botón como inactivo
                        btnLike.setImageResource(R.drawable.ic_outline_favorite_border_24);
                    }
                });
    }

    private void numLikes(String postId, TextView txtLikeCount) {
        CollectionReference likesCollection = firestore.collection("likes");

        likesCollection.whereEqualTo("postId", postId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error al obtener likes: ", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot likeDoc = queryDocumentSnapshots.getDocuments().get(0);
                        Like like = likeDoc.toObject(Like.class);

                        if (like != null) {
                            List<String> likersUser = like.getLikersUser();
                            txtLikeCount.setText(String.valueOf(likersUser.size()));
                        } else {
                            txtLikeCount.setText("0");
                        }
                    } else {
                        txtLikeCount.setText("0");
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
