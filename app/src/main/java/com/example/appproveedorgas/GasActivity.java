package com.example.appproveedorgas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class GasActivity extends AppCompatActivity implements GasFragment.OnFragmentInteractionListener,
      //  NavigationView.OnNavigationItemSelectedListener,
        GasdetailFragment.OnFragmentInteractionListener,
        GasCamionFragment.OnFragmentInteractionListener{

    @SuppressLint("RestrictedApi")
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
        bottomNavigationView.setItemIconTintList(null);
        SpannableString sPremiumActive = new SpannableString("Premium");
        sPremiumActive.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, sPremiumActive.length(), 0);

        BottomNavigationItemView   oMenuItem = findViewById(R.id.Gas_Premium);
        oMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.gasgreen));
        oMenuItem.setTitle(sPremiumActive);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            GasFragment oGasFragment=  new GasFragment();
            FragmentProductDetail oFragmentProductDetail =new FragmentProductDetail();
            oFragmentProductDetail.MarcasId="2";
            SpannableString sPremiumActive = new SpannableString("Premium");
            sPremiumActive.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, sPremiumActive.length(), 0);
            SpannableString sNormalActive = new SpannableString("Normal");
            sNormalActive.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0, sNormalActive.length(), 0);
            SpannableString sPremiumInactive = new SpannableString("Premium");
            sPremiumInactive.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, sPremiumInactive.length(), 0);
            SpannableString sNormalInactive = new SpannableString("Normal");
            sNormalInactive.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, sNormalInactive.length(), 0);
            BottomNavigationItemView oMenuItem;
            switch (menuItem.getItemId()) {
                case R.id.Gas_Premium:
                    menuItem.setIcon(R.drawable.gasgreen);
                        menuItem.setTitle(sPremiumActive);
                     oMenuItem = findViewById(R.id.Gas_Normal);

                    oMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.gaswhite));
                    oMenuItem.setTitle(sNormalInactive);
                    oGasFragment.TipoGas="gas-premium";
                    transaction.replace(R.id.gas_container,oGasFragment);
                    transaction.commit();
                    transaction.addToBackStack(null);
                    Toast.makeText(getBaseContext(), "Gas Premium", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.Gas_Normal:

                    menuItem.setIcon(R.drawable.gasgreen);
                    menuItem.setTitle(sNormalActive);
                    oMenuItem = findViewById(R.id.Gas_Premium);

                    oMenuItem.setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.gaswhite));
                    oMenuItem.setTitle(sPremiumInactive);
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

  /*  @Override
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
    }*/
}
