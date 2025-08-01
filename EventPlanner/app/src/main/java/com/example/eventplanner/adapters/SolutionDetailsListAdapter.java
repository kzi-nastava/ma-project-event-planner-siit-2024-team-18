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
import androidx.lifecycle.LifecycleOwner;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.models.ProductDetails;
import com.example.eventplanner.models.SolutionDetails;
import com.example.eventplanner.viewmodels.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class SolutionDetailsListAdapter extends ArrayAdapter<SolutionDetails> {
    private final LifecycleOwner lifecycleOwner;
    private LinearLayout solutionDetailsCard;
    private ArrayList<SolutionDetails> solutions;
    private BudgetViewModel budgetViewModel;
    private TextView solutionName, solutionDescription, solutionPrice;
    public SolutionDetailsListAdapter(Activity context, BudgetViewModel budgetViewModel, LifecycleOwner lifecycleOwner) {
        super(context, R.layout.category_card);
        this.solutions = new ArrayList<>();
        this.budgetViewModel = budgetViewModel;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.solution_details_card, parent, false);
        }

        SolutionDetails solution = getItem(position);

        initializeViews(convertView);
        populateFields(solution);
        setupListeners(solution);

        return convertView;
    }

    @Override
    public int getCount() {
        return solutions.size();
    }

    @Nullable
    @Override
    public SolutionDetails getItem(int position) {
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

    private void populateFields(SolutionDetails solution) {
        solutionName.setText(solution.getName());
        solutionDescription.setText(solution.getDescription());
        int discountedPrice = (int) (solution.getPrice() * (1 - (solution.getDiscount() / 100.0)));
        solutionPrice.setText(String.format("%d$", discountedPrice));
    }

    private void setupListeners(SolutionDetails solution) {
        solutionDetailsCard.setOnClickListener(v -> {
            budgetViewModel.isProduct().removeObservers(lifecycleOwner);

            budgetViewModel.isProduct().observe(lifecycleOwner, isProduct -> {
                if (isProduct != null) {
                    Intent intent = new Intent(getContext(),
                            isProduct ? ProductDetailsActivity.class : ServiceDetailsActivity.class);
                    intent.putExtra("solutionId", solution.getId());
                    getContext().startActivity(intent);

                    if (getContext() instanceof FragmentActivity) {
                        ((FragmentActivity) getContext()).getSupportFragmentManager().popBackStack();
                    }
                }
            });

            budgetViewModel.fetchIsProduct(solution.getId());
        });
    }


    public void updateSolutionDetailsList(List<SolutionDetails> allSolutions) {
        if (allSolutions != null) {
            this.solutions.clear();
            this.solutions.addAll(allSolutions);
            notifyDataSetChanged();
        }
    }
}
