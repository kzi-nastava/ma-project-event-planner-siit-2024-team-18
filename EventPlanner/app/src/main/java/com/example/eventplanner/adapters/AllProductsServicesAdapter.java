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

import com.example.eventplanner.activities.ProductServiceDetailsActivity;
import com.example.eventplanner.R;
import com.example.eventplanner.models.ProductService;

import java.util.ArrayList;
import java.util.List;

public class AllProductsServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final Context context;
    private final List<ProductService> productServiceList;
    private List<ProductService> productServiceListFiltered;

    public AllProductsServicesAdapter(Context context, List<ProductService> productServiceList) {
        this.context = context;
        this.productServiceList = new ArrayList<>(productServiceList);
        this.productServiceListFiltered = new ArrayList<>(productServiceList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_service, parent, false);
        return new ProductServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductService productService = productServiceListFiltered.get(position);
        ProductServiceViewHolder productServiceHolder = (ProductServiceViewHolder) holder;
        productServiceHolder.productServiceTitle.setText(productService.getTitle());
        productServiceHolder.productServiceDescription.setText(productService.getDescription());
        productServiceHolder.productServiceImage.setImageResource(productService.getImageResourceId());

        productServiceHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductServiceDetailsActivity.class);
            intent.putExtra("productServiceTitle", productService.getTitle());
            intent.putExtra("productServiceDescription", productService.getDescription());
            intent.putExtra("productServiceImage", productService.getImageResourceId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productServiceListFiltered.size();
    }

    public static class ProductServiceViewHolder extends RecyclerView.ViewHolder {
        TextView productServiceTitle, productServiceDescription;
        ImageView productServiceImage;

        public ProductServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            productServiceTitle = itemView.findViewById(R.id.product_service_title);
            productServiceDescription = itemView.findViewById(R.id.product_service_description);
            productServiceImage = itemView.findViewById(R.id.product_service_image);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ProductService> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(productServiceList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (ProductService productService : productServiceList) {
                        if (productService.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(productService);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productServiceListFiltered.clear();
                productServiceListFiltered.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void updateProductServiceList(List<ProductService> newProductServiceList) {
        productServiceList.clear();
        productServiceList.addAll(newProductServiceList);
        productServiceListFiltered = new ArrayList<>(productServiceList);
        notifyDataSetChanged();
    }
}
