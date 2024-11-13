package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.eventplanner.R;
import com.example.eventplanner.models.ProductService;
import com.example.eventplanner.activities.ProductServiceDetailsActivity;
import java.util.List;

public class Top5ProductsServicesAdapter extends RecyclerView.Adapter<Top5ProductsServicesAdapter.Top5ProductsServicesViewHolder> {
    private final Context context;
    private final List<ProductService> productServiceList;
    private RecyclerView recyclerView;
    private SnapHelper snapHelper;

    public Top5ProductsServicesAdapter(Context context, List<ProductService> productServiceList) {
        this.context = context;
        this.productServiceList = productServiceList;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setSnapHelper(SnapHelper snapHelper) {
        this.snapHelper = snapHelper;
    }

    @NonNull
    @Override
    public Top5ProductsServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_product_service, parent, false);
        return new Top5ProductsServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Top5ProductsServicesViewHolder holder, int position) {
        ProductService productService = productServiceList.get(position);
        holder.title.setText(productService.getTitle());
        holder.description.setText(productService.getDescription());
        holder.image.setImageResource(productService.getImageResourceId());

        if (recyclerView != null && recyclerView.getLayoutManager() != null && snapHelper != null) {
            View currentSnapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
            if (currentSnapView != null) {
                int currentCenterPosition = recyclerView.getLayoutManager().getPosition(currentSnapView);
                float scaleFactor = (position == currentCenterPosition) ? 1.0f : 0.8f;
                holder.itemView.setScaleX(scaleFactor);
                holder.itemView.setScaleY(scaleFactor);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (recyclerView != null && recyclerView.getLayoutManager() != null) {
                View currentSnapView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                int currentCenterPosition = currentSnapView != null ? recyclerView.getLayoutManager().getPosition(currentSnapView) : -1;

                if (position == currentCenterPosition) {
                    Intent intent = new Intent(context, ProductServiceDetailsActivity.class);
                    intent.putExtra("productServiceTitle", productService.getTitle());
                    intent.putExtra("productServiceDescription", productService.getDescription());
                    intent.putExtra("productServiceImage", productService.getImageResourceId());
                    context.startActivity(intent);
                } else {
                    recyclerView.smoothScrollToPosition(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productServiceList.size();
    }

    static class Top5ProductsServicesViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, description;

        public Top5ProductsServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.carousel_item_image);
            title = itemView.findViewById(R.id.carousel_item_title);
            description = itemView.findViewById(R.id.carousel_item_description);
        }
    }
}
