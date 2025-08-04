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
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.models.SolutionCard;

import java.util.ArrayList;
import java.util.List;

public class AllProductsServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private List<SolutionCard> solutionCardList;
    private List<SolutionCard> solutionCardListFiltered;

    public AllProductsServicesAdapter(Context context, List<SolutionCard> solutionCardList) {
        this.context = context;
        this.solutionCardList = new ArrayList<>(solutionCardList);
        this.solutionCardListFiltered = new ArrayList<>(solutionCardList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_service, parent, false);
        return new SolutionCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SolutionCard solutionCard = solutionCardListFiltered.get(position);
        SolutionCardViewHolder solutionCardHolder = (SolutionCardViewHolder) holder;

        solutionCardHolder.solutionCardTitle.setText(solutionCard.getName());
        solutionCardHolder.solutionCardDescription.setText(solutionCard.getDescription());

        Glide.with(context)
                .load(solutionCard.getCardImage())
                .into(solutionCardHolder.solutionCardImage);

        solutionCardHolder.itemView.setOnClickListener(v -> {
            Intent intent;

            if ("PRODUCT".equals(solutionCard.getSolutionType())) {
                intent = new Intent(context, ProductDetailsActivity.class);
            } else {
                intent = new Intent(context, ServiceDetailsActivity.class);
            }

            intent.putExtra("solutionId", solutionCard.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return solutionCardListFiltered.size();
    }

    public static class SolutionCardViewHolder extends RecyclerView.ViewHolder {
        TextView solutionCardTitle, solutionCardDescription;
        ImageView solutionCardImage;

        public SolutionCardViewHolder(@NonNull View itemView) {
            super(itemView);
            solutionCardTitle = itemView.findViewById(R.id.product_service_title);
            solutionCardDescription = itemView.findViewById(R.id.product_service_description);
            solutionCardImage = itemView.findViewById(R.id.product_service_image);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SolutionCard> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(solutionCardList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (SolutionCard solutionCard : solutionCardList) {
                        if (solutionCard.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(solutionCard);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                solutionCardListFiltered.clear();
                solutionCardListFiltered.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateSolutionCardList(List<SolutionCard> newSolutionCardList) {
        solutionCardList.clear();
        solutionCardList.addAll(newSolutionCardList);
        solutionCardListFiltered = new ArrayList<>(solutionCardList);
        notifyDataSetChanged();
    }
}