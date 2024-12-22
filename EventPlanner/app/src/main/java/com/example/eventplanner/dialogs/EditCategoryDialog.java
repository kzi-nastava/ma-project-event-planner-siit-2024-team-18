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
import com.example.eventplanner.models.Category;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class EditCategoryDialog {
    private static TextInputEditText categoryName, categoryDescription;
    private static TextView errorCategoryName, errorCategoryDescription;
    private static View dialogView;
    private static Category category;

    public interface EditCategoryListener {
        void onCategoryEdited(Category category);
    }

    public static void show(Context context, Category editCategory, EditCategoryListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_edit_category, null);
        category = editCategory;

        initializeViews();
        populateFields();
        createDialog(context, listener);
    }

    private static void initializeViews() {
        categoryName = dialogView.findViewById(R.id.categoryName);
        categoryDescription = dialogView.findViewById(R.id.categoryDescription);
        errorCategoryName = dialogView.findViewById(R.id.errorCategoryName);
        errorCategoryDescription = dialogView.findViewById(R.id.errorCategoryDescription);
    }

    private static void populateFields() {
        categoryName.setText(category.getName());
        categoryDescription.setText(category.getDescription());
    }

    private static void createDialog(Context context, EditCategoryListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Edit Category")
                .setPositiveButton("Edit", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean isValid = true;

                if (!validateField(categoryName, errorCategoryName)) {
                    isValid = false;
                }

                if (!validateField(categoryDescription, errorCategoryDescription)) {
                    isValid = false;
                }

                if (!isValid) {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                } else {
                    category.setName(categoryName.getText().toString());
                    category.setDescription(categoryDescription.getText().toString());
                    listener.onCategoryEdited(category);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private static boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }
}
