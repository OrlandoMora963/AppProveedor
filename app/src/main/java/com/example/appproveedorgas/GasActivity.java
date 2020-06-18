package com.example.appproveedorgas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class GasActivity extends AppCompatActivity implements GasFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        GasdetailFragment.OnFragmentInteractionListener,
        GasCamionFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        GasFragment oGasFragment=  new GasFragment();
        oGasFragment.TipoGas="gas-premium";
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.gas_container,oGasFragment);
        transaction.commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            Intent intent;
            GasFragment oGasFragment=  new GasFragment();
            FragmentProductDetail oFragmentProductDetail =new FragmentProductDetail();
            oFragmentProductDetail.MarcasId="2";
            switch (menuItem.getItemId()) {
                case R.id.Gas_Premium:
                    oGasFragment.TipoGas="gas-premium";
                    transaction.replace(R.id.gas_container,oGasFragment);
                    transaction.commit();
                    transaction.addToBackStack(null);
                    Toast.makeText(getBaseContext(), "Gas Premium", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.Gas_Normal:
                    oGasFragment.TipoGas="gas-normal";
                    transaction.replace(R.id.gas_container,oGasFragment);
                    transaction.commit();
                    transaction.addToBackStack(null);
                    Toast.makeText(getBaseContext(), "Gas Normal", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.Camion:
                    transaction.replace(R.id.gas_container,new GasCamionFragment() );
                    transaction.commit();
                    transaction.addToBackStack(null);
                    Toast.makeText(getBaseContext(), "Camión", Toast.LENGTH_SHORT).show();
                    break;

            }
            return false;
        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Intent intent;
        GasFragment oGasFragment=  new GasFragment();
        switch (menuItem.getItemId()) {
            case R.id.Gas_Premium:
                oGasFragment.TipoGas="gas-premium";
                transaction.replace(R.id.gas_container,oGasFragment);
                transaction.commit();
                break;
            case R.id.Gas_Normal:
                oGasFragment.TipoGas="gas-normal";
                transaction.replace(R.id.gas_container,oGasFragment);
                transaction.commit();
                break;
            case R.id.Camion:
                transaction.replace(R.id.gas_container, new FragmentProductDetail());
                transaction.commit();
                break;
        }
        return false;
    }
}
