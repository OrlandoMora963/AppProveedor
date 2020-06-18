package com.example.appproveedorgas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.appproveedorgas.util.ProductDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GasCamionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GasCamionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GasCamionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private RecyclerView recyclerView;
    private ArrayList<ProductDetail> productDetails_list;
    private CamionGasAdapter camionGasAdapter;

    public GasCamionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GasCamionFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        View view=inflater.inflate(R.layout.fragment_gas_camion, container, false);
        recyclerView=view.findViewById(R.id.gas_camion_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Listar();
    /*    productDetails_list=new ArrayList<>();
        productDetails_list.add(new product_marcas(1,"gas solgas ",""));
        productDetails_list.add(new product_marcas(1,"gas solgas ",""));
        productDetails_list.add(new product_marcas(1,"gas solgas ",""));

        camionGasAdapter=new CamionGasAdapter(productDetails_list);
        recyclerView.setAdapter(camionGasAdapter);*/

        return view;


    }
    private void Listar() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringBuilder sb = new StringBuilder();
        sb.append("http://34.71.251.155/api/product/gas/gas-cisterna");
        String url = sb.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        recyclerView.setAdapter(null);
                        Gson gson = new Gson();
                        productDetails_list = gson.fromJson(response.substring(34,response.length()-1).trim(), new TypeToken<ArrayList<ProductDetail>>() {
                        }.getType());
                        camionGasAdapter = new CamionGasAdapter(productDetails_list);
                        for (ProductDetail item : productDetails_list) {
                            item.setImage("http://34.71.251.155/"+item.getImage());
                        }
                        recyclerView.setAdapter(camionGasAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "That didn't work!", Toast.LENGTH_SHORT).show();
            }
        });
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
