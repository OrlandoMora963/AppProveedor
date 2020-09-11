package com.mayorista.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayorista.appproveedorgas.util.LoadImage;
import com.mayorista.appproveedorgas.util.product_marcas;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> implements View.OnClickListener {

    List<product_marcas> list_marcas;

    private View.OnClickListener listener;

    public ProductAdapter(List<product_marcas> list_marcas) {
        this.list_marcas = list_marcas;
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product,parent,false);
        view.setOnClickListener(this);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.viewHolder holder, int position) {
        holder.bind(list_marcas.get(position));
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public int getItemCount() {
        return list_marcas.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        ImageView image_product;
        TextView title;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image_product=itemView.findViewById(R.id.marcas_product);
            title=itemView.findViewById(R.id.marcas_name_product);

        }

        void bind(final product_marcas marcas) {

             title.setText(marcas.getName());
            new LoadImage(image_product).execute(marcas.getImage());
            Picasso.get().load(marcas.getImage()).into(image_product);
        }
    }
}
