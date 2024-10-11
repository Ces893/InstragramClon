package com.example.instragramclone.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instragramclone.R;
import com.example.instragramclone.UserPerfilFragment;
import com.example.instragramclone.clases.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private List<User> data;
    private List<User> filterUser;
    private Context context;
    private boolean showbtn;

    public SearchAdapter(List<User> data, Context context,boolean showbtn) {
        this.data = data;
        this.filterUser = new ArrayList<>(data);
        this.context = context;
        this.showbtn = showbtn;
    }

    public void updateUsers(List<User> newUsers) {
        this.data.clear();
        this.data.addAll(newUsers);
        this.filterUser.clear();
        this.filterUser.addAll(newUsers); // Actualizar filterUser para que tenga todos los usuarios
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.itemView;

        User item = filterUser.get(position);

        TextView nameUser = view.findViewById(R.id.nameUser);
        ImageView imgUser = view.findViewById(R.id.imgUser);
        Button btnSeguir = view.findViewById(R.id.btnSeguido);

        if(showbtn){
            btnSeguir.setVisibility(view.VISIBLE);
        }else {btnSeguir.setVisibility(view.GONE);}

        nameUser.setText(item.userName);

        Picasso.get()
                .load(item.imgUser) // URL de la imagen obtenida de la API
                .placeholder(R.drawable.ic_rounded_account_circle_24) // Imagen predeterminada mientras carga
                .error(R.drawable.ic_launcher_background) // Imagen si hay error
                .into(imgUser); // ImageView donde se mostrará la imagen

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPerfilFragment userPerfilFragment = new UserPerfilFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId",item.getId());
                userPerfilFragment.setArguments(bundle);

                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, userPerfilFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filterUser.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String busqueda){
        filterUser.clear();
        if(busqueda.isEmpty()){
            //filterUser.addAll(data);
        }else {
            for(User user : data){
                if(user.userName.toLowerCase().contains(busqueda.toLowerCase())){
                    filterUser.add(user);
                    Log.d("Filter", "Added user: " + user.userName);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.nameUser);
        }
    }
}
