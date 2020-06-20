package com.example.appproveedorgas;

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
import com.example.appproveedorgas.util.Product;
import com.example.appproveedorgas.util.Sortbymeasurement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GasdetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GasdetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GasdetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ArrayList<Product> products;
    private GasKilosAdapter gasKilosAdapter;
    public String MarcaGas = "";
    public String TipoGas = "";

    public GasdetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GasdetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GasdetailFragment newInstance(String param1, String param2) {
        GasdetailFragment fragment = new GasdetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_gasdetail, container, false);
        recyclerView = view.findViewById(R.id.gas_conatiner_detail_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Listar();
  /*
        productDetails=new ArrayList<>();

      productDetails.add(new ProductDetail(1,"solgas de 5 kilos",15,1,"1"));
        productDetails.add(new ProductDetail(1,"solgas de 10 kilos",15,1,"1"));
        productDetails.add(new ProductDetail(1,"solgas de 15 kilos",15,1,"1"));
        productDetails.add(new ProductDetail(1,"solgas de 45 kilos",15,1,"1"));

        gasKilosAdapter=new GasKilosAdapter(productDetails);
        recyclerView.setAdapter(gasKilosAdapter);*/
        return view;
    }

    void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        final DatabaseHelper db=  new DatabaseHelper(getContext());
        StringBuilder sb = new StringBuilder();
        sb.append("http://34.71.251.155/api/product/staff/markes/" + MarcaGas);
        final String TypeGas = TipoGas.split("-")[1];
        String url = sb.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        recyclerView.setAdapter(null);
                        Gson gson = new Gson();
                        products = gson.fromJson(response.substring(34,response.length()-1).trim(), new TypeToken<ArrayList<Product>>() {
                        }.getType());

                        for (Product item : products)
                            item.setImage("http://34.71.251.155/" + item.getImage());

                        for (int i = 0; i < products.size(); i++) {
                            String ProductTypeGas = products.get(i).getDetail_measurement_id().getName().split(" ")[1];
             ;
                            if (! TypeGas.equals(ProductTypeGas)) {
                                products.remove(i);
                                i--;
                            }
                        }
                        Collections.sort(products, new Sortbymeasurement());
                        gasKilosAdapter = new GasKilosAdapter(products,GasdetailFragment.this);
                        recyclerView.setAdapter(gasKilosAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        }){
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
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
