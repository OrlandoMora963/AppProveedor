package com.example.appproveedorgas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appproveedorgas.routes.FetchURL;
import com.example.appproveedorgas.routes.TaskLoadedCallback;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executor;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    //---
    private DatabaseHelper db;
    //---
    RequestQueue requestQueue;
    int statusCode;
    //String baseUrl = "http://134.209.37.205:8000/api";
    String baseUrl = "http://34.71.251.155/api";
    //---
    public static Socket SOCKET;
    public String HOST_NODEJS = "http://34.71.251.155:9000";
    //--
    private ArrayList<Marker> pedidos = new ArrayList<>();
    //--
    private static final String TAG = "GAS";
    private Marker marcador;
    private Marker marcador_carro;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private LatLng posicion;
    private static final int DEFAULT_ZOOM = 16;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    Context context;
    private Location mLastKnownLocation;
    View view;
    //---
    private boolean stopAnimation;
    //---
    BroadcastReceiver updateUIReciver;
    //---
    private Polyline currentPolyline;
    //---
    private boolean isNoti2;
    private int noti_id2;
    private double noti_lat2, noti_lng2;

    //--
    //---
    public MapsFragment(int id, double lat, double lng) {
        noti_id2 = id;
        noti_lat2 = lat;
        noti_lng2 = lng;
        isNoti2 = true;
    }

    //--
    public MapsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        //---
        stopAnimation = false;
        //--
        context = getContext();
        //---
        this.db = new DatabaseHelper(getContext());


        //--
        InitSocketIO();
        //--
        FloatingActionButton fab_refresh = view.findViewById(R.id.fab_refresh);
        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Consultando pedidos", Toast.LENGTH_SHORT).show();
                getProductCategory();
            }
        });
        // Inflate the layout for this fragment
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        //--- response
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.appproveedorgas.mapsfragment");
        updateUIReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here
                if (intent != null) {
                    if (intent.getStringExtra("toastMessage") != null) {
                        Toast.makeText(getContext(), intent.getStringExtra("toastMessage").toString(), Toast.LENGTH_SHORT).show();
                    }

                    if (intent.getStringExtra("agregarmPedido") != null) {
                        if (intent.getStringExtra("agregarmPedido").equals("si")) {
                            double lat = Double.parseDouble(intent.getStringExtra("lat"));
                            double lng = Double.parseDouble(intent.getStringExtra("lng"));
                            final int id = Integer.parseInt(intent.getStringExtra("id"));
                            final LatLng latLng = new LatLng(lat, lng);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        agregarPedido(latLng, id, true);
                                    }
                                });
                            }
                        }
                    }
                }

            }
        };
        getActivity().registerReceiver(updateUIReciver, filter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getProductCategory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getProductCategory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (updateUIReciver != null)
            getActivity().unregisterReceiver(updateUIReciver);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //--------------------------------
        map.setOnMarkerClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        // Prompt the user for permission.

        getLocationPermission();

        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                if (marcador != null) marcador.remove();

                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                //marcador=mMap.addMarker(new MarkerOptions().position(latLng).title("Estas aqui").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                //marcador.setTag(1);
                                //setMarkerBounce(marcador);
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                getProductCategory();

                            } else {
                                //Toast.makeText(getApplicationContext(),"GPS Desactivado!",Toast.LENGTH_SHORT).show();
                                new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
                                    @Override
                                    public void gpsStatus(boolean isGPSEnable) {
                                        // turn on GPS
                                        if (isGPSEnable) {
                                            //---------------------------------
                                            updateLocationUI();
                                            getDeviceLocation();
                                            //LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                            //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                //getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && marker.getTag() != null) {
            Toast.makeText(getContext(), marker.getTag().toString(), Toast.LENGTH_SHORT).show();
            Intent myIntent = new Intent(getContext(), PedidoActivity.class);
            myIntent.putExtra("id_pedido", marker.getTag().toString());
            startActivityForResult(myIntent, 1);
        } else {
            Toast.makeText(getContext(), "Marker null", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //----
    private void setMarkerBounce(final Marker marker) {
        if (!stopAnimation) {
            final Handler handler = new Handler();
            final long startTime = SystemClock.uptimeMillis();
            final long duration = 2000;
            final Interpolator interpolator = new BounceInterpolator();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - startTime;
                    float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + t);

                    if (t > 0.0) {
                        handler.postDelayed(this, 16);
                    } else {
                        setMarkerBounce(marker);
                    }
                }
            });
        }

    }

    //----
    private void cargarPedido() {
        if (isNoti2) {
            final LatLng latLng = new LatLng(noti_lat2, noti_lng2);
            Log.d("DATA NOTI", "prepare data");
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("DATA NOTI", "add data");
                        agregarPedido(latLng, noti_id2, true);
                    }
                });
            }
            isNoti2 = false;
        }

    }

    //----
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getProductCategory() {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        JSONObject object = new JSONObject();
        account cuenta = db.getAcountToken();
        try {
            object.put("staff_id", cuenta.getDni());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = this.baseUrl + "/orders/distributor/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley get", response.toString());
                        try {
                            int st = response.getInt("status");
                            if (st == 200) {
                                JSONArray data = response.getJSONArray("data");
                                stopAnimation = true;
                                pedidos.clear();
                                // mMap.clear();
                                cargarPedido();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject pe = data.getJSONObject(i);
                                    double lat = pe.getDouble("latitude");
                                    double lng = pe.getDouble("longitude");
                                    final LatLng latLng = new LatLng(lat, lng);
                                    final int id = pe.getInt("id");
                                    boolean espera = false;
                                    if (pe.getString("status").equals("wait"))
                                    {
                                        espera = true;
                                        final boolean finalEspera = espera;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                agregarPedido(latLng, id, finalEspera);
                                            }
                                        });
                                    }
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
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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

    //------------------------------
    void InitSocketIO() {
        final JSONObject json_connect = new JSONObject();
        IO.Options opts = new IO.Options();
        // opts.forceNew = true;
        opts.reconnection = true;
        opts.query = "auth_token=thisgo77";
        try {
            json_connect.put("ID", "US01");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            SOCKET = IO.socket(HOST_NODEJS, opts);
            SOCKET.connect();
            // SOCKET.io().reconnectionDelay(10000);
            Log.d(TAG, "Node connect ok");
        } catch (URISyntaxException e) {
            Log.d(TAG, "Node connect error");
        }

        SOCKET.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //SOCKET.emit("new connect", json_connect);
                conect();
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER connect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER disconnect " + date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnect " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER timeout " + my_date);
            }
        });

        SOCKET.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String my_date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                Log.d(TAG, "SERVER reconnecting " + my_date);
            }
        });

        SOCKET.on("login", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    String msg = jsonObject.getString("message");
                    Log.d(TAG, jsonObject.toString());
                    if (msg.equals("ok")) {
                        Log.d(TAG, "AUTH ok");
                        // Snackbar.make(getWindow().getDecorView().findViewById(R.id.container), "Conexión Exitosa", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Realizando pedido", Toast.LENGTH_SHORT).show();

                            }
                        });

                         */
                    } else {
                        Log.e(TAG, "AUTH error");
                        /*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Verifique Conexión", Toast.LENGTH_SHORT).show();
                            }
                        });

                         */
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("send order", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                try {

                    //String msg = jsonObject.getString("message");
                    Log.d(TAG, "Confirm Order: " + jsonObject.toString());
                    //JSONObject data = jsonObject.getJSONObject("data");
                    double lat = jsonObject.getDouble("latitude");
                    double lng = jsonObject.getDouble("longitude");
                    final LatLng latLng = new LatLng(lat, lng);
                    final int id = jsonObject.getInt("order_id");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                agregarPedido(latLng, id, true);
                            }
                        });
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SOCKET.on("cancel order provider", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (this == null) {
                    return;
                }
                JSONObject jsonObject = (JSONObject) args[0];
                Log.d(TAG, "Cancel Order: " + jsonObject.toString());
            }
        });
    }

    private void conect() {
        Log.d(TAG, "emitiendo new conect");
        JSONObject data = new JSONObject();
        account cuenta = db.getAcountToken();
        try {
            data.put("ID", cuenta.getDni());
            data.put("type", "provider");
            data.put("company_id", cuenta.getCompany_id());
            Log.d(TAG, "conect " + data.toString());
            SOCKET.emit("new connect", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean existePedido(int id) {
        boolean existe = false;
        for (int i = 0; i < pedidos.size(); i++) {
            Marker mrk = pedidos.get(i);
            if (mrk.getTag().toString().equals(String.valueOf(id))) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    private void agregarPedido(LatLng latLng, int id, boolean espera) {
        //LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        account cuenta = db.getAcountToken();
        LatLng location_company = new LatLng(Double.parseDouble(cuenta.getCompany_latitude()), Double.parseDouble(cuenta.getCompany_longitude()));
        if (!existePedido(id)) {
            if (espera) {
                stopAnimation = false;
                marcador = mMap.addMarker(new MarkerOptions().position(latLng).title("Pedido: " + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                marcador.setTag(id);
                setMarkerBounce(marcador);
                pedidos.add(marcador);
            } else {
                marcador = mMap.addMarker(new MarkerOptions().position(latLng).title("Pedido: " + id).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                marcador.setTag(id);
                pedidos.add(marcador);
            }
            new FetchURL(getActivity()).execute(getUrl(location_company, latLng, "driving"), "driving");
        }

    }

    //------
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    //-----
    public void DibujarRuta(Object... values) {
        //Log.d("Maps FRagment",data);
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
