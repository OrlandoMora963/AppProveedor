package com.example.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.util.LoadImage;
import com.example.appproveedorgas.util.ProductDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class detailproductAdapter extends RecyclerView.Adapter<detailproductAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;

    List<ProductDetail> Product_list;

    public detailproductAdapter(List<ProductDetail> product_list) {
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_try,parent,false);
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull detailproductAdapter.viewHolder holder, int position) {
        holder.bind(Product_list.get(position));
    }

    @Override
    public int getItemCount() {
        return Product_list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView product_name;
        TextView txtMarca;
        TextView txtUnidadMedida;
        EditText etxtPrecioUnitario;
        ImageView image_product;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name=itemView.findViewById(R.id.gas_camion_name);
            txtMarca=itemView.findViewById(R.id.txtMarca);
            txtUnidadMedida=itemView.findViewById(R.id.txtUnidadMedida);
            etxtPrecioUnitario=itemView.findViewById(R.id.etxtPrecioUnitario);
            image_product=itemView.findViewById(R.id.image_product);
        }

        void bind(final ProductDetail products) {

            product_name.setText(products.getDescription());
            txtMarca.setText(products.getMarke_id().getName());
            txtUnidadMedida.setText(" "+products.getUnit_measurement_id().getName());
            etxtPrecioUnitario.setText("0.00");
            new LoadImage(image_product).execute(products.getImage());
            Picasso.get().load(products.getImage()).into(image_product);
        }
    }
}