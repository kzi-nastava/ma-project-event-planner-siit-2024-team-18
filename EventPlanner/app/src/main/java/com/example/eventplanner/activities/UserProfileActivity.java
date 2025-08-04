package com.example.eventplanner.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.bumptech.glide.Glide;
import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.fragments.FavouriteEventsListFragment;
import com.example.eventplanner.fragments.FavouriteSolutionsListFragment;
import com.example.eventplanner.models.CalendarEvent;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.AllEventsViewModel;
import com.example.eventplanner.viewmodels.LoginViewModel;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseActivity {
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
    private FavouriteEventsListFragment favouriteEventsListFragment;
    private FavouriteSolutionsListFragment favouriteSolutionsListFragment;
    private WeekView mWeekView;
    private AllEventsViewModel allEventsViewModel;
    private Button calendarButton;
    private Button favouriteEventsButton;
    private Button favouriteSolutionsButton;
    private FrameLayout favouriteEventsLayout;
    private FrameLayout favouriteSolutionsLayout;
    private TextView calendarTextView;
    private Button deactivateButton;
    private Button editProfileButton;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_user_profile, findViewById(R.id.content_frame));
        allEventsViewModel = new ViewModelProvider(this).get(AllEventsViewModel.class);
        allEventsViewModel.setContext(this);
        allEventsViewModel.fetchAcceptedEvents();
        loginViewModel = LoginViewModel.getInstance(getApplicationContext());
        InitializeViews();
        getLoggedUser();

        calendarButton.setOnClickListener(v -> {
            calendarButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A29BFE")));
            favouriteEventsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            favouriteSolutionsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            calendarTextView.setVisibility(View.VISIBLE);
            mWeekView.setVisibility(View.VISIBLE);
            favouriteEventsLayout.setVisibility(View.GONE);
            favouriteSolutionsLayout.setVisibility(View.GONE);
        });

        favouriteEventsButton.setOnClickListener(v -> {
            calendarButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            favouriteEventsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A29BFE")));
            favouriteSolutionsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            calendarTextView.setVisibility(View.GONE);
            mWeekView.setVisibility(View.GONE);
            favouriteEventsLayout.setVisibility(View.VISIBLE);
            favouriteSolutionsLayout.setVisibility(View.GONE);
        });

        favouriteSolutionsButton.setOnClickListener(v -> {
            calendarButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            favouriteEventsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#667A8A")));
            favouriteSolutionsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A29BFE")));
            calendarTextView.setVisibility(View.GONE);
            mWeekView.setVisibility(View.GONE);
            favouriteEventsLayout.setVisibility(View.GONE);
            favouriteSolutionsLayout.setVisibility(View.VISIBLE);
        });
    }

    private void InitializeViews() {
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
        calendarButton = findViewById(R.id.menu_calendar_button);
        favouriteEventsButton = findViewById(R.id.menu_favorite_events_button);
        favouriteSolutionsButton = findViewById(R.id.menu_favorite_solutions_button);
        favouriteEventsLayout = findViewById(R.id.listViewFavouriteEvents);
        favouriteSolutionsLayout = findViewById(R.id.listViewFavouriteSolutions);
        calendarTextView = findViewById(R.id.calendarTextView);
        deactivateButton = findViewById(R.id.deactivate_profile_button);
        editProfileButton = findViewById(R.id.edit_profile_button);
        favouriteEventsListFragment = FavouriteEventsListFragment.newInstance();
        FragmentTransition.to(favouriteEventsListFragment, this, false, R.id.listViewFavouriteEvents);
        favouriteSolutionsListFragment = FavouriteSolutionsListFragment.newInstance();
        FragmentTransition.to(favouriteSolutionsListFragment, this, false, R.id.listViewFavouriteSolutions);
        mWeekView = findViewById(R.id.weekView);

        allEventsViewModel.getAcceptedEvents().observe(this, acceptedEvents -> {
            if (acceptedEvents != null) {
                List<WeekViewEvent<Void>> events = new ArrayList<>();

                for (CalendarEvent acceptedEvent : acceptedEvents) {
                    LocalDateTime startLocalDateTime = acceptedEvent.getStart();
                    ZonedDateTime startZonedDateTime = startLocalDateTime.atZone(ZoneId.systemDefault());
                    Date startDate = Date.from(startZonedDateTime.toInstant());
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);

                    LocalDateTime endLocalDateTime = acceptedEvent.getEnd();
                    ZonedDateTime endZonedDateTime = endLocalDateTime.atZone(ZoneId.systemDefault());
                    Date endDate = Date.from(endZonedDateTime.toInstant());
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(endDate);

                    WeekViewEvent<Void> event = new WeekViewEvent.Builder<Void>()
                            .setId(acceptedEvent.getId())
                            .setTitle(acceptedEvent.getTitle())
                            .setStartTime(startCalendar)
                            .setEndTime(endCalendar)
                            .setLocation(acceptedEvent.getLocation())
                            .setStyle(new WeekViewEvent.Style.Builder()
                                    .setBackgroundColor(getRandomColor())
                                    .build()
                            )
                            .setAllDay(false)
                            .build();

                    events.add(event);
                }

                mWeekView.submit(events);
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        deactivateButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deactivation")
                    .setMessage("Are you sure you want to deactivate your profile?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        loginViewModel.deactivateProfile();
                        Intent homeIntent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(homeIntent);
                        finish();
                        Toast.makeText(v.getContext(), "Profile successfuly deactivated.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private void getLoggedUser() {
        Call<User> call = ClientUtils.getUserService(this).getUserProfile();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Log.d("UserProfileActivity", "User fetched: " + user.toString());

                    updateUIWithUserData();
                } else {
                    Log.e("UserProfileActivity", "Failed to fetch user: " + response.message());
                    Toast.makeText(UserProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserProfileActivity", "API call failed", t);
                Toast.makeText(UserProfileActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
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
        if (user.getRole().equals("SERVICE_PRODUCT_PROVIDER")) {
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