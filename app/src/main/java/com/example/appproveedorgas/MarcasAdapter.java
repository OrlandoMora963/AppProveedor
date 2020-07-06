package com.example.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.util.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MarcasAdapter extends RecyclerView.Adapter<MarcasAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;

    List<Product> Product_list;

    public MarcasAdapter(List<Product> product_list) {
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_marcas_product,parent,false);
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public void onBindViewHolder(@NonNull MarcasAdapter.viewHolder holder, int position) {
        holder.bind(Product_list.get(position));
    }

    @Override
    public int getItemCount() {
        return Product_list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        ImageView image_product;
        TextView product_name;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            //product_name=itemView.findViewById(R.id.marcas_name_product);
            //product_name=itemView.findViewById(R.id.marcas_name_product);
        }

        void bind(final Product products) {

            //product_name.setText(products.getMarke_id().getName());
            //Picasso.get().load(products.getImage()).into(image_product);

        }
    }
}
