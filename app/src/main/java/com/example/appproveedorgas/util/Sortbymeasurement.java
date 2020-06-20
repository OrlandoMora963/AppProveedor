package com.example.appproveedorgas.util;

import java.util.Comparator;

public class Sortbymeasurement implements Comparator<Product>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Product a, Product b)
    {
        return (int) (a.getMeasurement() - b.getMeasurement());
    }
}
