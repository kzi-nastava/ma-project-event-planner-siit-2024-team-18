package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Filter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.eventplanner.R;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

public abstract class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.lightText));
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        signInButton = findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            Class<?> currentActivity = BaseActivity.this.getClass();

            if (id == R.id.nav_home) {
                if (currentActivity != HomeScreenActivity.class) {
                    Intent homeIntent = new Intent(BaseActivity.this, HomeScreenActivity.class);
                    startActivity(homeIntent);
                }
            } else if (id == R.id.nav_services) {
                if (currentActivity != AllServicesActivity.class) {
                    Intent servicesIntent = new Intent(BaseActivity.this, AllServicesActivity.class);
                    startActivity(servicesIntent);
                }
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
