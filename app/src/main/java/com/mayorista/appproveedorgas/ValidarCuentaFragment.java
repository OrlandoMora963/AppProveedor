package com.mayorista.appproveedorgas;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class ValidarCuentaFragment extends Fragment {
    View view;
    //--
    private TextView txtCodigo, txtEmail;
    private TextView btnSolicitar, btnValidar;
    private ProgressBar progressBar;
    private TextView tv_espera;

    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_validar_cuenta, container, false);

        txtCodigo = view.findViewById(R.id.txtCodigo);
        txtEmail = view.findViewById(R.id.txtCorreo);
        btnSolicitar = view.findViewById(R.id.btnSolicitar);
        btnValidar = view.findViewById(R.id.btnValidar);
        progressBar = view.findViewById(R.id.pbValidarCodigo);
        tv_espera = view.findViewById(R.id.tv_espera);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String email = bundle.getString("email");
            txtEmail.setText(email);
        }

        ocultarProgress();
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                solicitarCodigo();
            }
        });

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarCuenta();
            }
        });

        return view;
    }

    private void solicitarCodigo() {
        if (getActivity() == null || getContext() == null) {
            return;
        }

        mostrarProgress();
        String email = txtEmail.getText().toString();

        if (email.equals("") || email.length() == 0) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "(*)Ingrese el campo email para solicitar código");

            ocultarProgress();
        } else {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Variable.HOST + "/company/owner/resetcode/";
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int status = response.getInt("status");
                                if (status == 200) {
                                    ocultarProgress();
                                    btnSolicitar.setVisibility(View.INVISIBLE);
                                    String message = response.getString("message");

                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Volley get", "error voley" + error.toString());
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                if (getActivity() == null) {
                                    return;
                                }
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    System.out.println(res);
                                    JSONObject obj = new JSONObject(res);
                                    Log.i("Bussiness", obj.toString());
                                    ocultarProgress();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    private void validarCuenta() {
        if (getActivity() == null || getContext() == null) {
            return;
        }

        mostrarProgress();
        String email = txtEmail.getText().toString();
        String codigo = txtCodigo.getText().toString();

        if (email.equals("") || email.length() == 0 ||
                codigo.equals("") || codigo.length() == 0) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "(*)Ingrese el campo email y código para válidar.");
            ocultarProgress();

        } else {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("email", email);
                jsonObject.put("codigo", Integer.parseInt(codigo));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Variable.HOST + "/company/owner/validate/";
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int status = response.getInt("status");
                                if (status == 200) {
                                    ocultarProgress();
                                    btnSolicitar.setVisibility(View.INVISIBLE);
                                    String message = response.getString("message");
                                    switch (message) {
                                        case "OK":

                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            startActivity(intent);

                                            Toast.makeText(getContext(), "Cuenta válidada ya puede acceder :)", Toast.LENGTH_LONG)
                                                    .show();

                                            if (getActivity() == null) {
                                                return;
                                            }
                                            getActivity().finish();
                                            break;
                                        case "INVALIDO":
                                            Toast.makeText(getContext(), "Código invalido :(", Toast.LENGTH_LONG)
                                                    .show();
                                            btnSolicitar.setVisibility(View.VISIBLE);
                                            txtCodigo.setText("");
                                            break;
                                        case "EMAIL_INVALIDO":
                                            Toast.makeText(getContext(), "Correo invalido, registrese por favor.", Toast.LENGTH_LONG)
                                                    .show();
                                            txtEmail.setText("");
                                            txtCodigo.setText("");
                                            break;
                                        case "ACTIVE_USER":
                                            Toast.makeText(getContext(), "Su cuenta esta válidado :)", Toast.LENGTH_LONG)
                                                    .show();

                                            Intent intento2 = new Intent(getContext(), MainActivity.class);
                                            startActivity(intento2);
                                            Toast.makeText(getContext(), "Cuenta válidada ya puede acceder :)", Toast.LENGTH_LONG)
                                                    .show();
                                            if (getActivity() == null) {
                                                return;
                                            }
                                            getActivity().finish();
                                            break;
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Volley get", "error voley" + error.toString());
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                if (getActivity() == null) {
                                    return;
                                }
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    System.out.println(res);
                                    JSONObject obj = new JSONObject(res);
                                    Log.i("Bussiness", obj.toString());
                                    ocultarProgress();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    private void ocultarProgress() {
        if (getActivity() == null) {
            return;
        }

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
        tv_espera.setVisibility(View.INVISIBLE);
    }

    private void mostrarProgress() {
        if (getActivity() == null) {
            return;
        }
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        tv_espera.setVisibility(View.VISIBLE);

    }
}