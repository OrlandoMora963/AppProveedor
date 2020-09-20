package com.mayorista.appproveedorgas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.mayorista.appproveedorgas.pojo.account;

public class ProfileActivity extends AppCompatActivity {
    //---
    private DatabaseHelper db;
    //---
    TextInputEditText et_company_name, et_company_phone, et_company_address, et_company_lat, et_company_lng,et_company_ruc;
    TextInputEditText et_dni, et_correo, et_celular, et_direccion, et_company_id,et_nombre;
    TextView tv_profil_title;
    CardView card_company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //---
        this.db = new DatabaseHelper(getBaseContext());
        //--
        card_company = findViewById(R.id.card_empresa);
        //--
        et_company_name = findViewById(R.id.ti_company_name);
        et_company_phone = findViewById(R.id.ti_company_phone);
        et_company_address = findViewById(R.id.ti_company_address);
        et_company_lat = findViewById(R.id.ti_company_latitude);
        et_company_lng = findViewById(R.id.ti_company_longitude);
        et_company_ruc = findViewById(R.id.ti_company_ruc);
        //--
        tv_profil_title = findViewById(R.id.tv_perfil);
        et_dni = findViewById(R.id.ti_dni);
        et_correo = findViewById(R.id.ti_correo);
        et_celular = findViewById(R.id.ti_celular);
        et_direccion = findViewById(R.id.ti_direccion);
        et_company_id = findViewById(R.id.ti_company_id);
        et_nombre = findViewById(R.id.ti_nombre);
        //---
        getProfile();
    }
    private void getProfile(){
        account cuenta  =db.getAcountToken();
        if(cuenta.getType()==1){
            card_company.setVisibility(View.VISIBLE);
            //----
            et_company_name.setText(cuenta.getCompany_name());
            et_company_phone.setText(cuenta.getCompany_phone());
            et_company_address.setText(cuenta.getCompany_address());
            et_company_lat.setText(cuenta.getCompany_latitude());
            et_company_lng.setText(cuenta.getCompany_longitude());
            et_company_ruc.setText(cuenta.getCompany_ruc());
            //----
            et_dni.setText(cuenta.getDni());
            et_correo.setText(cuenta.getEmail());
            et_celular.setText(cuenta.getTelefono());
            et_direccion.setText(cuenta.getDireccion());
            et_company_id.setText(cuenta.getCompany_id());
            et_nombre.setText(cuenta.getNombre());
        }
        else{
            card_company.setVisibility(View.GONE);
            //----
            et_dni.setText(cuenta.getDni());
            et_correo.setText(cuenta.getEmail());
            et_celular.setText(cuenta.getTelefono());
            et_direccion.setText(cuenta.getDireccion());
            et_company_id.setText(cuenta.getCompany_id());
            et_nombre.setText(cuenta.getNombre());
        }
    }
}
