package com.mayorista.appproveedorgas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class CreateUserFragment extends Fragment {

    private DatabaseHelper db;
    private View view;
    private TextView idempresa;
    private TextView txtDireccion, txtTelefono, txtNombre, txtCliente_id, txtPassword, txtUsuario, txtPasswordRepeat;
    private String baseUrl = "http://34.71.251.155/api";
    private Button btnGuardar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_user, container, false);
        this.db = new DatabaseHelper(getContext());
        account cuenta = db.getAcountToken();

        idempresa = view.findViewById(R.id.txtCompaniaId);
        txtDireccion = view.findViewById(R.id.txtDireccion);
        txtNombre = view.findViewById(R.id.txtNombre);
        txtTelefono = view.findViewById(R.id.txtCelular);
        txtCliente_id = view.findViewById(R.id.txtDni);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtUsuario = view.findViewById(R.id.txtUser);
        txtPasswordRepeat = view.findViewById(R.id.txtPasswordRepeat);

        idempresa.setEnabled(false);

        idempresa.setText(cuenta.getCompany_id());
        txtDireccion.setText(cuenta.getDireccion());

        btnGuardar = view.findViewById(R.id.btnGuardarUsuario);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = txtNombre.getText().toString();
                String cliente_id = txtCliente_id.getText().toString();
                String telefono = txtTelefono.getText().toString();
                String direccion = txtDireccion.getText().toString();
                String usuario = txtUsuario.getText().toString();
                String password = txtPassword.getText().toString();
                String passwordRepeat = txtPasswordRepeat.getText().toString();
                String empresa = idempresa.getText().toString();

                //Validamos usuarios
                if (nombre.equals("") || nombre.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo NOMBRE es requerido.");
                } else if (cliente_id.equals("") || cliente_id.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo DNI es requerido.");
                } else if (telefono.equals("") || telefono.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo CELULAR es requerido.");
                } else if (direccion.equals("") || direccion.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo DIRECCION es requerido.");
                } else if (usuario.equals("") || usuario.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo USUARIO es requerido.");
                } else if (password.equals("") || password.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo PASSWORD es requerido.");
                } else if (passwordRepeat.equals("") || passwordRepeat.length() == 0) {
                    new CustomToast().Show_Toast(getActivity(), view, "El campo REPETIR PASSWORD es requerido.");
                } else if (!password.equals(passwordRepeat)) {
                    new CustomToast().Show_Toast(getActivity(), view, "Las contraseñas no coinciden, vuelva a intentar");
                } else {
                    Toast.makeText(getActivity(), "Registrando usuario.", Toast.LENGTH_SHORT)
                            .show();
                    insertarUsuario(usuario, password, cliente_id, usuario, telefono, direccion, empresa, nombre);
                    limpiarTextos();
                }

            }
        });


        return view;
    }


    private void insertarUsuario(String username, final String password, final String client_id, final String email,
                                 final String phone, final String address, final String companyId, final String name) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();

        account cuenta = db.getAcountToken();
        try {
            // Datos Personales
            object.put("company_id", companyId);
            object.put("name", name);
            object.put("staff_id", client_id);
            object.put("phone", phone);
            object.put("address", address);
            object.put("email", email);
            object.put("latitude", cuenta.getCompany_latitude());
            object.put("longitude", cuenta.getCompany_longitude());

            // Datos Accesos
            object.put("username", username);
            object.put("password", password);

            Log.d("Voley post out", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = this.baseUrl + "/company/distributor/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Voley post", response.toString());
                        try {
                            int status = response.getInt("status");
                            String msj = response.getString("message");
                            if (status == 201) {
                                //JSONObject companyJS = response.getJSONObject("data");
                                Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley post", "error voley" + error.toString());
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        Toast.makeText(getContext(), "Error en operacion", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void limpiarTextos() {
        account cuenta = db.getAcountToken();
        idempresa.setText(cuenta.getCompany_id());
        txtDireccion.setText(cuenta.getDireccion());

        txtNombre.setText(null);
        txtTelefono.setText(null);
        txtCliente_id.setText(null);
        txtPassword.setText(null);
        txtUsuario.setText(null);
        txtPasswordRepeat.setText(null);
    }

}
