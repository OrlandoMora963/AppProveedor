package com.example.appproveedorgas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonArrayRequest;
import com.example.appproveedorgas.pojo.Usuario;
import com.example.appproveedorgas.util.AdaptadorUsuario;
import com.example.appproveedorgas.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListUserFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelper db;
    private RecyclerView contenedor;
    private AdaptadorUsuario adaptador;


    public ListUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_user, container, false);

        this.db = new DatabaseHelper(getContext());
        contenedor = view.findViewById(R.id.rcvUsuarios);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutUsers);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarDatos();
            }
        });

        cargarDatos();

        return view;
    }


    private void cargarDatos() {
        final List<Usuario> usuarios = new ArrayList<>();
        account cuenta = db.getAcountToken();
        contenedor.setLayoutManager(new LinearLayoutManager(getContext()));

        // Implementacion lista de usuarios
        JSONArray object = new JSONArray();
        String baseUrl = "http://34.71.251.155/api";
        String url = baseUrl + "/company/users/" + cuenta.getCompany_id();
        JsonArrayRequest objectRequest = new JsonArrayRequest(Request.Method.GET, url, object, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        Usuario user = new Usuario();
                        user.setId(data.getString("staff_id"));
                        user.setNombre(data.getString("name"));
                        user.setCelular(data.getString("phone"));

                        usuarios.add(user);
                    }

                    adaptador = new AdaptadorUsuario(usuarios);
                    contenedor.setAdapter(adaptador);

                    if (swipeRefreshLayout != null)
                        swipeRefreshLayout.setRefreshing(false);
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

        VolleySingleton.getInstance(getContext()).addToRequestQueue(objectRequest);
    }
}
