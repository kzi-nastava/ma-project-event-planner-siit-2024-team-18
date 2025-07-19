package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.models.BudgetItem;
import com.example.eventplanner.viewmodels.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class BudgetDetailsListAdapter extends RecyclerView.Adapter<BudgetDetailsListAdapter.ViewHolder> {
    private List<BudgetItem> budgetItems = new ArrayList<>();
    private Activity context;

    public BudgetDetailsListAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BudgetDetailsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.budget_details_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetDetailsListAdapter.ViewHolder holder, int position) {
        BudgetItem budgetItem = budgetItems.get(position);
        holder.budgetItemCategory.setText(budgetItem.getCategory());
        holder.budgetItemMaxAmount.setText(String.format("%s$", budgetItem.getMaxAmount()));
    }

    @Override
    public int getItemCount() {
        return budgetItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView budgetItemCategory, budgetItemMaxAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            budgetItemCategory = itemView.findViewById(R.id.budgetItemCategory);
            budgetItemMaxAmount = itemView.findViewById(R.id.budgetItemMaxAmount);
        }
    }

    public void updateBudgetItemList(List<BudgetItem> allBudgetItems) {
        if (allBudgetItems != null) {
            this.budgetItems.clear();
            this.budgetItems.addAll(allBudgetItems);
            notifyDataSetChanged();
        }
    }
}
