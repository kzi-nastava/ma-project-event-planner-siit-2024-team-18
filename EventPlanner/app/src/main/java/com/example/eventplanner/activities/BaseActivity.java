package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.viewmodels.LoginViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Button signInButton;
    private Button signUpButton;
    private LoginViewModel viewModel;
    private ImageView profileImageView;
    private TextView userFullNameTextView;
    private TextView userEmailTextView;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        viewModel = LoginViewModel.getInstance(getApplicationContext());

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
        signUpButton = findViewById(R.id.button_sign_up);
        viewModel.getLoggedInStatus().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                signInButton.setVisibility(View.GONE);
                signUpButton.setVisibility(View.GONE);
                if(Objects.equals(viewModel.getRole(), "AUTHENTICATED_USER")){
                    signUpButton.setVisibility(View.VISIBLE);
                }
                updateProfileInfo();
            } else {
                signInButton.setVisibility(View.VISIBLE);
                signUpButton.setVisibility(View.GONE);
                hideProfileInfo();
            }
        });

        signInButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        signUpButton.setOnClickListener(v -> {
            Intent fastRegisterIntent = new Intent(BaseActivity.this, FastRegisterActivity.class);
            startActivity(fastRegisterIntent);
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        profileImageView = headerView.findViewById(R.id.profile_image);
        userFullNameTextView = headerView.findViewById(R.id.user_name);
        userEmailTextView = headerView.findViewById(R.id.user_email);

        viewModel.getLoggedInStatus().observe(this, isLoggedIn -> {
            updateMenuVisibility(navigationView.getMenu(), isLoggedIn);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            Class<?> currentActivity = BaseActivity.this.getClass();

            if (id == R.id.nav_home) {
                if (currentActivity != HomeScreenActivity.class) {
                    Intent homeIntent = new Intent(BaseActivity.this, HomeScreenActivity.class);
                    startActivity(homeIntent);
                }
            } else if (id == R.id.nav_chat) {
                if (currentActivity != CommunicationActivity.class) {
                    Intent communicationIntent = new Intent(BaseActivity.this, CommunicationActivity.class);
                    startActivity(communicationIntent);
                }
            } else if (id == R.id.nav_services) {
                if (currentActivity != AllServicesActivity.class) {
                    Intent servicesIntent = new Intent(BaseActivity.this, AllServicesActivity.class);
                    startActivity(servicesIntent);
                }
            } else if (id == R.id.nav_products) {
                if (currentActivity != AllProductsActivity.class) {
                    Intent productsIntent = new Intent(BaseActivity.this, AllProductsActivity.class);
                    startActivity(productsIntent);
                }
            } else if (id == R.id.nav_categories) {
                if (currentActivity != CategoriesActivity.class) {
                    Intent categoriesIntent = new Intent(BaseActivity.this, CategoriesActivity.class);
                    startActivity(categoriesIntent);
                }
            } else if (id == R.id.nav_budget) {
                if (currentActivity != BudgetActivity.class) {
                    Intent budgetIntent = new Intent(BaseActivity.this, BudgetActivity.class);
                    startActivity(budgetIntent);
                }
            } else if (id == R.id.nav_event_types) {
                if (currentActivity != EventTypesActivity.class) {
                    Intent eventTypesIntent = new Intent(BaseActivity.this, EventTypesActivity.class);
                    startActivity(eventTypesIntent);
                }
            } else if (id == R.id.nav_pricelist) {
                if (currentActivity != PricelistActivity.class) {
                    Intent pricelistIntent = new Intent(BaseActivity.this, PricelistActivity.class);
                    startActivity(pricelistIntent);
                }
            } else if (id == R.id.nav_profile) {
                if (currentActivity != UserProfileActivity.class) {
                    Intent profileIntent = new Intent(BaseActivity.this, UserProfileActivity.class);
                    startActivity(profileIntent);
                }
            } else if (id == R.id.nav_reports) {
                if (currentActivity != ReportRequestsActivity.class) {
                    Intent reportRequestsIntent = new Intent(BaseActivity.this, ReportRequestsActivity.class);
                    startActivity(reportRequestsIntent);
                }
            } else if (id == R.id.nav_comments) {
                if (currentActivity != CommentRequestsActivity.class) {
                    Intent commentRequestsIntent = new Intent(BaseActivity.this, CommentRequestsActivity.class);
                    startActivity(commentRequestsIntent);
                }
            } else if (id == R.id.nav_sign_out) {
                viewModel.signOut();
                Intent homeIntent = new Intent(BaseActivity.this, HomeScreenActivity.class);
                startActivity(homeIntent);
                finish();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void updateMenuVisibility(Menu menu, boolean isLoggedIn) {
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_chat).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_notifications).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_events).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "EVENT_ORGANIZER"));
        menu.findItem(R.id.nav_services).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "SERVICE_PRODUCT_PROVIDER"));
        menu.findItem(R.id.nav_products).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "SERVICE_PRODUCT_PROVIDER"));
        menu.findItem(R.id.nav_categories).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "ADMIN"));
        menu.findItem(R.id.nav_event_types).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "ADMIN"));
        menu.findItem(R.id.nav_budget).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "EVENT_ORGANIZER"));
        menu.findItem(R.id.nav_pricelist).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "SERVICE_PRODUCT_PROVIDER"));
        menu.findItem(R.id.nav_profile).setVisible(isLoggedIn);
        menu.findItem(R.id.nav_reports).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "ADMIN"));
        menu.findItem(R.id.nav_comments).setVisible(isLoggedIn && Objects.equals(viewModel.getRole(), "ADMIN"));
        menu.findItem(R.id.nav_sign_out).setVisible(isLoggedIn);
    }

    private void updateProfileInfo() {
        profileImageView.setVisibility(View.VISIBLE);
        userFullNameTextView.setVisibility(View.VISIBLE);
        userEmailTextView.setVisibility(View.VISIBLE);
        userEmailTextView.setText(viewModel.getUserEmail());
        userFullNameTextView.setText(viewModel.getUserFirstName() + " " + viewModel.getUserLastName());
        Glide.with(this)
                .load(viewModel.getUserImage())
                .placeholder(R.drawable.profile_photo_placeholder)
                .error(R.drawable.profile_photo_placeholder)
                .into(profileImageView);
    }

    private void hideProfileInfo() {
        profileImageView.setVisibility(View.GONE);
        userFullNameTextView.setVisibility(View.GONE);
        userEmailTextView.setVisibility(View.GONE);
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
