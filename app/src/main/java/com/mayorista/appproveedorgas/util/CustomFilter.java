package com.mayorista.appproveedorgas.util;

import android.widget.Filter;
import android.widget.ListAdapter;

public class CustomFilter extends Filter {
    private ListAdapter listAdapter;

    private CustomFilter(ListAdapter listAdapter) {
        super();
        this.listAdapter = listAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

    }
}
