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
import com.example.eventplanner.dialogs.EditBudgetItemDialog;
import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.viewmodels.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class BudgetItemListAdapter extends ArrayAdapter<BudgetItem> {
    private LinearLayout budgetItemCard;
    private ArrayList<BudgetItem> budgetItems;
    private BudgetViewModel budgetViewModel;
    private TextView budgetItemCategory, budgetItemMaxAmount;
    private FrameLayout frameEditBudgetItem, frameDeleteBudgetItem;
    private int eventId;

    public BudgetItemListAdapter(Activity context, BudgetViewModel budgetViewModel, int eventId) {
        super(context, R.layout.budget_item_card);
        this.budgetItems = new ArrayList<>();
        this.budgetViewModel = budgetViewModel;
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.budget_item_card, parent, false);
        }

        BudgetItem budgetItem = getItem(position);

        initializeViews(convertView);
        populateFields(budgetItem);
        setupListeners(budgetItem);

        return convertView;
    }

    @Override
    public int getCount() {
        return budgetItems.size();
    }

    @Nullable
    @Override
    public BudgetItem getItem(int position) {
        return budgetItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        budgetItemCard = convertView.findViewById(R.id.budgetItemCard);
        budgetItemCategory = convertView.findViewById(R.id.budgetItemCategory);
        budgetItemMaxAmount = convertView.findViewById(R.id.budgetItemMaxAmount);
        frameEditBudgetItem = convertView.findViewById(R.id.editBudgetItem);
        frameDeleteBudgetItem = convertView.findViewById(R.id.deleteBudgetItem);
    }

    private void populateFields(BudgetItem budgetItem) {
        budgetItemCategory.setText(budgetItem.getCategory());
        budgetItemMaxAmount.setText(Integer.toString(budgetItem.getMaxAmount()));
    }

    private void setupListeners(BudgetItem budgetItem) {
        frameEditBudgetItem.setOnClickListener(v -> {
            EditBudgetItemDialog.show(getContext(), budgetItem, updatedBudgetItem -> {
                budgetViewModel.editBudgetItem(eventId, updatedBudgetItem.getId(), updatedBudgetItem);
            });
        });

        frameDeleteBudgetItem.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this budget item?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        budgetViewModel.deleteBudgetItemById(eventId, budgetItem.getId());
                        Toast.makeText(v.getContext(), "Budget Item deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    public void updateBudgetItemList(List<BudgetItem> allBudgetItems) {
        if (allBudgetItems != null) {
            this.budgetItems.clear();
            this.budgetItems.addAll(allBudgetItems);
            notifyDataSetChanged();
        }
    }
}
