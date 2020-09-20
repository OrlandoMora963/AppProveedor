package com.mayorista.appproveedorgas.pojo;

public class Product {
    private int ID;
    private double medida;
    private double precio_unitario;
    private String imagen;
    private String categoria;
    private String marca;
    private String unidad_medida;

    public Product(int ID, double medida, double precio_unitario, String imagen, String categoria, String marca, String unidad_medida) {
        this.ID = ID;
        this.medida = medida;
        this.precio_unitario = precio_unitario;
        this.imagen = imagen;
        this.categoria = categoria;
        this.marca = marca;
        this.unidad_medida = unidad_medida;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getMedida() {
        return medida;
    }

    public void setMedida(double medida) {
        this.medida = medida;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }
}
