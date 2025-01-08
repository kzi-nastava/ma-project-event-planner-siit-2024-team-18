package com.example.eventplanner.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Method;

public class EditPricelistItemDialog<T> {
    private TextInputEditText pricelistItemPrice, pricelistItemDiscount;
    private TextView errorPricelistItemPrice, errorPricelistItemDiscount;
    private View dialogView;
    private T pricelistItem;

    public interface EditPricelistItemListener<T> {
        void onPricelistItemEdited(T pricelistItem);
    }

    public void show(Context context, T editPricelistItem, EditPricelistItemListener<T> listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_edit_pricelist_item, null);
        this.pricelistItem = editPricelistItem;

        initializeViews();
        populateFields();
        createDialog(context, listener);
    }

    private void initializeViews() {
        pricelistItemPrice = dialogView.findViewById(R.id.pricelistItemPrice);
        pricelistItemDiscount = dialogView.findViewById(R.id.pricelistItemDiscount);
        errorPricelistItemPrice = dialogView.findViewById(R.id.errorPricelistItemPrice);
        errorPricelistItemDiscount = dialogView.findViewById(R.id.errorPricelistItemDiscount);
    }

    private void populateFields() {
        try {
            Method getPriceMethod = pricelistItem.getClass().getMethod("getPrice");
            Method getDiscountMethod = pricelistItem.getClass().getMethod("getDiscount");

            pricelistItemPrice.setText(String.valueOf(getPriceMethod.invoke(pricelistItem)));
            pricelistItemDiscount.setText(String.valueOf(getDiscountMethod.invoke(pricelistItem)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDialog(Context context, EditPricelistItemListener<T> listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Edit Pricelist Item")
                .setPositiveButton("Edit", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean isValid = validateField(pricelistItemPrice) && validateField(pricelistItemDiscount);

                if (!isValid) {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Method setPriceMethod = pricelistItem.getClass().getMethod("setPrice", double.class);
                        Method setDiscountMethod = pricelistItem.getClass().getMethod("setDiscount", double.class);

                        setPriceMethod.invoke(pricelistItem, Double.parseDouble(pricelistItemPrice.getText().toString()));
                        setDiscountMethod.invoke(pricelistItem, Double.parseDouble(pricelistItemDiscount.getText().toString()));

                        listener.onPricelistItemEdited(pricelistItem);
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        dialog.show();
    }

    private boolean validateField(EditText field) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        return isValid;
    }
}
