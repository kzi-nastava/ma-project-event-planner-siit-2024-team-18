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

import com.example.eventplanner.R;
import com.example.eventplanner.models.BudgetItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class EditBudgetItemDialog {
    private static Spinner budgetItemCategory;
    private static TextInputEditText budgetItemMaxAmount;
    private static TextView errorBudgetItemCategory, errorBudgetItemMaxAmount;
    private static View dialogView;
    private static BudgetItem budgetItem;

    public interface EditBudgetItemListener {
        void onBudgetItemEdited(BudgetItem budgetItem);
    }

    public static void show(Context context, BudgetItem editBudgetItem, EditBudgetItemDialog.EditBudgetItemListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_edit_budget_item, null);
        budgetItem = editBudgetItem;

        initializeViews(context);
        populateFields();
        createDialog(context, listener);
    }

    private static void initializeViews(Context context) {
        budgetItemCategory = dialogView.findViewById(R.id.budgetItemCategory);
        budgetItemMaxAmount = dialogView.findViewById(R.id.budgetItemMaxAmount);
        errorBudgetItemCategory = dialogView.findViewById(R.id.errorBudgetItemCategory);
        errorBudgetItemMaxAmount = dialogView.findViewById(R.id.errorBudgetItemMaxAmount);
    }

    private static void populateFields() {
        String budgetItemCategoryName = budgetItem.getCategory();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                dialogView.getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{budgetItemCategoryName}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetItemCategory.setAdapter(adapter);
        budgetItemCategory.setEnabled(false);

        budgetItemMaxAmount.setText(Integer.toString(budgetItem.getMaxAmount()));
    }

    private static void createDialog(Context context, EditBudgetItemDialog.EditBudgetItemListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Edit Budget Item")
                .setPositiveButton("Edit", null)
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
                    budgetItem.setCategory(budgetItemCategory.getSelectedItem().toString());
                    budgetItem.setMaxAmount(Integer.parseInt(budgetItemMaxAmount.getText().toString()));
                    listener.onBudgetItemEdited(budgetItem);
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
