package com.example.eventplanner.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.EditServiceActivity;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.ServiceListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends ArrayAdapter<Service> {
    private LinearLayout serviceCard;
    private ArrayList<Service> services;
    private Activity activity;
    private ServiceListViewModel serviceListViewModel;
    private TextView serviceName, serviceDescription;
    private ImageView imgService;
    private FrameLayout frameEditService, frameDeleteService;
    public ServiceListAdapter(Activity context, ServiceListViewModel serviceListViewModel) {
        super(context, R.layout.service_card);
        this.services = new ArrayList<>();
        this.activity = context;
        this.serviceListViewModel = serviceListViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_card, parent, false);
        }

        Service service = getItem(position);

        initializeViews(convertView);
        populateFields(service);
        setupListeners(service);

        return convertView;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Nullable
    @Override
    public Service getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        serviceCard = convertView.findViewById(R.id.serviceCard);
        serviceName = convertView.findViewById(R.id.txtServiceName);
        serviceDescription = convertView.findViewById(R.id.txtServiceDescription);
        imgService = convertView.findViewById(R.id.imgService);
        frameEditService = convertView.findViewById(R.id.editService);
        frameDeleteService = convertView.findViewById(R.id.deleteService);
    }

    private void populateFields(Service service) {
        serviceName.setText(service.getName());
        serviceDescription.setText(service.getDescription());

        String imageUrl = service.getImages()[0];
        Glide.with(activity)
                .load(imageUrl)
                .into(imgService);

    }

    private void setupListeners(Service service) {
        frameEditService.setOnClickListener(v -> {
            Intent editIntent = new Intent(getContext(), EditServiceActivity.class);
            editIntent.putExtra("serviceId", service.getId());
            getContext().startActivity(editIntent);
        });

        frameDeleteService.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        serviceListViewModel.deleteServiceById(service.getId());
                        Toast.makeText(v.getContext(), "Service deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        serviceCard.setOnClickListener(v -> {
            Intent detailIntent = new Intent(activity, ServiceDetailsActivity.class);
            detailIntent.putExtra("solutionId", service.getId());
            activity.startActivity(detailIntent);
        });
    }

    public void updateServicesList(List<Service> services) {
        if (services != null) {
            this.services.clear();
            this.services.addAll(services);
            notifyDataSetChanged();
        }
    }
}
