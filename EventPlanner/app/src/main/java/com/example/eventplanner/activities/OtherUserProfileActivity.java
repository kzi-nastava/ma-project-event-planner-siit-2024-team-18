package com.example.eventplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.fragments.ReportSubmissionFragment;
import com.example.eventplanner.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherUserProfileActivity extends BaseActivity {
    private ImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileRole;
    private TextView profileAddress;
    private TextView profilePhone;
    private TextView profileCompany;
    private TextView profileCategories;
    private TextView profileEventTypes;
    private TextView profileDescription;
    private LinearLayout companyLayout;
    private LinearLayout descriptionLayout;
    private LinearLayout categoriesLayout;
    private LinearLayout eventTypesLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_other_user_profile, findViewById(R.id.content_frame));
        initializeViews();

        int userId = getIntent().getIntExtra("userId", -1);
        if (userId != -1) {
            fetchUserProfile(userId);
        } else {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        profileImage = findViewById(R.id.profile_image);
        profileEmail = findViewById(R.id.profile_email_value);
        profileName = findViewById(R.id.profile_name);
        profileRole = findViewById(R.id.profile_role_value);
        profileAddress = findViewById(R.id.profile_address_value);
        profilePhone = findViewById(R.id.profile_phone_value);
        profileCompany = findViewById(R.id.profile_company_value);
        profileCategories = findViewById(R.id.profile_categories_value);
        profileEventTypes = findViewById(R.id.profile_event_types_value);
        profileDescription = findViewById(R.id.profile_description_value);
        companyLayout = findViewById(R.id.company_layout);
        descriptionLayout = findViewById(R.id.description_layout);
        categoriesLayout = findViewById(R.id.categories_layout);
        eventTypesLayout = findViewById(R.id.event_types_layout);

        Button reportUserButton = findViewById(R.id.report_user_button);
        reportUserButton.setOnClickListener(v -> showReportConfirmationDialog());
    }

    private void navigateToReportFragment(int userId) {
        ReportSubmissionFragment fragment = new ReportSubmissionFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showReportConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Report User")
                .setMessage("Are you sure you want to report this user?")
                .setPositiveButton("Confirm", (dialog, which) -> navigateToReportFragment(user.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchUserProfile(int userId) {
        Call<User> call = ClientUtils.getUserService(this).getOtherUserProfile(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Log.d("OtherUserProfileActivity", "User fetched: " + user.toString());
                    updateUIWithUserData();
                } else {
                    Log.e("OtherUserProfileActivity", "Failed to fetch user: " + response.message());
                    Toast.makeText(OtherUserProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("OtherUserProfileActivity", "API call failed", t);
                Toast.makeText(OtherUserProfileActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithUserData() {
        Glide.with(this)
                .load(user.getImage())
                .placeholder(R.drawable.profile_photo_placeholder)
                .error(R.drawable.profile_photo_placeholder)
                .into(profileImage);
        profileEmail.setText(user.getEmail());
        profileRole.setText(user.getRole());
        profileName.setText(user.getFirstName() + " " + user.getLastName());
        profileAddress.setText(user.getAddress());
        profilePhone.setText(user.getPhone());

        if ("SERVICE_PRODUCT_PROVIDER".equals(user.getRole())) {
            companyLayout.setVisibility(View.VISIBLE);
            descriptionLayout.setVisibility(View.VISIBLE);
            categoriesLayout.setVisibility(View.VISIBLE);
            eventTypesLayout.setVisibility(View.VISIBLE);
            profileCompany.setText(user.getCompanyName());
            profileDescription.setText(user.getDescription());

            String categories = user.getCategories().toString();
            categories = categories.substring(1, categories.length() - 1);
            String eventTypes = user.getEventTypes().toString();
            eventTypes = eventTypes.substring(1, eventTypes.length() - 1);
            profileCategories.setText(categories);
            profileEventTypes.setText(eventTypes);
        }
    }
}
