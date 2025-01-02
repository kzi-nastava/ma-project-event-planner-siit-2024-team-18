package com.example.eventplanner.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddBudgetItemDialog {
    private static Spinner budgetItemCategory;
    private static TextInputEditText budgetItemMaxAmount;
    private static TextView errorBudgetItemCategory, errorBudgetItemMaxAmount;
    private static View dialogView;
    private static CategoryCardViewModel categoriesViewModel;
    private static Event event;
    public interface AddBudgetItemListener {
        void onBudgetItemCreated(String category, String maxAmount);
    }

    public static void show(Event eventAdd, Context context, AddBudgetItemDialog.AddBudgetItemListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_add_budget_item, null);
        event = eventAdd;

        initializeViews();
        populateFields();
        createDialog(context, listener);
    }

    private static void initializeViews() {
        budgetItemCategory = dialogView.findViewById(R.id.budgetItemCategory);
        budgetItemMaxAmount = dialogView.findViewById(R.id.budgetItemMaxAmount);
        errorBudgetItemCategory = dialogView.findViewById(R.id.errorBudgetItemCategory);
        errorBudgetItemMaxAmount = dialogView.findViewById(R.id.errorBudgetItemMaxAmount);
    }

    private static void populateFields() {
        categoriesViewModel.getCategoriesForEvent().observeForever(new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            dialogView.getContext(),
                            android.R.layout.simple_spinner_item,
                            categories.stream().map(Category::getName).toArray(String[]::new)
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    budgetItemCategory.setAdapter(adapter);
                }
            }
        });

        categoriesViewModel.fetchCategoriesForEvent(event.getId());
    }


    private static void createDialog(Context context, AddBudgetItemDialog.AddBudgetItemListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Create Budget Item")
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean isValid = true;

                if (!validateField(budgetItemMaxAmount, errorBudgetItemMaxAmount)) {
                    isValid = false;
                }

                if (!isValid) {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onBudgetItemCreated(budgetItemCategory.getSelectedItem().toString().trim(), budgetItemMaxAmount.getText().toString().trim());
                    Toast.makeText(context, "Budget Item created: " + budgetItemCategory.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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
