package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<Category> {
    private LinearLayout categoryCard;
    private ArrayList<Category> categories;
    private Activity activity;
    private CategoryCardViewModel categoriesViewModel;
    private TextView categoryName, categoryDescription;
    private FrameLayout frameEditCategory, frameDeleteCategory;
    public CategoryListAdapter(Activity context, CategoryCardViewModel categoriesViewModel) {
        super(context, R.layout.category_card);
        this.categories = new ArrayList<>();
        this.activity = context;
        this.categoriesViewModel = categoriesViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_card, parent, false);
        }

        Category category = getItem(position);

        initializeViews(convertView);
        populateFields(category);
        setupListeners(category, position);

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
        frameEditCategory = convertView.findViewById(R.id.editCategory);
        frameDeleteCategory = convertView.findViewById(R.id.deleteCategory);
    }

    private void populateFields(Category category) {
        categoryName.setText(category.getName());
        categoryDescription.setText(category.getDescription());
    }

    private void setupListeners(Category category, int position) {
        frameEditCategory.setOnClickListener(v -> {
//            Intent editIntent = new Intent(getContext(), EditCategoryActivity.class);
//            editIntent.putExtra("CategoryId", Category.getId());
//            editIntent.putExtra("position", position);
//            getContext().startActivity(editIntent);
        });

        frameDeleteCategory.setOnClickListener(v -> {
//            new AlertDialog.Builder(v.getContext())
//                    .setTitle("Confirm Deletion")
//                    .setMessage("Are you sure you want to delete this Category?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        CategoryListViewModel.deleteCategoryById(Category.getId());
//                        Toast.makeText(v.getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
//                    })
//                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                    .show();
        });
    }

    public void updateCategoriesList(List<Category> categories) {
        if (categories != null) {
            this.categories.clear();
            this.categories.addAll(categories);
            notifyDataSetChanged();
        }
    }
}
