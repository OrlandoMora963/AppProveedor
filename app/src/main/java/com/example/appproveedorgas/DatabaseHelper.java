package com.example.appproveedorgas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DaTABASE_NAME = "appgasProveedor.db";
    private static final String TABLE_NAME = "account_pro";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "dni";
    private static final String COL_3 = "email";
    private static final String COL_4 = "telefono";
    private static final String COL_5 = "direccion";
    private static final String COL_6 = "password";
    private static final String COL_7 = "token";
    private static final String COL_8 = "tipe";
    public static final String COL_9 = "company_id";
    //-----
    private static final String COL_10 = "company_name";
    private static final String COL_11 = "company_phone";
    private static final String COL_12 = "company_address";
    private static final String COL_13 = "company_latitude";
    private static final String COL_14 = "company_longitude";
    //----12/12/2019
    private static final String COL_15 = "nombre";//user
    private static final String COL_16 = "company_ruc";//company

    //----
    public DatabaseHelper(@Nullable Context context) {
        super(context, DaTABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + COL_2 + " TEXT, " + COL_3 + " TEXT , " + COL_4 + " TEXT, " + COL_5 + " TEXT, " + COL_6 + " TEXT, " + COL_7 + " TEXT, " + COL_8 + " INTEGER, " + COL_9 + " TEXT, " + COL_10 + " TEXT, " + COL_11 + " TEXT, " + COL_12 + " TEXT, " + COL_13 + " TEXT, " + COL_14 + " TEXT, " + COL_15 + " TEXT, " + COL_16 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(account cuenta) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        ;
        contentValues.put(COL_2, cuenta.getDni());
        contentValues.put(COL_3, cuenta.getEmail());
        contentValues.put(COL_4, cuenta.getTelefono());
        contentValues.put(COL_5, cuenta.getDireccion());
        contentValues.put(COL_6, cuenta.getPassword());
        contentValues.put(COL_7, cuenta.getToken());
        contentValues.put(COL_8, cuenta.getType());
        contentValues.put(COL_9, cuenta.getCompany_id());
        //---
        contentValues.put(COL_10, cuenta.getCompany_name());
        contentValues.put(COL_11, cuenta.getCompany_phone());
        contentValues.put(COL_12, cuenta.getCompany_address());
        contentValues.put(COL_13, cuenta.getCompany_latitude());
        contentValues.put(COL_14, cuenta.getCompany_longitude());
        contentValues.put(COL_15, cuenta.getNombre());
        contentValues.put(COL_16, cuenta.getCompany_ruc());
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean ExistsID(int id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where ID = " + id;
        Cursor cursor = sqLiteDatabase.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public account getCuenta(int id) {
        account cuenta = new account();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where ID = " + id, null);
        Log.d("db", "cuenta consultado");
        Log.d("db", String.valueOf(cursor.getCount()));
        if (cursor.getCount() == 0) {
            cursor.close();
            cuenta = null;
        }
        if (cursor.moveToNext()) {
            cuenta = new account(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(14));
            cursor.close();
        }
        return cuenta;
    }

    public account getCuentaUserPass(String user, String pass) {
        account cuenta = new account();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where email = '" + user + "' and password = '" + pass + "'", null);
        Log.d("db", "cuenta consultado");
        Log.d("db", String.valueOf(cursor.getCount()));
        if (cursor.getCount() == 0) {
            cursor.close();
            cuenta = null;
        }
        if (cursor.moveToNext()) {
            cuenta = new account(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13),
                    cursor.getString(14),
                    cursor.getString(15));
            cursor.close();
        }
        return cuenta;
    }

    public boolean updateData(account cuenta) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, cuenta.getID());
        contentValues.put(COL_2, cuenta.getDni());
        contentValues.put(COL_3, cuenta.getEmail());
        contentValues.put(COL_4, cuenta.getTelefono());
        contentValues.put(COL_5, cuenta.getDireccion());
        contentValues.put(COL_6, cuenta.getPassword());
        contentValues.put(COL_7, cuenta.getToken());
        contentValues.put(COL_8, cuenta.getType());
        contentValues.put(COL_9, cuenta.getCompany_id());
        //---
        contentValues.put(COL_10, cuenta.getCompany_name());
        contentValues.put(COL_11, cuenta.getCompany_phone());
        contentValues.put(COL_12, cuenta.getCompany_address());
        contentValues.put(COL_13, cuenta.getCompany_latitude());
        contentValues.put(COL_14, cuenta.getCompany_longitude());
        contentValues.put(COL_15, cuenta.getNombre());
        contentValues.put(COL_16, cuenta.getCompany_ruc());
        sqLiteDatabase.update(TABLE_NAME, contentValues, "ID = ?", new String[]{String.valueOf(cuenta.getID())});
        return true;
    }

    public boolean actualizarPosicion(String id, double lat, double lon) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_13, lat);
        contentValues.put(COL_14, lon);
        Log.i("Bussiness", contentValues.toString());
        int i = sqLiteDatabase.update(TABLE_NAME, contentValues, "company_id = '" + id + "'", null);
        return i > 0;
    }

    public void clearToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("update " + TABLE_NAME + " set token = '-1' ", null);
        Log.d("db", "actualizando");
        cursor.moveToFirst();
        cursor.close();
    }

    public boolean existsToken() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where token != '-1'", null);
        Log.d("db", "cuenta consultado");
        Log.d("db", String.valueOf(cursor.getCount()));
        int nro = cursor.getCount();
        cursor.close();
        return nro > 0;
    }

    public String getToken() {
        String tok = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where token != '-1'", null);
        if (cursor.moveToNext())
            tok = cursor.getString(6);
        cursor.close();
        return tok;
    }

    public account getAcountToken() {
        account cuenta = new account();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where token != '-1'", null);
        if (cursor.moveToNext())
            cuenta = new account(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getString(10),
                    cursor.getString(11),
                    cursor.getString(12),
                    cursor.getString(13),
                    cursor.getString(14),
                    cursor.getString(15));
        cursor.close();
        return cuenta;
    }
}
