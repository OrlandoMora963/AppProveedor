package com.mayorista.appproveedorgas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mayorista.appproveedorgas.util.Product;
import com.mayorista.appproveedorgas.util.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class detailproductAdapter extends RecyclerView.Adapter<detailproductAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;
    private Context context;
    private DatabaseHelper db;
    List<Product> Product_list;
    FragmentProductDetail oFragmentProductDetail;

    public detailproductAdapter(List<Product> product_list, FragmentProductDetail oFragmentProductDetail) {
        this.Product_list = product_list;
        this.oFragmentProductDetail = oFragmentProductDetail;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_try, parent, false);
        view.setOnClickListener(this);
        context = parent.getContext();
        this.db = new DatabaseHelper(context);
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

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView product_name;
        TextView txtMarca;
        TextView txtUnidadMedida;
        EditText etxtPrecioUnitario;
        ImageView image_product;
        Button btnguardar_misproductos;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.gas_camion_name);
            txtMarca = itemView.findViewById(R.id.txtMarca);
            txtUnidadMedida = itemView.findViewById(R.id.txtUnidadMedida);
            etxtPrecioUnitario = itemView.findViewById(R.id.etxtPrecioUnitario);
            image_product = itemView.findViewById(R.id.marcas_product);
            btnguardar_misproductos = itemView.findViewById(R.id.guardar_misproductos);
        }

        void bind(final Product products) {

            product_name.setText(products.getDescription().replace("Ã±", "ñ"));
            txtMarca.setText(products.getMarke_id().getName());
            txtUnidadMedida.setText(" " + products.getUnit_measurement_id().getName());
            etxtPrecioUnitario.setText("0.00");
            Picasso.get().load(products.getImage()).into(image_product);
            btnguardar_misproductos.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (etxtPrecioUnitario.getText().toString().equals("") || Double.valueOf(etxtPrecioUnitario.getText().toString()) < 1)
                        Toast.makeText(context, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("product_id", products.getId());
                            object.put("price", Double.valueOf(etxtPrecioUnitario.getText().toString()) + 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String url = Variable.HOST + "/product/staff/register/";
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(context, "Se agrego el producto " + product_name.getText(), Toast.LENGTH_LONG).show();
                                    //     Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                                    oFragmentProductDetail.Listar();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                String token = db.getToken();
                                Log.d("Voley get", token);
                                headers.put("Authorization", "JWT " + token);
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };

                        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
                    }
                }
            });
        }
    }
}