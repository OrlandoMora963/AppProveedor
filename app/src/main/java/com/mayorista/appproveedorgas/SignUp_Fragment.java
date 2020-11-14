package com.mayorista.appproveedorgas;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mayorista.appproveedorgas.addressLocation.Constants;
import com.mayorista.appproveedorgas.addressLocation.FetchAddressIntentService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mayorista.appproveedorgas.pojo.account;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp_Fragment extends Fragment implements OnClickListener {

    int statusCode;
    //----
    private DatabaseHelper db;
    //---
    private static View view;
    private static EditText fullName, emailId, mobileNumber, location,
            password, confirmPassword, company_id, fullName_nombre;
    private static TextView login, singup_title;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    //-------
    private static EditText company_name, company_phone, company_address, company_lat,
            company_lng, company_ruc;

    private String type_proveedor;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    ProgressBar progressBar_gps;

    private double latitude, longitude;
    //--
    private String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    //---
    private Toast toastGps;
    private int nroCheckGps;

    private FragmentManager fragmentManager;
    private TextView terminos_condiciones;

    //--
    public SignUp_Fragment(String type_proveedor) {
        this.type_proveedor = type_proveedor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        mResultReceiver = new AddressResultReceiver(new Handler());
        fragmentManager = getActivity().getSupportFragmentManager();
        boolean pro = false;
        latitude = 0;
        longitude = 0;
        //--
        toastGps = Toast.makeText(getContext(), "Verificando GPS!", Toast.LENGTH_SHORT);
        nroCheckGps = 0;
        //---
        if (type_proveedor.equals("t1"))
            pro = true;
        initViews(pro);
        setListeners();
        this.db = new DatabaseHelper(getContext());
        //---
        CardView cardView = view.findViewById(R.id.card_id);
        progressBar_gps = view.findViewById(R.id.progressBar_gps);
        progressBar_gps.setVisibility(View.VISIBLE);
        deshabilitarTouch();
        Toast.makeText(getContext(), "Verificando posicion", Toast.LENGTH_SHORT).show();
        if (type_proveedor.equals("t1"))
            cardView.setVisibility(View.VISIBLE);
        else
            cardView.setVisibility(View.GONE);
        //----
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        //---
        if (getActivity() != null)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getDeviceLocation();


        terminos_condiciones = view.findViewById(R.id.terminos_condiciones);

        terminos_condiciones.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Terminos_Condiciones_Activity.class);
                startActivity(intent);
            }
        });

        return view;
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

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //getDeviceLocation();
    }

    private void getDeviceLocation() {
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
                                habilitarTouch();
                                toastGps.cancel();
                                nroCheckGps = 0;
                                progressBar_gps.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Obteniendo ubicacion!", Toast.LENGTH_SHORT).show();
                                latitude = mLastKnownLocation.getLatitude();
                                longitude = mLastKnownLocation.getLongitude();
                                startIntentService(mLastKnownLocation);
                            } else {
                                //Toast.makeText(getContext(),"Activando GPS!",Toast.LENGTH_SHORT).show();
                                toastGps.show();
                                nroCheckGps++;
                                new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
                                    @Override
                                    public void gpsStatus(boolean isGPSEnable) {
                                        // turn on GPS
                                        if (isGPSEnable) {
                                            //getDeviceLocation();
                                        }
                                    }
                                });
                                if (nroCheckGps == 3) {
                                    toastGps.cancel();
                                    showDialogCloseApp();
                                } else {
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    getDeviceLocation();
                                                }
                                            },
                                            10000);
                                }
                            }
                        } else {
                            Log.d("GPS", "Current location is null. Using defaults.");
                            Log.e("GPS", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //------
    private void displayAddressOutput() {
        location.setText(mAddressOutput);
        company_address.setText(mAddressOutput);
    }

    // Initialize all views
    private void initViews(boolean propietario) {

        fullName = (EditText) view.findViewById(R.id.fullName);
        fullName_nombre = (EditText) view.findViewById(R.id.fullName_nombre);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        mobileNumber = (EditText) view.findViewById(R.id.mobileNumber);
        location = (EditText) view.findViewById(R.id.location);
        company_id = (EditText) view.findViewById(R.id.company_id);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);

        company_name = view.findViewById(R.id.company_name);
        company_phone = view.findViewById(R.id.company_phone);
        company_address = view.findViewById(R.id.company_address);
        company_ruc = view.findViewById(R.id.company_ruc);
        singup_title = view.findViewById(R.id.tv_signup);

        singup_title.setText("Datos de Repartidor");

        if (propietario) {
            company_id.setVisibility(View.GONE);
            singup_title.setText("Datos de Propietario");
        }

        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);


        // Setting text selector over textviews
        //XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            //ColorStateList csl = ColorStateList.createFromXml(getResources(),
            //		xrp);

            //login.setTextColor(csl);
            //terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Call checkValidation method
                if (type_proveedor.equals("t1"))
                    checkValidation_propietario();
                else
                    checkValidation();
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private void checkValidation() {
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getNombre = fullName_nombre.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getLocation = location.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();
        //---
        String getCompanyID = company_id.getText().toString();
        //String getType = spinner_type.getSelectedItem().toString();
        // Pattern match for email id
        Pattern p = Pattern.compile(Variable.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getNombre.equals("") || getNombre.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getLocation.equals("") || getLocation.length() == 0
                || getCompanyID.equals("") || getCompanyID.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "Todos los campos son requeridos.");

            // Check if email id valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Tu correo es incorrecto.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Los password no coinsiden.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Por favor, acepte los terminos y condiciones.");

        else if (latitude == 0 || longitude == 0)
            new CustomToast().Show_Toast(getActivity(), view,
                    "La ubicacion del dispositivo no es correcto");
            // Else do signup or do your stuff
        else {
            Toast.makeText(getActivity(), "Registrando distribuidor.", Toast.LENGTH_SHORT)
                    .show();
            postDataRegistro(getEmailId, getPassword, getEmailId, getFullName, getMobileNumber, getLocation, getCompanyID, getNombre);
        }

    }


    public void postDataRegistro(final String username, final String password, final String email,
                                 final String client_id, final String phone,
                                 final String address, final String companyId, final String name) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("username", username);
            object.put("password", password);
            object.put("email", email);
            object.put("staff_id", client_id);
            object.put("phone", phone);
            object.put("address", address);
            object.put("company_id", companyId);
            object.put("name", name);
            object.put("latitude", latitude);
            object.put("longitude", longitude);
            Log.d("Voley post out", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/company/distributor/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Voley post", response.toString());
                        try {
                            int status = response.getInt("status");
                            String msj = response.getString("message");
                            if (status == 201) {
                                //db.insertData(new account(0,client_id,email,phone,address,password,"-1",1,data.getString("company_id"),companyName,companyPhone,ComapnyAddress,String.valueOf(latitude),String.valueOf(longitude),name,companyRuc)))
                                JSONObject companyJS = response.getJSONObject("data");
                                if (db.insertData(new account(0, client_id, email, phone, address, password, "-1", 2, companyId, companyJS.getString("name"), companyJS.getString("phone"), companyJS.getString("address"), String.valueOf(companyJS.getDouble("latitude")), String.valueOf(companyJS.getDouble("longitude")), name, companyJS.getString("ruc")))) {
                                    Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                                    // new MainActivity().replaceLoginFragment();

                                    postDataLogin(username, password);
                                } else {
                                    Toast.makeText(getContext(), "Error en base de datos", Toast.LENGTH_SHORT).show();
                                }
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
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Toast.makeText(getContext(), "Error en operacion", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void postDataLogin(final String username, final String password) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("username", username);
            object.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/auth/obtain_token/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Voley post", "login: " + response.toString());
                        String tok = "";
                        try {

                            tok = response.getString("token");
                            account cuenta = db.getCuentaUserPass(username, password);

                            cuenta.setToken(tok);
                            db.clearToken();
                            db.updateData(cuenta);

                            /*Intent myIntent = new Intent(getContext(), HomeActivity.class);
                            startActivity(myIntent);
                            getActivity().finish();*/

                            fragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                    .replace(R.id.frameContainer, new ValidarCuentaFragment(), Variable.TipoProveedorFragment)
                                    .commit();

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
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        //String msj = obj.getString("message");


                        new CustomToast().Show_Toast(getActivity(), view, "Error en usuario o contraseña");


                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Toast.makeText(getContext(), "Error en operacion", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    //-------- propietario
    // Check Validation Method
    private void checkValidation_propietario() {
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getNombre = fullName_nombre.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getLocation = location.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();
        //---
        String getCompanyName = company_name.getText().toString();
        String getCompanyPhone = company_phone.getText().toString();
        String getCompanyAddrees = company_address.getText().toString();
        String getCompanyRuc = company_ruc.getText().toString();

        //String getType = spinner_type.getSelectedItem().toString();
        // Pattern match for email id
        Pattern p = Pattern.compile(Variable.regEx);
        Matcher m = p.matcher(getEmailId);
        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getNombre.equals("") || getNombre.length() == 0
                || getCompanyRuc.equals("") || getCompanyRuc.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getLocation.equals("") || getLocation.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0
                || getCompanyName.equals("") || getCompanyName.length() == 0
                || getCompanyPhone.equals("") || getCompanyPhone.length() == 0
                || getCompanyAddrees.equals("") || getCompanyAddrees.length() == 0)

            new CustomToast().Show_Toast(getActivity(), view,
                    "Todos los campos son requeridos.");

            // Check if email id valid or not
        else if (!m.find())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Tu correo es incorrecto.");

            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            new CustomToast().Show_Toast(getActivity(), view,
                    "Los password no coinsiden.");

            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            new CustomToast().Show_Toast(getActivity(), view,
                    "Por favor, acepte los terminos y condiciones.");

        else if (latitude == 0 || longitude == 0)
            new CustomToast().Show_Toast(getActivity(), view,
                    "La ubicacion del dispositivo no es correcto");

            // Else do signup or do your stuff
        else {
            Toast.makeText(getActivity(), "Registrando propietario", Toast.LENGTH_SHORT)
                    .show();
            postDataRegistro_propietario(getCompanyName, getCompanyPhone, getCompanyAddrees, getEmailId, getPassword, getEmailId, getFullName, getMobileNumber, getLocation, getNombre, getCompanyRuc);
        }


    }

    public void postDataRegistro_propietario(final String companyName, final String companyPhone, final String ComapnyAddress,
                                             final String username, final String password, final String email, final String client_id, final String phone, final String address,
                                             final String name, final String companyRuc) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("company_name", companyName);
            object.put("company_phone", companyPhone);
            object.put("company_address", ComapnyAddress);
            object.put("company_latitude", latitude);
            object.put("company_longitude", longitude);
            object.put("company_ruc", companyRuc);
            //-------
            object.put("username", username);
            object.put("password", password);
            object.put("email", email);

            object.put("staff_id", client_id);
            object.put("phone", phone);
            object.put("address", address);
            object.put("name", name);
            object.put("latitude", latitude);
            object.put("longitude", longitude);

            Log.d("Voley post propietario", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/company/owner/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Voley post", response.toString());
                        try {
                            int status = response.getInt("status");
                            String msj = response.getString("message");
                            if (status == 201) {
                                JSONObject data = response.getJSONObject("data");
                                if (db.insertData(new account(0, client_id, email, phone, address,
                                        password, "-1", 1, data.getString("company_id"), companyName, companyPhone,
                                        ComapnyAddress, String.valueOf(latitude), String.valueOf(longitude), name, companyRuc))) {
                                    Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();
                                    // new MainActivity().replaceLoginFragment();

                                    // postDataLogin(username, password);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", email);
                                    ValidarCuentaFragment validarCuentaFragment = new ValidarCuentaFragment();
                                    validarCuentaFragment.setArguments(bundle);

                                    fragmentManager
                                            .beginTransaction()
                                            .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                                            .replace(R.id.frameContainer, validarCuentaFragment, Variable.TipoProveedorFragment)
                                            .commit();

                                } else {
                                    Toast.makeText(getContext(), "Error en base de datos", Toast.LENGTH_SHORT).show();
                                }
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
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(), msj, Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        Toast.makeText(getContext(), "Error en operacion", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    //----- address location
    private void startIntentService(Location mLastLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this.getContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d("Location", "Direccion encontrado");
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }
    }

    private void showDialogCloseApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Lo sentimos");
        builder.setMessage("Tenemos inconvenientes con el GPS del dispoaitivo. Asegurese de contar con internet y tener habilitado el GPS!");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                if (getActivity() != null)
                    getActivity().finish();
            }
        });

        builder.setCancelable(false);
        builder.show();

    }

    //---
    private void deshabilitarTouch() {
        if (getActivity() != null)
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    private void habilitarTouch() {
        if (getActivity() != null)
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
