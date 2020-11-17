package com.mayorista.appproveedorgas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mayorista.appproveedorgas.pojo.account;
import com.mayorista.appproveedorgas.util.ProductRegister;
import com.mayorista.appproveedorgas.util.SortbyProductRegister;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MIsPedidosFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerView;
    private ArrayList<ProductRegister> mispedidos_list;
    private MIsPedidosAdapter mIsPedidosAdapter;
    private View view;

    public MIsPedidosFragment() {
        // Required empty public constructor
    }

    public static MIsPedidosFragment newInstance(String param1, String param2) {
        MIsPedidosFragment fragment = new MIsPedidosFragment();
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
        view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false);
        recyclerView = view.findViewById(R.id.mis_pedidos_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Listar();
        mispedidos_list = new ArrayList<>();
        //---


        return view;
    }

    void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final DatabaseHelper db = new DatabaseHelper(getContext());
        StringBuilder sb = new StringBuilder();
        account cuenta = db.getAcountToken();
        sb.append(Variable.HOST + "/product/staff/" + cuenta.getCompany_id());
        String url = sb.toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        recyclerView.setAdapter(null);
                        Gson gson = new Gson();
                        mispedidos_list = gson.fromJson(response.substring(34, response.length() - 1).trim(), new TypeToken<ArrayList<ProductRegister>>() {
                        }.getType());
                        for (ProductRegister item : mispedidos_list) {

                            item.getProduct().setImage(Variable.HOST_BASE + item.getProduct().getImage());
                            if (item.getProduct().getCategory_id().getId() == 2)
                                item.getProduct().setDescription(item.getProduct().getMarke_id().getName() + " " + item.getProduct().getDetail_measurement_id().getName());
                            item.getProduct().setDescription(item.getProduct().getDescription().substring(0, 1).toUpperCase() + item.getProduct().getDescription().substring(1));
                        }
                        Collections.sort(mispedidos_list, new SortbyProductRegister());
                        mIsPedidosAdapter = new MIsPedidosAdapter(mispedidos_list, MIsPedidosFragment.this);
                        recyclerView.setAdapter(mIsPedidosAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        queue.add(stringRequest);

        EditText txtFilterProduct = view.findViewById(R.id.txtBuscarProductos);
        txtFilterProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsPedidosAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
