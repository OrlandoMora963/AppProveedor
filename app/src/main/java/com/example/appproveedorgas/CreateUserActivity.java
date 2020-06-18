package com.example.appproveedorgas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.appproveedorgas.util.UserPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class CreateUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        UserPagerAdapter userPagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_user);
        viewPager.setAdapter(userPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayour_user);
        tabLayout.setupWithViewPager(viewPager);
    }
}
