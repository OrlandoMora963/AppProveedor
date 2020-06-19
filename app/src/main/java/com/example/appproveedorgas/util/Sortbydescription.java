package com.example.appproveedorgas.util;
import java.util.Comparator;
public class Sortbydescription implements Comparator<ProductDetail>{
// Used for sorting in ascending order of
// roll number
public int compare(ProductDetail a,ProductDetail b) {
        return a.getDescription().compareTo(b.getDescription());
        }
}
