package com.example.appproveedorgas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appproveedorgas.pojo.DetailOrder;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailOrderAdapter extends RecyclerView.Adapter<DetailOrderAdapter.ViewHolderDatos> {
    ArrayList<DetailOrder> DetailOrderList;
    private Context getContext;
    public DetailOrderAdapter(ArrayList<DetailOrder> DetailOrderList) {
        this.DetailOrderList = DetailOrderList;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_detail_order, null, false);
        getContext = parent.getContext();
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        holder.getList(DetailOrderList.get(position));
    }

    @Override
    public int getItemCount() {
        return DetailOrderList.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView txtProducto;
        TextView txtCantidad;
        TextView txtPrecioU;
        TextView txtSubtotal;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            txtProducto = itemView.findViewById(R.id.txtProducto);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtPrecioU = itemView.findViewById(R.id.txtPrecioU);
            txtSubtotal = itemView.findViewById(R.id.txtSubtotal);
        }

        public void getList(DetailOrder oDetailOrder) {
            txtProducto.setText(oDetailOrder.getProducto());
            txtCantidad.setText(String.valueOf(oDetailOrder.getCantidad()));
            DecimalFormat df = new DecimalFormat("#.00");
            double preciou = oDetailOrder.getPrecioU();
            double subtotal = oDetailOrder.getSubTotal();
            if(preciou>0)
                txtPrecioU.setText( df.format(preciou));
            else
                txtPrecioU.setText("");
            if(subtotal>0)
                txtSubtotal.setText( df.format(subtotal));
            else
                txtSubtotal.setText("");

        }
    }
}
