package com.example.appproveedorgas.util;
import java.util.Comparator;
public class Sortbydescription implements Comparator<Product>{
// Used for sorting in ascending order of
// roll number
public int compare(Product a, Product b) {
        return a.getDescription().compareTo(b.getDescription());
        }
}
