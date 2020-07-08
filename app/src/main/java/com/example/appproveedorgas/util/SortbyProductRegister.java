package com.example.appproveedorgas.util;
import java.util.Comparator;
public class SortbyProductRegister implements Comparator<ProductRegister>{
    // Used for sorting in ascending order of
// roll number
    public int compare(ProductRegister a, ProductRegister b) {
        return a.getProduct().getDescription().compareTo(b.getProduct().getDescription());
    }
}

