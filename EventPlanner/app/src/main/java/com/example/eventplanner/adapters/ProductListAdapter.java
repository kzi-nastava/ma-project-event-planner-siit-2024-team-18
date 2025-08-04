package com.example.eventplanner.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.EditProductActivity;
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.viewmodels.ProductListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends ArrayAdapter<Product> {
    private LinearLayout productCard;
    private ArrayList<Product> products;
    private Activity activity;
    private ProductListViewModel productListViewModel;
    private TextView productName, productDescription;
    private ImageView imgProduct;
    private FrameLayout frameEditProduct, frameDeleteProduct;
    public ProductListAdapter(Activity context, ProductListViewModel productListViewModel) {
        super(context, R.layout.product_card);
        this.products = new ArrayList<>();
        this.activity = context;
        this.productListViewModel = productListViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_card, parent, false);
        }

        Product product = getItem(position);

        initializeViews(convertView);
        populateFields(product);
        setupListeners(product);

        return convertView;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Nullable
    @Override
    public Product getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        productCard = convertView.findViewById(R.id.productCard);
        productName = convertView.findViewById(R.id.txtProductName);
        productDescription = convertView.findViewById(R.id.txtProductDescription);
        imgProduct = convertView.findViewById(R.id.imgProduct);
        frameEditProduct = convertView.findViewById(R.id.editProduct);
        frameDeleteProduct = convertView.findViewById(R.id.deleteProduct);
    }

    private void populateFields(Product product) {
        productName.setText(product.getName());
        productDescription.setText(product.getDescription());

        String imageUrl = product.getImages()[0];
        Glide.with(activity)
                .load(imageUrl)
                .into(imgProduct);
    }

    private void setupListeners(Product product) {
        frameEditProduct.setOnClickListener(v -> {
            Intent editIntent = new Intent(getContext(), EditProductActivity.class);
            editIntent.putExtra("productId", product.getId());
            getContext().startActivity(editIntent);
        });

        frameDeleteProduct.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        productListViewModel.deleteProductById(product.getId());
                        Toast.makeText(v.getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        productCard.setOnClickListener(v -> {
            Intent detailIntent = new Intent(activity, ProductDetailsActivity.class);
            detailIntent.putExtra("solutionId", product.getId());
            activity.startActivity(detailIntent);
        });
    }

    public void updateProductsList(List<Product> products) {
        if (products != null) {
            this.products.clear();
            this.products.addAll(products);
            notifyDataSetChanged();
        }
    }
}
