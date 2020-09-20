package com.mayorista.appproveedorgas.pojo;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Mpedido {
    private int idPedido;
    private String fecha;
    private String precioTotal;
    private String estado;
    private List<Mpedido_detalle> detalle = new ArrayList<>();
    private Double Latitud;
    private Double Longitud;
    private String phone;
    private Context context;

    public Mpedido(int idPedido, String fecha, String precioTotal, String estado) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.estado = estado;

    }

    public Mpedido(int idPedido, String fecha, String precioTotal, String estado, List<Mpedido_detalle> detalle, String phone, Context context,
                   Double Latitud,Double Longitud) {
        this.idPedido = idPedido;
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.estado = estado;
        this.detalle = detalle;
        this.context = context;
        this.phone = phone;
        this.Latitud=Latitud;
        this.Longitud=Longitud;
        if (estado.equals("cancel"))
            this.estado = "Cancelado";
        if (estado.equals("delivered"))
            this.estado = "Completado";
        if (estado.equals("wait"))
            this.estado = "En espera";
        if (estado.equals("confirm"))
            this.estado = "Confirmado";
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(String precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Mpedido_detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Mpedido_detalle> detalle) {
        this.detalle = detalle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
