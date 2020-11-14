package com.mayorista.appproveedorgas.util;

import com.mayorista.appproveedorgas.pojo.product_marcas;

import java.util.Comparator;

public class SortMarkbyName  implements Comparator<product_marcas> {
    public int compare(product_marcas a, product_marcas b) {
        return a.getName().compareTo(b.getName());
    }
}
