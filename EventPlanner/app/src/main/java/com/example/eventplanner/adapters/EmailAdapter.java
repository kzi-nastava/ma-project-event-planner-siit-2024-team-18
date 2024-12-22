package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.eventplanner.databinding.ItemEmailBinding;
import com.example.eventplanner.fragments.InviteScreenViewModel;

public class EmailAdapter extends ListAdapter<String, EmailAdapter.EmailViewHolder> {

    private final InviteScreenViewModel viewModel;

    public EmailAdapter(InviteScreenViewModel viewModel) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEmailBinding binding = ItemEmailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EmailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        String email = getItem(position);
        holder.binding.emailText.setText(email);
        holder.binding.deleteButton.setOnClickListener(v -> viewModel.removeEmail(email));
    }

    static class EmailViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final ItemEmailBinding binding;

        public EmailViewHolder(ItemEmailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
