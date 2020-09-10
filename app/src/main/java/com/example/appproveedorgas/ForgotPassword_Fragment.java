package com.example.appproveedorgas;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword_Fragment extends Fragment implements
        OnClickListener {
    private View view;
    private FragmentManager fragmentManager;
    private EditText emailId;
    private TextView submit, btnCodigo;

    //--
    private ProgressBar progressBar;
    private TextView tv_espera;

    public ForgotPassword_Fragment() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forgotpassword_layout, container,
                false);
        initViews();
        ocultarProgress();
        setListeners();
        return view;
    }

    // Initialize the views
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initViews() {
        emailId = (EditText) view.findViewById(R.id.registered_emailid);
        submit = (TextView) view.findViewById(R.id.forgot_button);
        btnCodigo = (TextView) view.findViewById(R.id.btncodigo);
        fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        progressBar = view.findViewById(R.id.pbCambiarContraseña);
        tv_espera = view.findViewById(R.id.tv_espera);
    }

    // Set Listeners over buttons
    private void setListeners() {
        btnCodigo.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btncodigo:
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ResetPasswordFragment()).commit();
                break;

            case R.id.forgot_button:
                submitButtonTask();
                break;

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void submitButtonTask() {
        mostrarProgress();
        String getEmailId = emailId.getText().toString();
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getEmailId.equals("") || getEmailId.length() == 0) {
            new CustomToast().Show_Toast(Objects.requireNonNull(getActivity()), view,
                    "Ingrese su correo.");
        } else if (!m.find()) {
            new CustomToast().Show_Toast(Objects.requireNonNull(getActivity()), view,
                    "Tu correo es inválido.");
        } else {
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", getEmailId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String urlBase = "http://34.71.251.155";
            String url = urlBase + "/api/password_reset/";
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                if (status.equals("OK")) {
                                    ocultarProgress();
                                    fragmentManager
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                            .replace(R.id.frameContainer,
                                                    new ResetPasswordFragment()).commit();
                                    Toast.makeText(getContext(), "Por favor revise su bandeja de entrada o spam", Toast.LENGTH_LONG)
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
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    // Now you can use any deserializer to make sense of data
                                    JSONObject obj = new JSONObject(res);
                                    Log.d("Voley post", obj.toString());
                                    ocultarProgress();
                                    if (!obj.getString("email").equals("")) {
                                        Toast.makeText(getContext(),
                                                obj.getJSONArray("email")
                                                        .getString(0), Toast.LENGTH_SHORT)
                                                .show();
                                    }

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