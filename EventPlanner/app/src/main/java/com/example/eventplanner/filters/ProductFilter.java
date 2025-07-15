package com.example.eventplanner.filters;

import android.widget.Filter;
import android.widget.Filterable;

import com.example.eventplanner.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductFilter extends Filter implements Filterable {
    private List<Product> products, filteredProducts;
    private ProductFilter productFilter;

    public ProductFilter(List<Product> productsList) {
        products = productsList;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        Filter.FilterResults results = new Filter.FilterResults();
        if (constraint == null || constraint.length() == 0) {
            results.values = products;
            results.count = products.size();
        } else {
            String filterString = constraint.toString().toLowerCase();
            List<Product> filteredList = new ArrayList<>();

            for (Product product : products) {
                if (product.getName().toLowerCase().contains(filterString) ||
                        product.getCategory().toLowerCase().contains(filterString) ||
                        product.getEventTypes().contains(filterString)) {
                    filteredList.add(product);
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        filteredProducts = (List<Product>) results.values;
    }

    @Override
    public Filter getFilter() {
        if (productFilter == null) {
            productFilter = new ProductFilter(products);
        }
        return productFilter;
    }
}
