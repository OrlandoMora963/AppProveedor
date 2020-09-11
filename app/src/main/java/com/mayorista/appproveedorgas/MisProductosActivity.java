package com.mayorista.appproveedorgas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;

public class MisProductosActivity extends AppCompatActivity implements MIsPedidosFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);

        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
         transaction.add(R.id.mis_pedidos_container,new MIsPedidosFragment());
         transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
