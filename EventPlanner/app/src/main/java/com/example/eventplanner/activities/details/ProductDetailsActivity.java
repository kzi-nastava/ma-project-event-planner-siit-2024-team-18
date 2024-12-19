package com.example.eventplanner.activities.details;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.models.ProductDetails;

public class ProductDetailsActivity extends BaseActivity {

    private ProductDetailsViewModel productDetailsViewModel;

    private ImageView productImage;
    private TextView productTitle, productPrice, productDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_product_details, findViewById(R.id.content_frame));

        productImage = findViewById(R.id.product_image);
        productTitle = findViewById(R.id.product_title);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description);

        int productId = getIntent().getIntExtra("solutionId", -1);

        Log.d(String.valueOf(productId), "ProductDetailsActivity");

        if (productId != -1) {
            setupViewModel();
            productDetailsViewModel.fetchProductDetailsById(productId);
        } else {
            Toast.makeText(this, "Invalid Product ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);

        productDetailsViewModel.getProductDetails().observe(this, this::displayProductDetails);

        productDetailsViewModel.isLoading().observe(this, isLoading -> {
        });

        productDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetails(ProductDetails product) {
        if (product != null) {
            productTitle.setText(product.getName());
            productPrice.setText(String.format("Price: $%.2f", product.getPrice()));
            productDescription.setText(product.getDescription());

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                Glide.with(this)
                        .load(product.getImages().get(0))
                        .placeholder(R.drawable.product_service_placeholder)
                        .error(R.drawable.product_service_placeholder)
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.product_service_placeholder);
            }
        }
    }
}
