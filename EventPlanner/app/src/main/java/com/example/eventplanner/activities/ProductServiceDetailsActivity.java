package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventplanner.R;

public class ProductServiceDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_product_service_details, findViewById(R.id.content_frame));

        ImageView productServiceImage = findViewById(R.id.product_service_image);
        TextView productServiceTitle = findViewById(R.id.product_service_title);
        TextView productServiceDescription = findViewById(R.id.product_service_description);

        Intent intent = getIntent();
        String title = intent.getStringExtra("productServiceTitle");
        String description = intent.getStringExtra("productServiceDescription");
        int imageResourceId = intent.getIntExtra("productServiceImage", R.drawable.product_service_placeholder);

        productServiceTitle.setText(title);
        productServiceDescription.setText(description);
        productServiceImage.setImageResource(imageResourceId);
    }
}
