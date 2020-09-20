package com.mayorista.appproveedorgas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class ProductMarcasFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private ArrayList<product_marcas> product_marcas;
    private MarcasAdapter marcasAdapter;
    public String CategoriaId = "";

    public ProductMarcasFragment() {
        // Required empty public constructor
    }

    public static ProductMarcasFragment newInstance(String param1, String param2) {
        ProductMarcasFragment fragment = new ProductMarcasFragment();
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
        View view = inflater.inflate(R.layout.fragment_productmarcas, container, false);
        recyclerView = view.findViewById(R.id.ContainerMarcasProduct);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Listar();
        //  AsignarBotones();

        return view;
    }

    private void AsignarBotones() {
        marcasAdapter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentProductDetail oFragmentProductDetail = new FragmentProductDetail();
                oFragmentProductDetail.MarcasId = String.valueOf(product_marcas.get(recyclerView.getChildAdapterPosition(v)).getId());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.main_container, oFragmentProductDetail);
                transaction.commit();
                transaction.addToBackStack(null);
            }
        });
    }

    private void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringBuilder sb = new StringBuilder();
        sb.append(Variable.HOST + "/markes/" + CategoriaId);
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
                        //     Collections.sort(product_marcas, new SortMarkbyName());
                        marcasAdapter = new MarcasAdapter(product_marcas);
                        recyclerView.setAdapter(marcasAdapter);
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
