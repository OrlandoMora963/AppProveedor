package com.example.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.util.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CamionGasAdapter extends RecyclerView.Adapter<CamionGasAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;

    List<Product> Product_list;

    public CamionGasAdapter(List<Product> product_list) {
        this.Product_list = product_list;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_gas_camion,parent,false);
        view.setOnClickListener(this);


        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public void onBindViewHolder(@NonNull CamionGasAdapter.viewHolder holder, int position) {
        holder.bind(Product_list.get(position));
    }

    @Override
    public int getItemCount() {
        return Product_list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView image_product;
        TextView product_name;
        EditText editar_precio_misproductos;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name=itemView.findViewById(R.id.gas_camion_name);
            image_product=itemView.findViewById(R.id.image_product);
            editar_precio_misproductos=itemView.findViewById(R.id.editar_precio_misproductos);
        }

        void bind(final Product products) {

            product_name.setText(products.getDescription());
            editar_precio_misproductos.setText("0.00");
            Picasso.get().load(products.getImage()).into(image_product);
        }
    }
}
