package com.example.appproveedorgas;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    //----
    RecyclerView recyclerView;
    List<Mpedido> orders = new ArrayList<>();
    orderAdapter adapter;
    //-----
    //String baseUrl = "http://134.209.37.205:8000/api";
    String baseUrl = "http://34.71.251.155/api";
    private DatabaseHelper db;
    //-----
    private int pagination;
    private boolean onData;

    //-----
    public OrderFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);
        //----
        this.db = new DatabaseHelper(getContext());
        pagination = 1;
        onData = true;
        //---
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                cargarDatos();
            }
        });
        //--- cargar datos
        cargarDatos();
        //---- InitView
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new orderAdapter(recyclerView, getActivity(), orders);
        recyclerView.setAdapter(adapter);
        // set load more event
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                if (onData) {
                    orders.add(null);
                    //adapter.notifyItemInserted(orders.size()-1);
                    recyclerView.post(new Runnable() {
                        public void run() {
                            adapter.notifyItemInserted(orders.size() - 1);
                        }
                    });
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    pagination++;
                                    orders.remove(orders.size() - 1);
                                    adapter.notifyItemRemoved(orders.size());
                                    Log.d("Data", "Page " + pagination);
                                    postGetDataOrders(pagination);
                                }
                            },
                            1000);
                    //mas datos
                    /*
                    int index = orders.size();
                    int end = index+10;
                    for(int i = index;i<end;i++){
                        Mpedido order = new Mpedido(i,"12/11/19","70.50","Espera");
                        orders.add(order);
                    }
                     */
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), "No hay mas datos", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void cargarDatos() {
        orders.clear();
        pagination = 1;
        onData = true;
        postGetDataOrders(1);
        /*
        for(int i=0;i<10;i++){
            Mpedido order = new Mpedido(i,"12/11/19","70.50","Espera");
            orders.add(order);

        }
         */
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatos();
    }

    //----
    private void postGetDataOrders(int pg) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        // Enter the correct url for your api service site
        String url = this.baseUrl + "/company/orders/" + pg + "/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley get", response.toString());
                        try {
                            int st = response.getInt("status");
                            if (st == 200) {
                                JSONArray lista = response.getJSONArray("data");
                                onData = lista.length() > 0;
                                for (int i = 0; i < lista.length(); i++) {
                                    JSONObject jorder = lista.getJSONObject(i).getJSONObject("orden");
                                    JSONArray jorder_de = lista.getJSONObject(i).getJSONArray("order_detail");
                                    JSONObject jclient = lista.getJSONObject(i).getJSONObject("client");
                                    //lista detalle
                                    List<Mpedido_detalle> l_detalle = new ArrayList<>();
                                    for (int j = 0; j < jorder_de.length(); j++) {
                                        JSONObject detail = jorder_de.getJSONObject(j);
                                        Mpedido_detalle detalle = new Mpedido_detalle(detail.getInt("quantity"),
                                                detail.getJSONObject("product_id").getString("description"), detail.getDouble("unit_price"));
                                        l_detalle.add(detalle);
                                    }
                                    Mpedido order = new Mpedido(jorder.getInt("id"),
                                            jorder.getString("date"),
                                            "0",
                                            jorder.getString("status"),
                                            l_detalle,
                                            jclient.getString("phone1"),
                                            getContext());
                                    orders.add(order);
                                }
                                adapter.notifyDataSetChanged();
                                adapter.setLoaded();

                                if (swipeRefreshLayout != null)
                                    swipeRefreshLayout.setRefreshing(false);
                            } else {
                                Log.d("volley", "estatus " + st);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*
                            if(swipeRefreshSetting!= null)
                                swipeRefreshSetting.setRefreshing(false);

                         */
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley get", "error voley" + error.toString());
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = db.getToken();
                Log.d("Voley get", token);
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}
