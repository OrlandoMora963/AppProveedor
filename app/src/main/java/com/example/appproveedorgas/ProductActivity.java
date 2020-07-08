package com.example.appproveedorgas;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ProductActivity extends AppCompatActivity implements
        FragmentProductDetail.OnFragmentInteractionListener,
        ProductMarcasFragment.OnFragmentInteractionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Bundle bundle = getIntent().getExtras();
         FragmentProductDetail oFragmentProductDetail=new FragmentProductDetail();
        oFragmentProductDetail.MarcasId=bundle.getString("MarcasId");
        //System.out.println("marcas ID============================>"+bundle.getString("MarcasId"));

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ProductMarcasFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
