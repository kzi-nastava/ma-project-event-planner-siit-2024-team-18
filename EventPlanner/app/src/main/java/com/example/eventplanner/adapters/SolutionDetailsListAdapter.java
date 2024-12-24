package com.example.eventplanner.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.models.ProductDetails;
import com.example.eventplanner.viewmodels.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class SolutionDetailsListAdapter extends ArrayAdapter<ProductDetails> {
    private LinearLayout solutionDetailsCard;
    private ArrayList<ProductDetails> solutions;
    private BudgetViewModel budgetViewModel;
    private TextView solutionName, solutionDescription, solutionPrice;
    public SolutionDetailsListAdapter(Activity context, BudgetViewModel budgetViewModel) {
        super(context, R.layout.category_card);
        this.solutions = new ArrayList<>();
        this.budgetViewModel = budgetViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.solution_details_card, parent, false);
        }

        ProductDetails product = getItem(position);

        initializeViews(convertView);
        populateFields(product);
        setupListeners(product);

        return convertView;
    }

    @Override
    public int getCount() {
        return solutions.size();
    }

    @Nullable
    @Override
    public ProductDetails getItem(int position) {
        return solutions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        solutionDetailsCard = convertView.findViewById(R.id.solutionDetailsCard);
        solutionName = convertView.findViewById(R.id.solutionName);
        solutionDescription = convertView.findViewById(R.id.solutionDescription);
        solutionPrice = convertView.findViewById(R.id.solutionPrice);
    }

    private void populateFields(ProductDetails product) {
        solutionName.setText(product.getName());
        solutionDescription.setText(product.getDescription());
        int discountedPrice = (int) (product.getPrice() * (1 - (product.getDiscount() / 100.0)));
        solutionPrice.setText(String.format("%d$", discountedPrice));
    }

    private void setupListeners(ProductDetails product) {
        solutionDetailsCard.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
            intent.putExtra("solutionId", product.getId());
            getContext().startActivity(intent);

            if (getContext() instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) getContext();
                activity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void updateSolutionDetailsList(List<ProductDetails> allSolutions) {
        if (allSolutions != null) {
            this.solutions.clear();
            this.solutions.addAll(allSolutions);
            notifyDataSetChanged();
        }
    }
}
