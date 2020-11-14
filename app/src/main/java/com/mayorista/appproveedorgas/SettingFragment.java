package com.mayorista.appproveedorgas;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
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
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.mayorista.appproveedorgas.pojo.Product;
import com.squareup.picasso.Picasso;

public class SettingFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshSetting;
    //----
    private DatabaseHelper db;


    MaterialCardView cardView;
    Context context;
    LayoutParams layoutParams;
    LinearLayout linearLayout;

    View view;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        //---
        context = getContext();
        this.db = new DatabaseHelper(getContext());
        linearLayout = view.findViewById(R.id.contenedor);
        //--
        swipeRefreshSetting = view.findViewById(R.id.swipeRefreshSettings);
        swipeRefreshSetting.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Esto se ejecuta cada vez que se realiza el gesto
                postGetDataProduct();

                /*
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Log.d("Refresh", "despues de 1 segundor");
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        },
                        1000);

                 */
            }
        });
        //--

        postGetDataProduct();

        /*
        CreateCardViewProgrammatically(new Product(
                1,20.0,35.0,"","GAS","SOLGAS","kl"
        ));
        CreateCardViewProgrammatically(new Product(
                1,20.0,35.0,"","GAS","SOLGAS","kl"
        ));
        */
        return view;
    }

    public void CreateCardViewProgrammatically(Product producto, boolean select) {
        //--
        cardView = new MaterialCardView(context);
        layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(50, 50, 50, 50);
        //------
        LayoutParams layoutParams_tv = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams_tv.setMargins(30, 2, 30, 2);
        //-----
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(15);
        cardView.setPadding(10, 10, 10, 10);
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setMaxCardElevation(60);
        cardView.setCardElevation(30);
        //cardView.setPreventCornerOverlap(true);
        //cardView.setMaxCardElevation(6);
        LinearLayout li = new LinearLayout(context);
        li.setLayoutParams(layoutParams);
        li.setOrientation(LinearLayout.VERTICAL);
        // title
        LayoutParams layoutParams_ti = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        LinearLayout li_title = new LinearLayout(context);
        li_title.setLayoutParams(layoutParams_ti);
        li_title.setOrientation(LinearLayout.VERTICAL);
        //color
        /*
        int[] colors = {Color.parseColor("#fff3e0"),Color.parseColor("#FFFFFF")};
        //create a new gradient color
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gd.setCornerRadius(0f);
        */
        //li_title.setBackgroundColor(Color.argb(175,213,0,0));
        li_title.setBackgroundColor(Color.WHITE);
        TextView textview = new TextView(context);
        textview.setLayoutParams(layoutParams_tv);
        textview.setText(producto.getCategoria());
        textview.setTextSize(20);
        textview.setTextColor(Color.BLACK);
        textview.setPadding(5, 5, 5, 5);
        textview.setGravity(Gravity.LEFT);
        li_title.addView(textview);
        li.addView(li_title);
        // /title
        LinearLayout li_header = new LinearLayout(context);
        li_header.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.height_header)
        ));
        li_header.setOrientation(LinearLayout.VERTICAL);
        li_header.setBackgroundColor(Color.argb(175, 213, 0, 0));
        li.addView(li_header);
        //---
        LinearLayout li_content_horizontal = new LinearLayout(context);
        li_content_horizontal.setLayoutParams(layoutParams_ti);
        li_content_horizontal.setOrientation(LinearLayout.HORIZONTAL);
        //---
        LinearLayout li_content = new LinearLayout(context);
        li_content.setLayoutParams(new LayoutParams(
                (int) getResources().getDimension(R.dimen.width_content),
                LayoutParams.WRAP_CONTENT
        ));
        li_content.setOrientation(LinearLayout.VERTICAL);
        //---
        TextView textview1 = new TextView(context);
        textview1.setLayoutParams(layoutParams_tv);
        textview1.setText("MARCA: " + producto.getMarca());
        textview1.setTextSize(15);
        textview1.setTextColor(Color.BLACK);
        textview1.setPadding(0, 25, 0, 0);
        textview1.setGravity(Gravity.LEFT);
        li_content.addView(textview1);
        //---
        TextView textview2 = new TextView(context);
        textview2.setLayoutParams(layoutParams_tv);
        textview2.setText("PRESENTACION: " + producto.getMedida() + " " + producto.getUnidad_medida());
        textview2.setTextSize(15);
        textview2.setTextColor(Color.BLACK);
        //textview2.setPadding(25,25,25,25);
        textview2.setGravity(Gravity.LEFT);
        li_content.addView(textview2);
        //---
        TextView textview3 = new TextView(context);
        textview3.setLayoutParams(layoutParams_tv);
        textview3.setText("PRECIO: " + producto.getPrecio_unitario());
        textview3.setTextSize(15);
        textview3.setTextColor(Color.BLACK);
        //textview3.setPadding(25,25,25,25);
        textview3.setGravity(Gravity.LEFT);
        li_content.addView(textview3);
        CheckBox cb = new CheckBox(context);
        cb.setId(producto.getID());
        cb.setText("Seleccionado");
        cb.setTextColor(Color.argb(255, 51, 105, 30));
        cb.setLayoutParams(layoutParams_tv);
        cb.setPadding(0, 0, 0, 25);
        cb.setChecked(select);
        cb.setOnClickListener(getOnClickDoSomething(cb));
        li_content.addView(cb);
        //---
        li_content_horizontal.addView(li_content);
        //---- image
        LinearLayout li_image = new LinearLayout(context);
        li_image.setLayoutParams(layoutParams_ti);
        li_image.setOrientation(LinearLayout.VERTICAL);
        //--
        LayoutParams layoutParams_img = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        layoutParams_img.setMargins(0, 30, 0, 2);
        li_image.setGravity(Gravity.CENTER);
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams_img);
        imageView.setPadding(0, 0, 0, 0);
        //--- picasso
        Picasso.get()
                .load("http://34.71.251.155" + producto.getImagen())
                .placeholder(R.drawable.ic_photo_gray_24dp)
                .error(R.drawable.ic_report_problem_gray_24dp)
                .resizeDimen(R.dimen.width_image, R.dimen.width_image)
                .into(imageView);
        li_image.addView(imageView);
        //-----
        li_content_horizontal.addView(li_image);
        //---
        li.addView(li_content_horizontal);
        cardView.addView(li);
        linearLayout.addView(cardView);

    }

    private View.OnClickListener getOnClickDoSomething(final CheckBox button) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("check", "id " + button.getId());
                Log.d("check", "text " + button.getText().toString());
                Log.d("check", "text " + button.isChecked());
                if (button.isChecked())
                    postSelectProduct(button.getId());
                else
                    postnSelectProduct(button.getId());
            }
        };
    }

    private void postGetDataProduct() {
        if (swipeRefreshSetting != null)
            swipeRefreshSetting.setRefreshing(true);
        if (linearLayout != null)
            linearLayout.removeAllViews();
        //--
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/product/register/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley get", response.toString());
                        try {
                            JSONObject data = response.getJSONObject("data");
                            // cerveza
                            JSONObject cerveza = data.getJSONObject("Cerveza");
                            JSONArray seleted = cerveza.getJSONArray("selected");
                            for (int i = 0; i < seleted.length(); i++) {
                                JSONObject obj = seleted.getJSONObject(i);
                                CreateCardViewProgrammatically(new Product(obj.getInt("id"),
                                        obj.getDouble("measurement"),
                                        obj.getDouble("unit_price"),
                                        obj.getString("image"),
                                        obj.getJSONObject("category_id").getString("name"),
                                        obj.getJSONObject("marke_id").getString("name"),
                                        obj.getJSONObject("unit_measurement_id").getString("name")), true);
                            }

                            JSONArray nseleted = cerveza.getJSONArray("no_selected");
                            for (int i = 0; i < nseleted.length(); i++) {
                                JSONObject obj = nseleted.getJSONObject(i);
                                CreateCardViewProgrammatically(new Product(obj.getInt("id"),
                                        obj.getDouble("measurement"),
                                        obj.getDouble("unit_price"),
                                        obj.getString("image"),
                                        obj.getJSONObject("category_id").getString("name"),
                                        obj.getJSONObject("marke_id").getString("name"),
                                        obj.getJSONObject("unit_measurement_id").getString("name")), false);
                            }
                            //-- gas
                            JSONObject gas = data.getJSONObject("Gas");
                            JSONArray seletedg = gas.getJSONArray("selected");
                            for (int i = 0; i < seletedg.length(); i++) {
                                JSONObject obj = seletedg.getJSONObject(i);
                                CreateCardViewProgrammatically(new Product(obj.getInt("id"),
                                        obj.getDouble("measurement"),
                                        obj.getDouble("unit_price"),
                                        obj.getString("image"),
                                        obj.getJSONObject("category_id").getString("name"),
                                        obj.getJSONObject("marke_id").getString("name"),
                                        obj.getJSONObject("unit_measurement_id").getString("name")), true);
                            }
                            JSONArray nseletedg = gas.getJSONArray("no_selected");
                            for (int i = 0; i < nseletedg.length(); i++) {
                                JSONObject obj = nseletedg.getJSONObject(i);
                                CreateCardViewProgrammatically(new Product(obj.getInt("id"),
                                        obj.getDouble("measurement"),
                                        obj.getDouble("unit_price"),
                                        obj.getString("image"),
                                        obj.getJSONObject("category_id").getString("name"),
                                        obj.getJSONObject("marke_id").getString("name"),
                                        obj.getJSONObject("unit_measurement_id").getString("name")), false);
                            }
                            if (swipeRefreshSetting != null)
                                swipeRefreshSetting.setRefreshing(false);

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

    // Seleccionar productos
    private void postSelectProduct(int id) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("product_id", id);
            //object.put("selected",select);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/product/register/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Producto Seleccionado", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley get", "error voley" + error.toString());
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

    private void postnSelectProduct(int id) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JSONObject object = new JSONObject();
        // Enter the correct url for your api service site
        String url = Variable.HOST + "/product/register/" + id + "/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        /*
                        Log.d("Volley get",response.toString());
                        try {
                            JSONArray data = response.getJSONArray("data");
                            for(int i=0;i<data.length();i++){
                                JSONObject obj = data.getJSONObject(i);
                                CreateCardViewProgrammatically(new Product(obj.getInt("id"),
                                        obj.getDouble("measurement"),
                                        obj.getDouble("unit_price"),
                                        obj.getString("image"),
                                        obj.getJSONObject("category_id").getString("name"),
                                        obj.getJSONObject("marke_id").getString("name"),
                                        obj.getJSONObject("measurement_id").getString("name")));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        Toast.makeText(getContext(), "Producto Deseleccionado", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley get", "error voley" + error.toString());
                /*
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.d("Voley post",obj.toString());
                        String msj = obj.getString("message");
                        Toast.makeText(getContext(),msj , Toast.LENGTH_SHORT).show();

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                */
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

}
