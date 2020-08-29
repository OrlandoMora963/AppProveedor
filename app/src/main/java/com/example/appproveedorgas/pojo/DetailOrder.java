package com.example.appproveedorgas.pojo;

public class DetailOrder {
    private String Producto;
    private int Cantidad;
    private double PrecioU;
    private double SubTotal;

    public String getProducto() {
        return Producto;
    }

    public void setProducto(String producto) {
        Producto = producto;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public double getPrecioU() {
        return PrecioU;
    }

    public void setPrecioU(double precioU) {
        PrecioU = precioU;
    }

    public double getSubTotal() {
        return SubTotal;
    }

    public void setSubTotal(double subTotal) {
        SubTotal = subTotal;
    }
}
