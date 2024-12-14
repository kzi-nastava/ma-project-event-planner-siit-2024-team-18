package com.example.eventplanner.filters;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.eventplanner.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceFilter extends Filter implements Filterable {
    private List<Service> services, filteredServices;
    private ServiceFilter serviceFilter;

    public ServiceFilter(List<Service> servicesList) {
        services = servicesList;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        Filter.FilterResults results = new Filter.FilterResults();
        if (constraint == null || constraint.length() == 0) {
            results.values = services;
            results.count = services.size();
        } else {
            String filterString = constraint.toString().toLowerCase();
            List<Service> filteredList = new ArrayList<>();

            for (Service service : services) {
                if (service.getName().toLowerCase().contains(filterString) ||
                        service.getCategory().toLowerCase().contains(filterString) ||
                        service.getEventTypes()[0].toLowerCase().contains(filterString)) {
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
        filteredServices = (List<Service>) results.values;
    }

    @Override
    public Filter getFilter() {
        if (serviceFilter == null) {
            serviceFilter = new ServiceFilter(services);
        }
        return serviceFilter;
    }
}
