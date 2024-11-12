package com.example.eventplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends ArrayAdapter<Service> implements Filterable {

    private List<Service> originalServicesList, filteredServicesList;
    private ServiceFilter serviceFilter;

    public ServiceAdapter(Context context, List<Service> services) {
        super(context, 0, services);
        this.originalServicesList = new ArrayList<>(services);
        this.filteredServicesList = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_card, parent, false);
        }

        Service service = getItem(position);

        // set service name
        TextView txtServiceName = convertView.findViewById(R.id.txtServiceName);
        txtServiceName.setText(service.getName());

        // set service description
        TextView txtServiceDescription = convertView.findViewById(R.id.txtServiceDescription);
        txtServiceDescription.setText(R.string.lorem_ipsum);

        // set service image
        ImageView imgService = convertView.findViewById(R.id.imgService);
        int imageResId = getServiceImage(service.getName());
        imgService.setImageResource(imageResId);

        FrameLayout frameEditService = convertView.findViewById(R.id.editService);
        frameEditService.setOnClickListener(v -> {
            // open edit activity
            Intent editIntent = new Intent(getContext(), EditServiceActivity.class);
            editIntent.putExtra("service", service);
            editIntent.putExtra("position", position);
            editIntent.putExtra("serviceId", service.getId());
            editIntent.putExtra("serviceName", service.getName());
            editIntent.putExtra("serviceSpecifics", service.getSpecifics());
            editIntent.putExtra("serviceDescription", R.string.lorem_ipsum);
            editIntent.putExtra("servicePrice", service.getPrice());
            editIntent.putExtra("serviceDiscount", service.getDiscount());
            editIntent.putExtra("serviceCategory", service.getCategory());
            editIntent.putExtra("serviceEventType", service.getEventType());
            editIntent.putExtra("serviceAvailable", service.isAvailable());
            editIntent.putExtra("serviceReservationDate", service.getReservationDate());
            editIntent.putExtra("serviceCancellationDate", service.getCancellationDate());
            editIntent.putExtra("serviceDuration", service.getDuration());
            editIntent.putExtra("serviceReservationType", service.getReservationType());
            getContext().startActivity(editIntent);
        });

        // delete confirmation dialog
        FrameLayout frameDeleteService = convertView.findViewById(R.id.deleteService);
        frameDeleteService.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Toast.makeText(v.getContext(), "Service deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        return convertView;
    }

    // mapping picture to service name (no db yet)
    private int getServiceImage(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "catering":
                return R.drawable.catering;
            case "dj":
                return R.drawable.dj;
            case "photography":
                return R.drawable.photography;
            case "decoration":
                return R.drawable.decoration;
            case "lighting":
                return R.drawable.lighting;
            default:
                return R.drawable.default_service;
        }
    }

    // search filter
    @Override
    public Filter getFilter() {
        if (serviceFilter == null) {
            serviceFilter = new ServiceFilter();
        }
        return serviceFilter;
    }

    private class ServiceFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = originalServicesList;
                results.count = originalServicesList.size();
            } else {
                String filterString = constraint.toString().toLowerCase();
                List<Service> filteredList = new ArrayList<>();

                for (Service service : originalServicesList) {
                    if (service.getName().toLowerCase().contains(filterString) ||
                            service.getCategory().toLowerCase().contains(filterString) ||
                            service.getEventType().toLowerCase().contains(filterString)) {
                        filteredList.add(service);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredServicesList = (List<Service>) results.values;
            notifyDataSetChanged();
        }
    }
}
