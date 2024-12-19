package com.example.eventplanner.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.homepage.Top5EventsFragment;
import com.example.eventplanner.fragments.homepage.Top5SolutionsFragment;
import com.example.eventplanner.fragments.homepage.AllEventsFragment;
import com.example.eventplanner.fragments.homepage.AllProductsServicesFragment;

public class HomeScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_home_screen, findViewById(R.id.content_frame));

        Fragment top5EventsFragment = new Top5EventsFragment();
        Fragment allEventsFragment = new AllEventsFragment();
        Fragment top5ProductsServicesFragment = new Top5SolutionsFragment();
        Fragment allProductsServicesFragment = new AllProductsServicesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.top_5_events_container, top5EventsFragment)
                .replace(R.id.all_events_container, allEventsFragment)
                .replace(R.id.top_5_products_services_container, top5ProductsServicesFragment)
                .replace(R.id.all_products_services_container, allProductsServicesFragment)
                .commit();
    }
}
