package com.example.appproveedorgas.util;

import java.util.Comparator;

public class Sortbymeasurement implements Comparator<ProductDetail>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(ProductDetail a, ProductDetail b)
    {
        return (int) (a.getMeasurement() - b.getMeasurement());
    }
}
