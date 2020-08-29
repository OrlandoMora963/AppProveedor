package com.example.appproveedorgas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.appproveedorgas.routes.TaskLoadedCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements TaskLoadedCallback {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean isNoti = false;
    private int noti_id;
    private double noti_lat, noti_lng;
    //--
    private DatabaseHelper db;


    //--
    private MenuItem usuarioItem;

    //---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //-----
        this.db = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.activity_home);



        //------
        //---
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras() != null) {
                noti_lat = Double.parseDouble(intent.getStringExtra("lat"));
                noti_lng = Double.parseDouble(intent.getStringExtra("lng"));
                noti_id = Integer.parseInt(intent.getStringExtra("id"));
                isNoti = true;
            }
        }
        Log.d("ON CREATED", "CREANDO....");
        //-----
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        com.example.appproveedorgas.ViewPageAdapter adapter = new com.example.appproveedorgas.ViewPageAdapter(getSupportFragmentManager());

        if (isNoti) {
            Log.d("Intent home", "recibido id " + noti_id);
            adapter.AddFragment(new MapsFragment(noti_id, noti_lat, noti_lng), "Map");
            isNoti = false;
        } else
            adapter.AddFragment(new MapsFragment(), "Map");

        adapter.AddFragment(new OrderFragment(), "Pedidos");
        account cuenta = db.getAcountToken();
        if (cuenta.getType() == 1)
            adapter.AddFragment(new ProductFragment(), "Productos");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        if (getSupportActionBar() != null) {
            //getSupportActionBar().setTitle("Proveedor");
            getSupportActionBar().setElevation(0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
        //----

    }
    @Override
    protected void onResume() {
        super.onResume();

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.select();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        account cuenta = db.getAcountToken();
        usuarioItem = menu.findItem(R.id.item_usuarios);


        if (cuenta.getType() == 1) {
            usuarioItem.setVisible(true);
        } else {
            usuarioItem.setVisible(false);
        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_pocicion:
                Intent intent1 = new Intent(this, ActulizarPocicionActivity.class);
                startActivity(intent1);
                break;
            case R.id.item_perfil:
                Toast.makeText(getApplicationContext(), "Perfil", Toast.LENGTH_SHORT).show();
                Intent myIntentPro = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(myIntentPro);
                return true;

            case R.id.item_usuarios:
                Intent intent = new Intent(getBaseContext(), CreateUserActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onTaskDone(Object... values) {
        /* if (currentPolyline != null)
            currentPolyline.remove(); */
        //Log.d("Route",values.toString());
        EnviarToMaps(values);
        //currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void EnviarToMaps(Object... values) {
        com.example.appproveedorgas.ViewPageAdapter viewPageAdapter = (ViewPageAdapter) viewPager.getAdapter();
        MapsFragment maps = (MapsFragment) viewPageAdapter.getItem(0);
        // maps.DibujarRuta(values);
    }



}
