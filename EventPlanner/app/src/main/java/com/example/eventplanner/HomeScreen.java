package com.example.eventplanner;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

public class HomeScreen extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);

        getLayoutInflater().inflate(R.layout.activity_home_screen, findViewById(R.id.content_frame));

        Fragment top5EventsFragment = new Top5EventsFragment();
        Fragment allEventsFragment = new AllEventsFragment();
        Fragment top5ProductsServicesFragment = new Top5ProductsServicesFragment();
        Fragment allProductsServicesFragment = new AllProductsServicesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.top_5_events_container, top5EventsFragment)
                .replace(R.id.all_events_container, allEventsFragment)
                .replace(R.id.top_5_products_services_container, top5ProductsServicesFragment)
                .replace(R.id.all_products_services_container, allProductsServicesFragment)
                .commit();
    }
}
