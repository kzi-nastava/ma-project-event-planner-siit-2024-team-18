package com.example.eventplanner.activities.details;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.adapters.ImageSliderAdapter;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.ServiceDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetailsActivity extends BaseActivity {
    private ServiceDetailsViewModel serviceDetailsViewModel;
    private ViewPager2 serviceImageSlider;
    private LinearLayout sliderDotsContainer;
    private TextView serviceTitle, serviceDiscountedPrice, serviceDescription, serviceAvailability, serviceOriginalPrice;
    private Button favoriteButton;
    private List<ImageView> dots;
    boolean isFavorite = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_service_details, findViewById(R.id.content_frame));

        serviceImageSlider = findViewById(R.id.service_image_slider);
        sliderDotsContainer = findViewById(R.id.slider_dots_container);
        serviceTitle = findViewById(R.id.service_name);
        serviceDiscountedPrice = findViewById(R.id.service_discounted_price);
        serviceDescription = findViewById(R.id.service_description);
        serviceAvailability = findViewById(R.id.service_availability);
        favoriteButton = findViewById(R.id.favorite_button);
        serviceOriginalPrice = findViewById(R.id.service_original_price);

        int serviceId = getIntent().getIntExtra("solutionId", -1);

        if (serviceId != -1) {
            setupViewModel();
            serviceDetailsViewModel.fetchServiceById(serviceId);
        } else {
            Toast.makeText(this, "Invalid Service ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);

        serviceDetailsViewModel.getService().observe(this, this::displayServiceDetails);

        serviceDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayServiceDetails(Service service) {
        if (service != null) {
            serviceTitle.setText(service.getName());
            serviceDescription.setText(service.getDescription());

            if (service.getDiscount() > 0) {
                double discountedPrice = service.getPrice() * (1 - service.getDiscount() / 100.0);

                serviceOriginalPrice.setText(String.format("$%.2f", service.getPrice()));
                serviceOriginalPrice.setPaintFlags(serviceOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                serviceDiscountedPrice.setText(String.format("$%.2f", discountedPrice));
                serviceDiscountedPrice.setVisibility(View.VISIBLE);
            } else {
                serviceOriginalPrice.setText(String.format("$%.2f", service.getPrice()));
                serviceDiscountedPrice.setVisibility(View.GONE);
            }

            if (isFavorite) {
                favoriteButton.setText("Remove from Favorites");
            } else {
                favoriteButton.setText("Mark as Favorite");
            }

            favoriteButton.setVisibility(View.VISIBLE);
            favoriteButton.setOnClickListener(v -> toggleFavorite());

            if (service.getImages() != null && service.getImages().length != 0) {
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
        }
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        favoriteButton.setText(isFavorite ? "Mark as Favorite" : "Remove from Favorites");
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
}
