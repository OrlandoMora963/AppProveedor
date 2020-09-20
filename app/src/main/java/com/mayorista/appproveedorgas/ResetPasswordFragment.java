package com.mayorista.appproveedorgas;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
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

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class ResetPasswordFragment extends Fragment implements View.OnClickListener {
    private View view;

    private EditText txtCodigo;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private TextView submit, back;

    //--
    private ProgressBar progressBar;
    private TextView tv_espera;

    public ResetPasswordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reset_password, container,
                false);
        initViews();
        ocultarProgress();
        setListeners();
        return view;
    }

    // Initialize the views
    private void initViews() {
        txtCodigo = view.findViewById(R.id.txtCodigo);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtConfirmPassword = view.findViewById(R.id.txtConfirmPassword);
        submit = view.findViewById(R.id.btnResetearPassword);
        back = view.findViewById(R.id.backToLoginBtn);

        progressBar = view.findViewById(R.id.pbCambiarContraseña);
        tv_espera = view.findViewById(R.id.tv_espera);
    }

    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:
                Objects.requireNonNull(getActivity()).onBackPressed();
                //Objects.requireNonNull(getActivity()).finish();
                break;

            case R.id.btnResetearPassword:
                submitButtonTask();
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void submitButtonTask() {
        mostrarProgress();
        String password = txtPassword.getText().toString();
        String codigo = txtCodigo.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();
        if (password.equals("") || password.length() == 0 ||
                confirmPassword.equals("") || confirmPassword.length() == 0 ||
                codigo.equals("") || codigo.length() == 0)

            new CustomToast().Show_Toast(Objects.requireNonNull(getActivity()), view,
                    "(*) Los campos de código y contraseña son requeridos");
        else if (!password.equals(confirmPassword)) {
            new CustomToast().Show_Toast(Objects.requireNonNull(getActivity()), view,
                    "(*) Los campos de contraseña no coinciden");
        } else {


            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("password", password);
                jsonObject.put("token", codigo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = Variable.HOST + "/password_reset/confirm/";
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                if (status.equals("OK")) {
                                    ocultarProgress();
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getContext(), "Contraseña cambiada correctamente :)", Toast.LENGTH_LONG)
                                            .show();
                                    Objects.requireNonNull(getActivity()).finish();
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
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    System.out.println(res);
                                    JSONObject obj = new JSONObject(res);
                                    Log.i("Bussiness", obj.toString());
                                    StringBuilder mensajes = new StringBuilder();
                                    if (!obj.isNull("password")) {
                                        JSONArray messages = obj.getJSONArray("password");
                                        for (int i = 0; i < messages.length(); i++) {
                                            mensajes.append(messages.get(i)).append("\n");
                                        }
                                    }

                                    if (!obj.isNull("status")) {
                                        new CustomToast().Show_Toast(Objects.requireNonNull(getActivity()), view,
                                                "El código ingresado es incorrecto :(");
                                    }

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


    private void mostrarProgress() {
        if (getActivity() == null) {
            return;
        }
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        tv_espera.setVisibility(View.VISIBLE);

    }

    private void ocultarProgress() {
        if (getActivity() == null) {
            return;
        }
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.INVISIBLE);
        tv_espera.setVisibility(View.INVISIBLE);
    }
}
