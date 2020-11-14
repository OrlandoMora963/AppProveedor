package com.mayorista.appproveedorgas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mayorista.appproveedorgas.pojo.product_marcas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class ProductFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private ArrayList<product_marcas> product_marcas;
    private ProductAdapter productAdapter;
    private Button ver_mis_productos;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        recyclerView = view.findViewById(R.id.product_container);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ver_mis_productos = view.findViewById(R.id.ver_mis_productos);

        ver_mis_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MisProductosActivity.class);
                startActivity(intent);
            }
        });

        Listar();

        //    product_marcas=new ArrayList<>();

        //  product_marcas.add(new product_marcas(1,"cerveza",""));
        //product_marcas.add(new product_marcas(1,"agua",""));
        // product_marcas.add(new product_marcas(1,"gas",""));

        return view;

    }

    private void AsignarBotones() {
        productAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BrandsTitle = product_marcas.get(recyclerView.getChildAdapterPosition(v)).getName();
                if (BrandsTitle.equals("Gas")) {
                    Intent intent = new Intent(getContext(), GasActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), ProductActivity.class);
                    intent.putExtra("CategoriaId", String.valueOf(product_marcas.get(recyclerView.getChildAdapterPosition(v)).getId()));
                    startActivity(intent);
                }

            }
        });
    }

    private void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringBuilder sb = new StringBuilder();
        sb.append(Variable.HOST + "/categories");
        String url = sb.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        recyclerView.setAdapter(null);
                        Gson gson = new Gson();
                        product_marcas = gson.fromJson(response, new TypeToken<ArrayList<product_marcas>>() {
                        }.getType());
                        for (product_marcas item : product_marcas) {
                            item.setImage(Variable.HOST_BASE + item.getImage());
                        }
                        productAdapter = new ProductAdapter(product_marcas);
                        recyclerView.setAdapter(productAdapter);

                        AsignarBotones();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
