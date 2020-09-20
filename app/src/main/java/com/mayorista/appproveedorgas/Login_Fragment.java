package com.mayorista.appproveedorgas;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.mayorista.appproveedorgas.pojo.account;

import org.json.JSONException;
import org.json.JSONObject;


public class Login_Fragment extends Fragment implements OnClickListener {
    //----
    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    int statusCode;
    private DatabaseHelper db;
    //---
    private static View view;

    private static EditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp, btnValidarCuenta;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;

    private ProgressBar progressBar;
    //-----
    private Context context;

    //SocketIO  socketIO;
    //DBHelper db;
	/*
	private Socket socket;
	{
		try{
			socket = IO.socket("http:192.168.1.7:3000");
		}catch (URISyntaxException e){
			Log.e("error",e.toString());
			throw new RuntimeException(e);

		}
	}
	*/
    //------------------------------
    public Login_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);
        initViews();
        //------------- socket------------
		/*
		socketIO = new SocketIO();
		db = new DBHelper(getActivity());
		socketIO.connectSocket();
		socketIO.getSocket().on("login_conductor_ou", onLogin);
		 */
        //--------------------------------
        this.db = new DatabaseHelper(getContext());
        //---------------------
        setListeners();
        context = getContext();
        //c
        return view;
    }

    // Initiate Views
    private void initViews() {

        fragmentManager = getActivity().getSupportFragmentManager();
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(view.INVISIBLE);
        emailid = (EditText) view.findViewById(R.id.login_emailid);
        password = (EditText) view.findViewById(R.id.login_password);
        loginButton = (Button) view.findViewById(R.id.loginBtn);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        btnValidarCuenta = (TextView) view.findViewById(R.id.btnValidarCuenta);
        signUp = (TextView) view.findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) view
                .findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        //XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            //ColorStateList csl = ColorStateList.createFromXml(getResources(),
            //		xrp);

            //forgotPassword.setTextColor(csl);
            //show_hide_password.setTextColor(csl);
            //signUp.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);
        btnValidarCuenta.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:
                // Replace forgot password fragment with animation
                //socketIO.disconnectedSocket();
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Variable.ForgotPassword_Fragment).commit();
                break;

            case R.id.btnValidarCuenta:
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new ValidarCuentaFragment(), Variable.ForgotPassword_Fragment)
                        .commit();
                break;
            case R.id.createAccount:
                // Replace signup frgament with animation
                //socketIO.disconnectedSocket();
			/*
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer, new SignUp_Fragment(),
							Utils.SignUp_Fragment).commit();
			*/
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new TipoProveedorFragment(),
                                Variable.TipoProveedorFragment).commit();
                break;
        }

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();

        // Check patter for email id
        Pattern p = Pattern.compile(Variable.regEx);

        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view,
                    "Ingrese el correo y el password.");

        }
        // Check if email id is valid or not
		/*else if (!m.find())
		{
			loginLayout.startAnimation(shakeAnimation);
			new CustomToast().Show_Toast(getActivity(), view,
					"Tu email es incorrecto");
		}*/

        // Else do login and do your stuff
        else {
            progressBar.setVisibility(view.VISIBLE);

            //progressBar.setVisibility(view.INVISIBLE);
            //Intent myIntent = new Intent(getContext(),HomeActivity.class);
            //startActivity(myIntent);
            //getActivity().finish();
            postDataLogin(getEmailId, getPassword);

        }


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
                            if (cuenta != null) {
                                cuenta.setToken(tok);
                                db.clearToken();
                                db.updateData(cuenta);
                                progressBar.setVisibility(view.INVISIBLE);
                                if (context != null) {
                                    Intent myIntent = new Intent(context, HomeActivity.class);
                                    startActivity(myIntent);
                                    if (getActivity() != null)
                                        getActivity().finish();
                                }
                            } else {
                                progressBar.setVisibility(view.INVISIBLE);
                                verificarSiExiteUsuario(tok, username, password);
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
                        Log.d("Voley post", res);
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post", obj.toString());
                        //String msj = obj.getString("message");

                        loginLayout.startAnimation(shakeAnimation);
                        new CustomToast().Show_Toast(getActivity(), view, "Error en usuario o contraseña");
                        progressBar.setVisibility(view.INVISIBLE);

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

    // En caso de que la db este vacia y las credenciales son validas
    private void verificarSiExiteUsuario(final String token, final String email, final String contra) {
        if (getContext() == null) {
            return;
        }
        JWT parseToken = new JWT(token);
        Claim subscriptionMetaData = parseToken.getClaim("user_id");
        final int id_user = subscriptionMetaData.asInt();
        String url = Variable.HOST + "/staff/" + id_user;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                JSONObject data = response.getJSONObject("data");
                                String phone = data.getJSONObject("staff").getString("phone");
                                String staff_id = data.getJSONObject("staff").getString("staff_id");
                                String address = data.getJSONObject("staff").getString("address");
                                String type = data.getJSONObject("staff").getString("type");
                                String name = data.getJSONObject("staff").getString("name");
                                int type_insert;
                                if (type.equals("t1")) {
                                    type_insert = 1;
                                } else {
                                    type_insert = 2;
                                }
                                String company_id = data.getJSONObject("company").getString("company_id");
                                String companyName = data.getJSONObject("company").getString("name");
                                String companyPhone = data.getJSONObject("company").getString("phone");
                                String companyAddress = data.getJSONObject("company").getString("address");
                                String companyRuc = data.getJSONObject("company").getString("ruc");
                                String latitude = data.getJSONObject("company").getString("latitude");
                                String longitude = data.getJSONObject("company").getString("longitude");

                                if (db.insertData(new account(0, staff_id, email, phone, address,
                                        contra, token, type_insert, company_id, companyName, companyPhone,
                                        companyAddress, latitude, longitude, name, companyRuc))) {
                                    System.out.println("USUARIO CREADO EN LA DB LOCAL CORRECTAMENTE");
                                    if (context != null) {
                                        Intent myIntent = new Intent(context, HomeActivity.class);
                                        startActivity(myIntent);
                                        if (getActivity() != null)
                                            getActivity().finish();
                                    }
                                } else {
                                    System.out.println("NO SE CREO EL USUARIO");
                                }
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
                        System.out.println(res);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "JWT " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(objectRequest);
    }
}
