package com.example.eventplanner.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.EditServiceActivity;
import com.example.eventplanner.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends ArrayAdapter<Service> {
    private ArrayList<Service> services;
    private Activity activity;
    private FragmentManager fragmentManager;

    public ServiceListAdapter(Activity context, FragmentManager fragmentManager, List<Service> services) {
        super(context, R.layout.service_card, services);
        this.services = new ArrayList<>();
        this.activity = context;
        this.fragmentManager = fragmentManager;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_card, parent, false);
        }

        Service service = getItem(position);

        TextView serviceName = convertView.findViewById(R.id.txtServiceName);
        TextView serviceDescription = convertView.findViewById(R.id.txtServiceDescription);

        serviceName.setText(service.getName());
        serviceDescription.setText(service.getDescription());

        ImageView imgService = convertView.findViewById(R.id.imgService);
        String imageUrl = service.getImages()[0];
        Glide.with(activity)
                .load(imageUrl)
                .into(imgService);

        FrameLayout frameEditService = convertView.findViewById(R.id.editService);
        frameEditService.setOnClickListener(v -> {
            // open edit activity
            Intent editIntent = new Intent(getContext(), EditServiceActivity.class);
            editIntent.putExtra("serviceId", service.getId());
            editIntent.putExtra("position", position);
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

    public void updateServicesList(List<Service> services) {
        if (services != null) {
            this.services.clear();
            this.services.addAll(services);
            notifyDataSetChanged();
        }
    }
}
