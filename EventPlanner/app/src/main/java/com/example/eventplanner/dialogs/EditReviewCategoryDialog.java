package com.example.eventplanner.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class EditReviewCategoryDialog {
    private static TextInputEditText categoryName, categoryDescription;
    private static TextView errorCategoryName, errorCategoryDescription;
    private static View dialogView;
    private static Category originalCategory;
    private static Spinner predefinedCategory;
    private static CategoryCardViewModel categoriesViewModel;
    private static Context context;
    private static List<Category> categoryList = new ArrayList<>();

    public interface EditCategoryListener {
        void onCategoryEdited(Category category);
    }

    public static void show(Context activity, Category category, CategoryCardViewModel getCategoriesViewModel, EditCategoryListener listener) {
        context = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        dialogView = inflater.inflate(R.layout.dialog_edit_review_category, null);
        originalCategory = category;
        categoriesViewModel = getCategoriesViewModel;

        initializeViews();
        populateFields();
        setupSpinnerListener();
        createDialog(activity, listener);
    }

    private static void initializeViews() {
        predefinedCategory = dialogView.findViewById(R.id.predefinedCategory);
        categoryName = dialogView.findViewById(R.id.categoryName);
        categoryDescription = dialogView.findViewById(R.id.categoryDescription);
        errorCategoryName = dialogView.findViewById(R.id.errorCategoryName);
        errorCategoryDescription = dialogView.findViewById(R.id.errorCategoryDescription);
    }

    private static void populateFields() {
        categoriesViewModel.getCategories().observeForever(new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null) {
                    categoryList.clear();

                    categoryList.addAll(categories);

                    if (!isCategoryInList(originalCategory)) {
                        categoryList.add(0, originalCategory);
                    }

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, getCategoryNames(categoryList));
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    predefinedCategory.setAdapter(categoryAdapter);

                    int position = getCategoryPosition(originalCategory);
                    predefinedCategory.setSelection(position);
                }
            }
        });

        categoryName.setText(originalCategory.getName());
        categoryDescription.setText(originalCategory.getDescription());
    }

    private static boolean isCategoryInList(Category category) {
        for (Category cat : categoryList) {
            if (cat.getId() == category.getId()) {
                return true;
            }
        }
        return false;
    }

    private static void setupSpinnerListener() {
        predefinedCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = categoryList.get(position);

                if (selectedCategory.getId() == originalCategory.getId()) {
                    categoryName.setEnabled(true);
                    categoryDescription.setEnabled(true);
                } else {
                    categoryName.setEnabled(false);
                    categoryDescription.setEnabled(false);
                }

                categoryName.setText(selectedCategory.getName());
                categoryDescription.setText(selectedCategory.getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private static List<String> getCategoryNames(List<Category> categories) {
        List<String> names = new ArrayList<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

    private static int getCategoryPosition(Category category) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == category.getId()) {
                return i;
            }
        }
        return 0;
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
                boolean isValid = validateFields();

                if (isValid) {
                    originalCategory.setName(categoryName.getText().toString());
                    originalCategory.setDescription(categoryDescription.getText().toString());
                    listener.onCategoryEdited(originalCategory);
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private static boolean validateFields() {
        boolean isNameValid = validateField(categoryName, errorCategoryName);
        boolean isDescriptionValid = validateField(categoryDescription, errorCategoryDescription);

        return isNameValid && isDescriptionValid;
    }

    private static boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }
}
