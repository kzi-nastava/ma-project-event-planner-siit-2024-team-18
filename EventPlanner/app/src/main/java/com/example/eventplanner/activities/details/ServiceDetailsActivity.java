package com.example.eventplanner.activities.details;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.fragments.ServiceReservationFragment;
import com.example.eventplanner.models.ServiceDetails;

public class ServiceDetailsActivity extends BaseActivity {

    private ServiceDetailsViewModel serviceDetailsViewModel;

    private ImageView serviceImage;
    private TextView serviceTitle, servicePrice, serviceDescription;
    private Button bookServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_service_details, findViewById(R.id.content_frame));

        serviceImage = findViewById(R.id.service_image);
        serviceTitle = findViewById(R.id.service_title);
        servicePrice = findViewById(R.id.service_price);
        serviceDescription = findViewById(R.id.service_description);
        bookServiceButton = findViewById(R.id.book_service_button);

        int serviceId = getIntent().getIntExtra("solutionId", -1);

        if (serviceId != -1) {
            setupViewModel();
            serviceDetailsViewModel.fetchServiceDetailsById(serviceId);

            bookServiceButton.setOnClickListener(v -> navigateToServiceReservation(serviceId));
        } else {
            Toast.makeText(this, "Invalid Service ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);

        serviceDetailsViewModel.getServiceDetails().observe(this, this::displayServiceDetails);

        serviceDetailsViewModel.isLoading().observe(this, isLoading -> {
        });

        serviceDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayServiceDetails(ServiceDetails service) {
        if (service != null) {
            serviceTitle.setText(service.getName());
            servicePrice.setText(String.format("Price: $%.2f", service.getPrice()));
            serviceDescription.setText(service.getDescription());

            if (service.getImages() != null && !service.getImages().isEmpty()) {
                Glide.with(this)
                        .load(service.getImages().get(0))
                        .placeholder(R.drawable.product_service_placeholder)
                        .error(R.drawable.product_service_placeholder)
                        .into(serviceImage);
            } else {
                serviceImage.setImageResource(R.drawable.product_service_placeholder);
            }
        }
    }

    private void navigateToServiceReservation(int serviceId) {
        ServiceReservationFragment fragment = new ServiceReservationFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("serviceId", serviceId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
