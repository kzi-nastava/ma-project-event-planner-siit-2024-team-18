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
import com.example.eventplanner.dialogs.EditPricelistItemDialog;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.PricelistViewModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PricelistListAdapter<T> extends ArrayAdapter<T> {
    private FrameLayout frameEditPricelistItem;
    private LinearLayout pricelistItemCard;
    private ArrayList<T> pricelistItems;
    private PricelistViewModel pricelistViewModel;
    private TextView pricelistItemName, pricelistItemPrice, pricelistItemDiscount, pricelistItemId, pricelistItemDiscountedPrice;

    public PricelistListAdapter(Activity context, PricelistViewModel pricelistViewModel) {
        super(context, R.layout.pricelist_item_card);
        this.pricelistItems = new ArrayList<T>();
        this.pricelistViewModel = pricelistViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pricelist_item_card, parent, false);
        }

        T pricelistItem = getItem(position);

        initializeViews(convertView);
        populateFields(pricelistItem);
        setupListeners(pricelistItem);

        return convertView;
    }

    @Override
    public int getCount() {
        return pricelistItems.size();
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return pricelistItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        pricelistItemCard = convertView.findViewById(R.id.pricelistItemCard);
        pricelistItemId = convertView.findViewById(R.id.pricelistItemId);
        pricelistItemName = convertView.findViewById(R.id.pricelistItemName);
        pricelistItemPrice = convertView.findViewById(R.id.originalPrice);
        pricelistItemDiscount = convertView.findViewById(R.id.discount);
        pricelistItemDiscountedPrice = convertView.findViewById(R.id.price);
        frameEditPricelistItem = convertView.findViewById(R.id.editButtonLayout);
    }

    private void populateFields(T pricelistItem) {
        if (pricelistItem instanceof Service) {
            Service service = (Service) pricelistItem;
            pricelistItemId.setText(String.valueOf(service.getId()));
            pricelistItemName.setText(service.getName());
            pricelistItemPrice.setText(String.valueOf(service.getPrice()));
            pricelistItemDiscount.setText(String.valueOf(service.getDiscount()));
            pricelistItemDiscountedPrice.setText(String.valueOf(calculateDiscountedPrice(service.getPrice(), service.getDiscount())));
        } else if (pricelistItem instanceof Product) {
            Product product = (Product) pricelistItem;
            pricelistItemId.setText(String.valueOf(product.getId()));
            pricelistItemName.setText(product.getName());
            pricelistItemPrice.setText(String.valueOf(product.getPrice()));
            pricelistItemDiscount.setText(String.valueOf(product.getDiscount()));
            pricelistItemDiscountedPrice.setText(String.valueOf(calculateDiscountedPrice(product.getPrice(), product.getDiscount())));
        }
    }

    private double calculateDiscountedPrice(double originalPrice, double discount) {
        return originalPrice - (originalPrice * discount / 100);
    }

    private void setupListeners(T pricelistItem) {
        frameEditPricelistItem.setOnClickListener(v -> {
            EditPricelistItemDialog<T> dialog = new EditPricelistItemDialog<>();
            dialog.show(getContext(), pricelistItem, updatedPricelistItem -> {
                int id = -1;
                try {
                    Method getIdMethod = pricelistItem.getClass().getMethod("getId");
                    id = (int) getIdMethod.invoke(pricelistItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (pricelistItem instanceof Service) {
                    pricelistViewModel.editPricelistItemService(id, (Service) updatedPricelistItem);
                } else if (pricelistItem instanceof Product) {
                    pricelistViewModel.editPricelistItemProduct(id, (Product) updatedPricelistItem);
                }
            });
        });
    }

    public void updatePricelistItemList(List<T> allPricelistItems) {
        if (allPricelistItems != null) {
            this.pricelistItems.clear();
            this.pricelistItems.addAll(allPricelistItems);
            notifyDataSetChanged();
        }
    }
}
