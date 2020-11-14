package com.mayorista.appproveedorgas.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayorista.appproveedorgas.R;
import com.mayorista.appproveedorgas.pojo.Usuario;

import java.util.List;

public class AdaptadorUsuario extends RecyclerView.Adapter<AdaptadorUsuario.ViewHolder> {

    List<Usuario> usuarios;

    public AdaptadorUsuario(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(usuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre;
        TextView celular;
        Button btnQuitar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.lblUsuario);
            celular = itemView.findViewById(R.id.lblCelular);
            btnQuitar = itemView.findViewById(R.id.btnQuitarUsuario);
        }

        void bind(final Usuario usuario) {
            nombre.setText(usuario.getNombre());
            celular.setText(usuario.getCelular());
        }
    }
}
