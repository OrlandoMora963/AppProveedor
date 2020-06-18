package com.example.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.util.ProductDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class detailGasAdapter extends RecyclerView.Adapter<detailGasAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;

    List<ProductDetail> Product_list;

    public detailGasAdapter(List<ProductDetail> product_list) {
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_product_gas,parent,false);
        view.setOnClickListener(this);


        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public void onBindViewHolder(@NonNull detailGasAdapter.viewHolder holder, int position) {
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
            image_product=itemView.findViewById(R.id.image_product);
            product_name=itemView.findViewById(R.id.name_product_gas);
        }

        void bind(final ProductDetail products) {

            product_name.setText(products.getMarke_id().getName());
            Picasso.get().load(products.getImage()).into(image_product);
        }
    }
}
