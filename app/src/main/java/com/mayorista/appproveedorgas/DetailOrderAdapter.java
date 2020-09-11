package com.mayorista.appproveedorgas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayorista.appproveedorgas.pojo.DetailOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailOrderAdapter extends RecyclerView.Adapter<DetailOrderAdapter.ViewHolderDatos> {
    ArrayList<DetailOrder> DetailOrderList;
    int widthTxtProducto;
    public DetailOrderAdapter(ArrayList<DetailOrder> DetailOrderList,int widthTxtProducto) {
        this.DetailOrderList = DetailOrderList;
        this.widthTxtProducto=widthTxtProducto;
    }

    @NonNull
    @Override
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_detail_order, null, false);

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
            LinearLayout.LayoutParams btnparams = new LinearLayout.LayoutParams(widthTxtProducto, ViewGroup.LayoutParams.WRAP_CONTENT);
            txtProducto.setLayoutParams(btnparams);
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
