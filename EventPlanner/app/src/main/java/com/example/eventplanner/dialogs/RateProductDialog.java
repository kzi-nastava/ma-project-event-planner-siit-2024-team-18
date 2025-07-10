package com.example.eventplanner.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class RateProductDialog {
    public interface RateProductListener {
        void onRateProduct(int value, String comment);
    }

    public static void show(Context context, Product product, RateProductListener listener, androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rate_product, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);
        Button submitButton = dialogView.findViewById(R.id.submitRatingButton);
        TextView price = dialogView.findViewById(R.id.successPrice);
        price.setText(product.getPrice() + "€");

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        submitButton.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = commentInput.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            listener.onRateProduct(rating, comment);
            dialog.dismiss();
        });

        dialog.show();
    }
}
