package com.mayorista.appproveedorgas.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.mayorista.appproveedorgas.CreateUserFragment;
import com.mayorista.appproveedorgas.ListUserFragment;

public class UserPagerAdapter extends FragmentPagerAdapter {


    public UserPagerAdapter(FragmentManager fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ListUserFragment();
                break;
            case 1:
                fragment = new CreateUserFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Repartidores";
            case 1:
                return "Registrar Repartidor";
        }

        return null;
    }
}
