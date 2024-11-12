package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class BaseActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (id == R.id.nav_home) {
            openHome();
        } else if (id == R.id.nav_chat) {
            openChat();
        } else if (id == R.id.nav_notifications) {
            openNotifications();
        } else if (id == R.id.nav_events) {
            openEvents();
        } else if (id == R.id.nav_services) {
            openServices();
        } else if (id == R.id.nav_products) {
            openProducts();
        } else if (id == R.id.nav_profile) {
            openProfile();
        } else if (id == R.id.nav_settings) {
            openSettings();
        } else if (id == R.id.nav_about) {
            openAbout();
        } else if (id == R.id.nav_sign_out) {
            signOut();
        } else {
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    private void openHome() {
    }

    private void openChat() {
    }

    private void openNotifications() {
    }

    private void openEvents() {
    }

    private void openServices() {
    }

    private void openProducts() {
    }

    private void openProfile() {
    }

    private void openSettings() {
    }

    private void openAbout() {
    }

    private void signOut() {
    }
}
