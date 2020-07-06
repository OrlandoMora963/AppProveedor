package com.example.appproveedorgas;

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
import com.example.appproveedorgas.util.ProductRegister;
import com.example.appproveedorgas.util.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MIsPedidosAdapter extends RecyclerView.Adapter<MIsPedidosAdapter.viewHolder> implements View.OnClickListener {


    private View.OnClickListener listener;
    private Context context;
    private DatabaseHelper db;
    List<ProductRegister> Product_list;
    MIsPedidosFragment oMIsPedidosFragment;

    public MIsPedidosAdapter(List<ProductRegister> product_list, MIsPedidosFragment oMIsPedidosFragment) {
        this.Product_list = product_list;
        this.oMIsPedidosFragment = oMIsPedidosFragment;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_mis_pedidos, parent, false);
        view.setOnClickListener(this);
        context = parent.getContext();
        this.db = new DatabaseHelper(context);
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

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView product_name;
        TextView txtPeso;
        EditText etxtPrecioUnitario;
        ImageView image_product;
        Button btneditar, btnEstado, btnguardar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.gas_camion_name);
            txtPeso = itemView.findViewById(R.id.txtPeso);
            btneditar = itemView.findViewById(R.id.editar_misproductos);
            btnguardar = itemView.findViewById(R.id.guardar_misproductos);
            etxtPrecioUnitario = itemView.findViewById(R.id.etxtPrecioUnitario);
            image_product = itemView.findViewById(R.id.marcas_product);
            btnEstado = itemView.findViewById(R.id.btnEstado);
        }

        void bind(final ProductRegister products) {
            product_name.setText(products.getProduct().getDescription().replace("Ã±","ñ"));
            txtPeso.setText(products.getProduct().getMeasurement() + " " + products.getProduct().getUnit_measurement_id().getName());
            etxtPrecioUnitario.setText(String.valueOf(products.getRegister().getPrice()));
            Picasso.get().load(products.getProduct().getImage()).into(image_product);
            final String token = db.getToken();
            if (products.getRegister().getStatus().equals("active")) {
                btnEstado.setText("Habilitado");

            } else {
                btnEstado.setText("Deshabilitado");
                btnguardar.setEnabled(false);
                btneditar.setEnabled(false);
            }
            btneditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btneditar.getText().toString().equals("Editar")) {
                        btneditar.setText("Cancelar");
                        btnguardar.setVisibility(v.VISIBLE);
                        etxtPrecioUnitario.setEnabled(true);
                    } else {
                        btneditar.setText("Editar");
                        btnguardar.setVisibility(v.INVISIBLE);
                        etxtPrecioUnitario.setEnabled(false);
                        etxtPrecioUnitario.setText(products.getRegister().getPrice());
                    }
                }
            });
            btnguardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etxtPrecioUnitario.getText().toString().equals("") || Double.valueOf(etxtPrecioUnitario.getText().toString()) < 1)
                        Toast.makeText(context, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    else {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("id", products.getRegister().getID());
                            if (Double.valueOf(etxtPrecioUnitario.getText().toString())!=Double.valueOf(products.getRegister().getPrice()))
                                object.put("price", Double.valueOf(etxtPrecioUnitario.getText().toString())+1);
                            else
                                object.put("price", Double.valueOf(etxtPrecioUnitario.getText().toString()));
                            object.put("status", "active");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String url = "http://34.71.251.155/api/product/staff/register/";
                        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast.makeText(context, "Se modifico el producto " + products.getProduct().getDescription(), Toast.LENGTH_LONG).show();
                                    oMIsPedidosFragment.Listar();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();

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
            btnEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("id", products.getRegister().getID());
                            object.put("price", Double.valueOf(etxtPrecioUnitario.getText().toString()));
                        if (btnEstado.getText().toString().equals("Habilitado"))
                            object.put("status", "disable");
                        else
                            object.put("status", "active");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = "http://34.71.251.155/api/product/staff/register/";
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (btnEstado.getText().toString().equals("Habilitado"))
                                    Toast.makeText(context, "Se a deshabilitado el " + products.getProduct().getDescription(), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(context, "Se a habilitado el " + products.getProduct().getDescription(), Toast.LENGTH_LONG).show();

                                oMIsPedidosFragment.Listar();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();

                            Log.d("Voley get", token);
                            headers.put("Authorization", "JWT " + token);
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };

                    VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
                }
            });
        }
    }
}