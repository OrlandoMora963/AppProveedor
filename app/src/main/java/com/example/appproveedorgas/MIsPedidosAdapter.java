package com.example.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.util.ProductDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MIsPedidosAdapter extends RecyclerView.Adapter<MIsPedidosAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;

    List<ProductDetail> Product_list;

    public MIsPedidosAdapter(List<ProductDetail> product_list) {
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_mis_pedidos,parent,false);
        view.setOnClickListener(this);


        return new viewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {

        this.listener = onClickListener;

    }

    @Override
    public void onBindViewHolder(@NonNull MIsPedidosAdapter.viewHolder holder, int position) {
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
        Button editar,guardar,eliminar;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name=itemView.findViewById(R.id.gas_camion_name);
            editar=itemView.findViewById(R.id.editar_misproductos);
            guardar=itemView.findViewById(R.id.guardar_misproductos);
            etxtPrecioUnitario=itemView.findViewById(R.id.editar_precio_misproductos);

            image_product=itemView.findViewById(R.id.image_product);
        }

        void bind(final ProductDetail products) {
            product_name.setText(products.getDescription());
            etxtPrecioUnitario.setText(String.valueOf(products.getUnit_price()));
            etxtPrecioUnitario.setText(products.getImage());
//            Picasso.get().load(products.getImage()).into(image_product);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardar.setVisibility(v.VISIBLE);
                    etxtPrecioUnitario.setEnabled(true);
                }
            });

        }
    }
}