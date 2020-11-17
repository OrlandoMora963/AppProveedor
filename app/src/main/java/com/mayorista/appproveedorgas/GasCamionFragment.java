package com.mayorista.appproveedorgas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mayorista.appproveedorgas.util.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GasCamionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerView;
    private ArrayList<Product> productDetails_list;
    private CamionGasAdapter camionGasAdapter;
    private DatabaseHelper db;

    public GasCamionFragment() {
        // Required empty public constructor
    }

    public static GasCamionFragment newInstance(String param1, String param2) {
        GasCamionFragment fragment = new GasCamionFragment();
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
        View view = inflater.inflate(R.layout.fragment_gas_camion, container, false);
        recyclerView = view.findViewById(R.id.gas_camion_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.db = new DatabaseHelper(getContext());
        Listar();

        return view;
    }

    public void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringBuilder sb = new StringBuilder();
        sb.append(Variable.HOST + "/product/staff/cisterna/");
        String url = sb.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        recyclerView.setAdapter(null);
                        Gson gson = new Gson();
                        productDetails_list = gson.fromJson(response.substring(34, response.length() - 1).trim(), new TypeToken<ArrayList<Product>>() {
                        }.getType());
                        camionGasAdapter = new CamionGasAdapter(productDetails_list, GasCamionFragment.this);
                        for (Product item : productDetails_list) {
                            item.setImage(Variable.HOST_BASE + item.getImage());
                        }
                        recyclerView.setAdapter(camionGasAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.networkResponse);
                Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
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
        ;
        queue.add(stringRequest);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
