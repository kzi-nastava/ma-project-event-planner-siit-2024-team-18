package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.eventplanner.R;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder> {
    private Context context;
    private String[] imageUrls;

    public ImageSliderAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        String imageUrl = imageUrls[position];
        Glide.with(context)
                .load(imageUrl)
                .transform(new RoundedCorners(16))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.length;
    }

    public static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageSliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_slider_item);
        }
    }
}
