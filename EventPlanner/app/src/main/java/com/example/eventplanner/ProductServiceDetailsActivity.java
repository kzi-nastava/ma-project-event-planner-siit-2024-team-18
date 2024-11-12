package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProductServiceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_service_details);

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
