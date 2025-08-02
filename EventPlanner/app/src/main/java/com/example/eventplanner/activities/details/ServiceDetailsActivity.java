package com.example.eventplanner.activities.details;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.activities.CommentsActivity;
import com.example.eventplanner.activities.CommunicationActivity;
import com.example.eventplanner.fragments.ServiceReservationFragment;
import com.example.eventplanner.adapters.ImageSliderAdapter;
import com.example.eventplanner.fragments.SolutionContentUnavailableFragment;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.LoginViewModel;
import com.example.eventplanner.viewmodels.ServiceDetailsViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceDetailsActivity extends BaseActivity {
    private Service service;
    private ServiceDetailsViewModel serviceDetailsViewModel;
    private LoginViewModel loginViewModel;
    private UserViewModel userViewModel;
    private ViewPager2 serviceImageSlider;
    private LinearLayout sliderDotsContainer;
    private TextView serviceName, serviceDiscountedPrice, serviceDescription, serviceAvailability, serviceOriginalPrice, serviceNumberOfReviews, serviceSpecifics, serviceCategory, serviceEventTypes, serviceDuration, serviceEngagement, serviceReservationDeadline, serviceCancellationDeadline, serviceWorkingHours, serviceLocation;
    private ImageView favoriteButton, commentsButton;
    private Button visitProviderButton, bookServiceButton;
    private List<ImageView> dots;
    private RatingBar serviceRating;
    boolean isFavorite = false;
    private int serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_service_details, findViewById(R.id.content_frame));

        serviceId = getIntent().getIntExtra("solutionId", -1);
        initializeViews();
        setupViewModel();
        setupListeners();
    }

    private void initializeViews() {
        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        serviceDetailsViewModel.setContext(this);
        loginViewModel = LoginViewModel.getInstance(getApplicationContext());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setContext(this);

        favoriteButton = findViewById(R.id.favorite_button);
        serviceImageSlider = findViewById(R.id.service_image_slider);
        sliderDotsContainer = findViewById(R.id.slider_dots_container);
        serviceName = findViewById(R.id.service_name);
        serviceAvailability = findViewById(R.id.service_availability);
        serviceRating = findViewById(R.id.service_rating);
        serviceNumberOfReviews = findViewById(R.id.service_reviews);
        commentsButton = findViewById(R.id.comments);
        serviceDescription = findViewById(R.id.service_description);
        serviceSpecifics = findViewById(R.id.service_specifics);
        serviceCategory = findViewById(R.id.service_category);
        serviceEventTypes = findViewById(R.id.service_eventTypes);
        serviceLocation = findViewById(R.id.service_location);
        serviceDuration = findViewById(R.id.service_duration);
        serviceEngagement = findViewById(R.id.service_engagement);
        serviceReservationDeadline = findViewById(R.id.service_reservation_deadline);
        serviceCancellationDeadline = findViewById(R.id.service_cancellation_deadline);
        serviceWorkingHours = findViewById(R.id.service_working_hours);
        serviceOriginalPrice = findViewById(R.id.service_original_price);
        serviceDiscountedPrice = findViewById(R.id.service_discounted_price);
        visitProviderButton = findViewById(R.id.visit_provider);
        bookServiceButton = findViewById(R.id.book_service);
    }

    private void setupViewModel() {
        serviceDetailsViewModel.fetchServiceById(serviceId);
        serviceDetailsViewModel.fetchServiceRating(serviceId);

        serviceDetailsViewModel.getService().observe(this, this::displayServiceDetails);

        serviceDetailsViewModel.getServiceGrade().observe(this, grade -> {
            if (grade != null) {
                serviceRating.setRating(grade.getValue());
            }
        });

        serviceDetailsViewModel.getServiceReviews().observe(this, reviews -> {
            if (reviews != null) {
                serviceNumberOfReviews.setText("(" + reviews + " Reviews)");
            }
        });

        serviceDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.startsWith("End of input at line 1 column 1 path $")) {
                    navigateToSolutionUnavailableContentFragment();
                } else {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToSolutionUnavailableContentFragment() {
        SolutionContentUnavailableFragment fragment = new SolutionContentUnavailableFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void displayServiceDetails(Service service) {
        if (service != null) {
            this.service = service;

            userViewModel.isLiked().observe(this, liked -> {
                if (Boolean.TRUE.equals(liked)) {
                    isFavorite = true;
                    favoriteButton.setImageResource(R.drawable.liked);
                } else {
                    isFavorite = false;
                    favoriteButton.setImageResource(R.drawable.heart);
                }
            });
            userViewModel.fetchIsLiked(serviceId);

            if (service.getImages() != null && service.getImages().length != 0) {
                setupImageSlider();
            }

            serviceName.setText(service.getName());
            if (service.isAvailable()) {
                serviceAvailability.setText("(Available)");
            } else {
                serviceAvailability.setText("(Unavailable)");
            }

            SpannableString spannableDescription = new SpannableString(String.format("Description: %s", service.getDescription()));
            spannableDescription.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceDescription.setText(spannableDescription);

            SpannableString spannableSpecifics = new SpannableString(String.format("Specifics: %s", service.getSpecifics()));
            spannableSpecifics.setSpan(new StyleSpan(Typeface.BOLD), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceSpecifics.setText(spannableSpecifics);

            SpannableString spannableCategory = new SpannableString(String.format("Category: %s", service.getCategory()));
            spannableCategory.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceCategory.setText(spannableCategory);

            SpannableString spannableEventTypes = new SpannableString(String.format("Event Types: %s", String.join(", ", service.getEventTypes())));
            spannableEventTypes.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceEventTypes.setText(spannableEventTypes);

            SpannableString spannableLocation = new SpannableString(String.format("Location: %s", service.getLocation()));
            spannableLocation.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceLocation.setText(spannableLocation);

            if (service.getMaxEngagement() <= 1) {
                SpannableString spannableDuration = new SpannableString(String.format("Duration: %d minutes", service.getDuration()));
                spannableDuration.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                serviceDuration.setText(spannableDuration);
                serviceDuration.setVisibility(View.VISIBLE);
            } else {
                SpannableString spannableEngagement = new SpannableString(String.format("Engagement: %d - %d hours", service.getMinEngagement(), service.getMaxEngagement()));
                spannableEngagement.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                serviceEngagement.setText(spannableEngagement);
                serviceEngagement.setVisibility(View.VISIBLE);
            }
            SpannableString spannableReservationDeadline = new SpannableString(String.format("Reservation Deadline: %d days", service.getReservationDeadline()));
            spannableReservationDeadline.setSpan(new StyleSpan(Typeface.BOLD), 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceReservationDeadline.setText(spannableReservationDeadline);

            SpannableString spannableCancellationDeadline = new SpannableString(String.format("Cancellation Deadline: %d days", service.getCancellationDeadline()));
            spannableCancellationDeadline.setSpan(new StyleSpan(Typeface.BOLD), 0, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceCancellationDeadline.setText(spannableCancellationDeadline);

            String workingHoursStart = service.getWorkingHoursStart().substring(0, service.getWorkingHoursStart().lastIndexOf(":"));
            String workingHoursEnd = service.getWorkingHoursEnd().substring(0, service.getWorkingHoursEnd().lastIndexOf(":"));
            SpannableString spannableWorkingHours = new SpannableString(String.format("Working Hours: %s - %s", workingHoursStart, workingHoursEnd));
            spannableWorkingHours.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            serviceWorkingHours.setText(spannableWorkingHours);

            if (service.getDiscount() > 0) {
                setupDiscount();
            } else {
                serviceOriginalPrice.setText(String.format("$%.2f", service.getPrice()));
                serviceDiscountedPrice.setVisibility(View.GONE);
            }
        }
    }

    private void setupListeners() {
        favoriteButton.setOnClickListener(v -> toggleFavorite());
        commentsButton.setOnClickListener(v -> openComments());
        visitProviderButton.setOnClickListener(v -> visitProvider());
        if(!Objects.equals(loginViewModel.getRole(), "EVENT_ORGANIZER")){
            bookServiceButton.setVisibility(View.GONE);
        }
        bookServiceButton.setOnClickListener(v -> navigateToServiceReservation(this.service.getId()));
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        favoriteButton.setImageResource(isFavorite ? R.drawable.liked : R.drawable.heart);

        if (isFavorite) {
            userViewModel.addToFavouritesSolution(serviceId);
        } else {
            userViewModel.removeFromFavouritesSolution(serviceId);
        }
    }

    private void openComments() {
        Intent intent = new Intent(ServiceDetailsActivity.this, CommentsActivity.class);
        intent.putExtra("solutionId", serviceId);
        startActivity(intent);
    }

    private void visitProvider() {
        serviceDetailsViewModel.getSuccessChat().observe(this, success -> {
            if (success) {
                Intent intent = new Intent(ServiceDetailsActivity.this, CommunicationActivity.class);
                startActivity(intent);
            }
        });

        serviceDetailsViewModel.fetchChats(serviceId);
    }

    private void setupPaginationDots(int size) {
        dots = new ArrayList<>();
        sliderDotsContainer.removeAllViews();

        for (int i = 0; i < size; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.inactive_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            sliderDotsContainer.addView(dot);
            dots.add(dot);
        }

        if (!dots.isEmpty()) {
            dots.get(0).setImageResource(R.drawable.active_dot);
        }
    }

    private void updatePaginationDots(int activePosition) {
        for (int i = 0; i < dots.size(); i++) {
            if (i == activePosition) {
                dots.get(i).setImageResource(R.drawable.active_dot);
            } else {
                dots.get(i).setImageResource(R.drawable.inactive_dot);
            }
        }
    }

    private void setupImageSlider() {
        String[] images = service.getImages();
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        serviceImageSlider.setAdapter(adapter);

        setupPaginationDots(images.length);

        serviceImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePaginationDots(position);
            }
        });
    }

    private void setupDiscount() {
        double discountedPrice = service.getPrice() * (1 - service.getDiscount() / 100.0);

        serviceOriginalPrice.setText(String.format("$%.2f", service.getPrice()));
        serviceOriginalPrice.setPaintFlags(serviceOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (discountedPrice <= 0) {
            serviceDiscountedPrice.setText("FREE");
        } else {
            serviceDiscountedPrice.setText(String.format("$%.2f", discountedPrice));
        }
        serviceDiscountedPrice.setVisibility(View.VISIBLE);
    }

    private void navigateToServiceReservation(int serviceId) {
        ServiceReservationFragment fragment = new ServiceReservationFragment(serviceDetailsViewModel, service);

        Bundle bundle = new Bundle();
        bundle.putInt("serviceId", serviceId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
