package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.EditReviewCategoryDialog;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReviewCategoryListAdapter extends ArrayAdapter<Category> {
    private LinearLayout categoryCard;
    private ArrayList<Category> categories;
    private CategoryCardViewModel categoriesViewModel;
    private TextView categoryName, categoryDescription;
    private FrameLayout frameApproveCategory, frameDeleteCategory;
    public ReviewCategoryListAdapter(Activity context, CategoryCardViewModel categoriesViewModel) {
        super(context, R.layout.category_card);
        this.categories = new ArrayList<>();
        this.categoriesViewModel = categoriesViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_category_card, parent, false);
        }

        Category category = getItem(position);

        initializeViews(convertView);
        populateFields(category);
        setupListeners(category);

        return convertView;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        categoryCard = convertView.findViewById(R.id.categoryCard);
        categoryName = convertView.findViewById(R.id.categoryName);
        categoryDescription = convertView.findViewById(R.id.categoryDescription);
        frameApproveCategory = convertView.findViewById(R.id.approveCategory);
        frameDeleteCategory = convertView.findViewById(R.id.deleteCategory);
    }

    private void populateFields(Category category) {
        categoryName.setText(category.getName());
        categoryDescription.setText(category.getDescription());
    }

    private void setupListeners(Category category) {
        categoryCard.setOnClickListener(v -> {
            EditReviewCategoryDialog.show(getContext(), category, categoriesViewModel, updatedCategory -> {
                notifyDataSetChanged();
                categoriesViewModel.editCategory(updatedCategory.getId(), updatedCategory);
            });
        });

        frameApproveCategory.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Approval")
                    .setMessage("Are you sure you want to approve this category?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        categoriesViewModel.approveCategoryById(category.getId());
                        Toast.makeText(v.getContext(), "Category approved", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
            });

        frameDeleteCategory.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    categoriesViewModel.deleteCategoryById(category.getId());
                    Toast.makeText(v.getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
            });
    }

    public void updateCategoriesList(List<Category> allCategories) {
        if (allCategories != null) {
            this.categories.clear();
            this.categories.addAll(allCategories);
            notifyDataSetChanged();
        }
    }
}
