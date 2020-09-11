package com.mayorista.appproveedorgas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ActualizarPosicionActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actulizar_pocicion);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.db = new DatabaseHelper(getApplicationContext());
        getLocationPermission();
        getDeviceLocation();
    }

    // MI UBICACION
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {

        final account cuenta = db.getAcountToken();

        if (mLocationPermissionGranted) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            JSONObject data = new JSONObject();
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            String url = baseUrl + "/staff/coordinates/";
                            if (location != null) {
                                try {
                                    data.put("latitude", location.getLatitude());
                                    data.put("longitude", location.getLongitude());
                                    data.put("company_id", cuenta.getCompany_id());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, data,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    int status = response.getInt("status");
                                                    if (status == 200) {
                                                        String message = response.getString("message");
                                                        if (db.actualizarPosicion(cuenta.getCompany_id(), location.getLatitude(), location.getLongitude())) {
                                                            Log.i("Business", "Actualizado posicion sqllite");
                                                        } else {
                                                            Log.i("Business", "No se actualizo la pocicion sqllite");
                                                        }
                                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                                                String res = new String(response.data,
                                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                                // Now you can use any deserializer to make sense of data
                                                JSONObject obj = new JSONObject(res);
                                                Log.d("Voley post", obj.toString());
                                                String msj = obj.getString("message");
                                                Toast.makeText(getApplicationContext(), msj, Toast.LENGTH_SHORT).show();

                                            } catch (UnsupportedEncodingException e1) {
                                                e1.printStackTrace();
                                            } catch (JSONException e2) {
                                                e2.printStackTrace();
                                            }
                                        }
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
                                requestQueue.add(jsonObjectRequest);
                            }
                        }
                    });
        }
    }

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseHelper db;

    //--
    private String baseUrl = "http://34.71.251.155/api";
}
