package com.example.eventplanner.activities.details;

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
import com.example.eventplanner.adapters.ImageSliderAdapter;
import com.example.eventplanner.dialogs.BuyProductDialog;
import com.example.eventplanner.fragments.ContentUnavailableFragment;
import com.example.eventplanner.fragments.SolutionContentUnavailableFragment;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.viewmodels.ProductDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends BaseActivity {
    private Product product;
    private ProductDetailsViewModel productDetailsViewModel;
    private ViewPager2 productImageSlider;
    private LinearLayout sliderDotsContainer;
    private TextView productName, productDiscountedPrice, productDescription, productAvailability, productOriginalPrice, productNumberOfReviews, productCategory, productEventTypes, productLocation;
    private ImageView favoriteButton, commentsButton;
    private Button visitProviderButton, buyProductButton;
    private List<ImageView> dots;
    private RatingBar productRating;
    boolean isFavorite = false;
    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_product_details, findViewById(R.id.content_frame));

        productId = getIntent().getIntExtra("solutionId", -1);
        initializeViews();
        setupViewModel();
        setupListeners();
    }

    private void initializeViews() {
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        productDetailsViewModel.setContext(this);

        favoriteButton = findViewById(R.id.favorite_button);
        productImageSlider = findViewById(R.id.product_image_slider);
        sliderDotsContainer = findViewById(R.id.slider_dots_container);
        productName = findViewById(R.id.product_name);
        productAvailability = findViewById(R.id.product_availability);
        productRating = findViewById(R.id.product_rating);
        productNumberOfReviews = findViewById(R.id.product_reviews);
        commentsButton = findViewById(R.id.comments);
        productDescription = findViewById(R.id.product_description);
        productCategory = findViewById(R.id.product_category);
        productEventTypes = findViewById(R.id.product_eventTypes);
        productLocation = findViewById(R.id.product_location);
        productOriginalPrice = findViewById(R.id.product_original_price);
        productDiscountedPrice = findViewById(R.id.product_discounted_price);
        visitProviderButton = findViewById(R.id.visit_provider);
        buyProductButton = findViewById(R.id.buy_product);
    }

    private void setupViewModel() {
        productDetailsViewModel.fetchProductDetailsById(productId);

        productDetailsViewModel.getProductDetails().observe(this, this::displayProductDetails);

        productDetailsViewModel.isLoading().observe(this, isLoading -> {
        });

        productDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.startsWith("Network error: End of input at line 1 column 1 path $")) {
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

    private void displayProductDetails(Product product) {
        if (product != null) {
            this.product = product;

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.liked);
            } else {
                favoriteButton.setImageResource(R.drawable.heart);
            }

            if (product.getImages() != null && product.getImages().length != 0) {
                setupImageSlider();
            }

            productName.setText(product.getName());
            if (product.isAvailable()) {
                productAvailability.setText("(Available)");
            } else {
                productAvailability.setText("(Unavailable)");
            }

            // TODO: update logic
            productRating.setRating(4);
            productNumberOfReviews.setText("(42 Reviews)");

            SpannableString spannableDescription = new SpannableString(String.format("Description: %s", product.getDescription()));
            spannableDescription.setSpan(new StyleSpan(Typeface.BOLD), 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productDescription.setText(spannableDescription);

            SpannableString spannableCategory = new SpannableString(String.format("Category: %s", product.getCategory()));
            spannableCategory.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productCategory.setText(spannableCategory);

            SpannableString spannableEventTypes = new SpannableString(String.format("Event Types: %s", String.join(", ", product.getEventTypes())));
            spannableEventTypes.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productEventTypes.setText(spannableEventTypes);

            SpannableString spannableLocation = new SpannableString(String.format("Location: %s", product.getLocation()));
            spannableLocation.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            productLocation.setText(spannableLocation);

            if (product.getDiscount() > 0) {
                setupDiscount();
            } else {
                productOriginalPrice.setText(String.format("$%.2f", product.getPrice()));
                productDiscountedPrice.setVisibility(View.GONE);
            }
        }
    }

    private void setupListeners() {
        favoriteButton.setOnClickListener(v -> toggleFavorite());
        commentsButton.setOnClickListener(v -> openComments());
        visitProviderButton.setOnClickListener(v -> visitProvider());
        buyProductButton.setOnClickListener(v -> buyProduct());
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        favoriteButton.setImageResource(isFavorite ? R.drawable.liked : R.drawable.heart);
    }

    private void openComments() {}

    private void visitProvider() {}

    private void buyProduct() {
        BuyProductDialog.show(this, product, (productId, eventId) -> {
            productDetailsViewModel.buyProduct(productId, eventId);
            finish();
        }, this);
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
        String[] images = product.getImages();
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        productImageSlider.setAdapter(adapter);

        setupPaginationDots(images.length);

        productImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePaginationDots(position);
            }
        });
    }
    
    private void setupDiscount() {
        double discountedPrice = product.getPrice() * (1 - product.getDiscount() / 100.0);

        productOriginalPrice.setText(String.format("$%.2f", product.getPrice()));
        productOriginalPrice.setPaintFlags(productOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (discountedPrice <= 0) {
            productDiscountedPrice.setText("FREE");
        } else {
            productDiscountedPrice.setText(String.format("$%.2f", discountedPrice));
        }
        productDiscountedPrice.setVisibility(View.VISIBLE);
    }
}
