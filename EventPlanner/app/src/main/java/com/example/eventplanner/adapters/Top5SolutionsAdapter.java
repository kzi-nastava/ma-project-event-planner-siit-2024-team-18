package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.models.SolutionCard;

import java.util.ArrayList;
import java.util.List;

public class Top5SolutionsAdapter extends RecyclerView.Adapter<Top5SolutionsAdapter.Top5SolutionsViewHolder> implements Filterable {
    private final Context context;
    private List<SolutionCard> solutionList;
    private List<SolutionCard> solutionListFiltered;

    public Top5SolutionsAdapter(Context context, List<SolutionCard> solutionList) {
        this.context = context;
        this.solutionList = new ArrayList<>(solutionList);
        this.solutionListFiltered = new ArrayList<>(solutionList);
    }

    @NonNull
    @Override
    public Top5SolutionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_product_service, parent, false);
        return new Top5SolutionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Top5SolutionsViewHolder holder, int position) {
        SolutionCard solution = solutionListFiltered.get(position);
        holder.title.setText(solution.getName());
        holder.description.setText(solution.getDescription());

        Glide.with(context)
                .load(solution.getCardImage())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent;

            if ("PRODUCT".equals(solution.getSolutionType())) {
                intent = new Intent(context, ProductDetailsActivity.class);
            } else {
                intent = new Intent(context, ServiceDetailsActivity.class);
            }

            intent.putExtra("solutionId", solution.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return solutionListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SolutionCard> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(solutionList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (SolutionCard solution : solutionList) {
                        if (solution.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(solution);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                solutionListFiltered.clear();
                solutionListFiltered.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateSolutionList(List<SolutionCard> solutions) {
        if (solutions != null) {
            this.solutionList.clear();
            this.solutionList.addAll(solutions);
            this.solutionListFiltered.clear();
            this.solutionListFiltered.addAll(solutions);
            notifyDataSetChanged();
        }
    }

    static class Top5SolutionsViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        public Top5SolutionsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.carousel_item_image);
            title = itemView.findViewById(R.id.carousel_item_title);
            description = itemView.findViewById(R.id.carousel_item_description);
        }
    }
}
