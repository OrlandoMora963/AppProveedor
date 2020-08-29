package com.example.appproveedorgas;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar_orders);

    }
}

class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_estado;
    public TextView tv_fecha;
    public TextView tv_total;
    public LinearLayout li_detalle;
    public Button btn_detalle, btnLlamar;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_estado = (TextView) itemView.findViewById(R.id.tv_estado_id);
        tv_fecha = (TextView) itemView.findViewById(R.id.tv_fecha_id);
        tv_total = (TextView) itemView.findViewById(R.id.tv_total_id);
        li_detalle = (LinearLayout) itemView.findViewById(R.id.l_detalle_id);
        btn_detalle = (Button) itemView.findViewById(R.id.btn_detalle_id);
        btnLlamar = (Button) itemView.findViewById(R.id.btnLlamar);
    }
}

public class orderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    ILoadMore loadMore;
    boolean isLoading;
    Activity activity;
    List<Mpedido> pedidos;
    int visibleThreshold = 5;
    int lastVisible, totalItemCount;
    private String getStringAddress(Double lat, Double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString().substring(0,strReturnedAddress.toString().lastIndexOf(","));

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        if(strAdd.contains(","))
            return strAdd.substring(0, strAdd.lastIndexOf(","));
        else
            return "";
    }
    public orderAdapter(RecyclerView recyclerView, Activity activity, List<Mpedido> pedidos) {
        this.activity = activity;
        this.pedidos = pedidos;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisible = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisible + visibleThreshold)) {
                    if (loadMore != null)
                        loadMore.onLoadMore();
                    isLoading = true;
                }


            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return pedidos.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.item_layout, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final Mpedido mpedido = pedidos.get(position);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            //-- set data to components
            viewHolder.tv_estado.setText(mpedido.getEstado());
            viewHolder.tv_fecha.setText(mpedido.getFecha());

            double total = 0;
            viewHolder.li_detalle.removeAllViews();
            for (int i = 0; i < mpedido.getDetalle().size(); i++) {
                total += Math.round((mpedido.getDetalle().get(i).getPrecio() * mpedido.getDetalle().get(i).getCantidad()) * 100d) / 100d;
                TextView textview = new TextView(mpedido.getContext());
                textview.setText(mpedido.getDetalle().get(i).getCantidad() + " " + mpedido.getDetalle().get(i).getDescripcion());
                textview.setTextColor(Color.BLACK);
                viewHolder.li_detalle.addView(textview);
            }
            viewHolder.tv_total.setText("S/ " + total);
            //--- set click evento to buttom
            viewHolder.btn_detalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntentPro = new Intent(mpedido.getContext(), PedidoActivity.class);
                    myIntentPro.putExtra("id_pedido", String.valueOf(mpedido.getIdPedido()));
                    myIntentPro.putExtra("referencia", "Referencia : "+getStringAddress(mpedido.getLatitud(),mpedido.getLongitud()));
                    activity.startActivity(myIntentPro);
                }
            });

            System.out.println(mpedido.getEstado());

            if (!mpedido.getEstado().equals("Confirmado")) {
                viewHolder.btnLlamar.setVisibility(View.GONE);
            } else {
                viewHolder.btnLlamar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String dial = "tel: " + mpedido.getPhone();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
                        mpedido.getContext().startActivity(intent);
                    }
                });
            }
            //---
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public void setLoaded() {
        isLoading = false;
    }

}
