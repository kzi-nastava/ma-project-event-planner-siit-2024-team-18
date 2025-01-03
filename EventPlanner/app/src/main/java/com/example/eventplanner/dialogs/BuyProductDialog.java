package com.example.eventplanner.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.viewmodels.EventDetailsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class BuyProductDialog {
    private static Spinner eventSpinner;
    private static View dialogView;
    private static Product product;
    private static EventDetailsViewModel eventViewModel;

    public interface BuyProductListener {
        void onBuyProduct(int productId, int eventId);
    }

    public static void show(Context context, Product buyProduct, BuyProductDialog.BuyProductListener listener, androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_buy_product, null);
        product = buyProduct;

        initializeViews(context);
        initializeViewModels(context, lifecycleOwner);
        createDialog(context, listener);
    }

    private static void initializeViews(Context context) {
        eventSpinner = dialogView.findViewById(R.id.buyProduct);
    }

    private static void initializeViewModels(Context context, androidx.lifecycle.LifecycleOwner lifecycleOwner) {
        eventViewModel = new ViewModelProvider((androidx.fragment.app.FragmentActivity) context).get(EventDetailsViewModel.class);
        eventViewModel.setContext(context);

        eventViewModel.getEventsByCreator().observe(lifecycleOwner, events -> {
            populateFields(events);
        });

        eventViewModel.getErrorMessage().observe(lifecycleOwner, error -> {
            if (error != null) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
        });

        eventViewModel.fetchEventsByCreator();
    }


    private static void populateFields(ArrayList<EventCard> events) {
        ArrayAdapter<EventCard> adapter = new ArrayAdapter<>(dialogView.getContext(), android.R.layout.simple_spinner_item, events);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(adapter);
    }

    private static void createDialog(Context context, BuyProductDialog.BuyProductListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Select Event for this Product")
                .setPositiveButton("Buy Product", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                EventCard event = eventViewModel.getEventByName(eventSpinner.getSelectedItem().toString());
                listener.onBuyProduct(product.getId(), event.getId());
                dialog.dismiss();
            });
        });

        dialog.show();
    }
}
