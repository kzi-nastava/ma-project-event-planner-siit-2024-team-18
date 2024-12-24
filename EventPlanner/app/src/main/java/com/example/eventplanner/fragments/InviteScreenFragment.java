package com.example.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eventplanner.activities.details.EventDetailsActivity;
import com.example.eventplanner.adapters.EmailAdapter;
import com.example.eventplanner.databinding.FragmentInviteScreenBinding;
import com.example.eventplanner.viewmodels.InviteScreenViewModel;

public class InviteScreenFragment extends Fragment {

    private FragmentInviteScreenBinding binding;
    private InviteScreenViewModel viewModel;
    private EmailAdapter emailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInviteScreenBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(InviteScreenViewModel.class);

        setupRecyclerView();
        setupObservers();

        binding.addEmailButton.setOnClickListener(v -> {
            String email = binding.emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                viewModel.addEmail(email);
                binding.emailInput.setText("");
            } else {
                viewModel.showToast("Please enter a valid email");
            }
        });

        binding.sendInvitesButton.setOnClickListener(v -> {
            int eventId = getArguments() != null ? getArguments().getInt("eventId", -1) : -1;
            if (eventId != -1) {
                viewModel.sendInvites(eventId);
            } else {
                viewModel.showToast("Invalid event ID!");
            }
        });

        binding.closeButton.setOnClickListener(v -> navigateBackToEventDetails());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        emailAdapter = new EmailAdapter(viewModel);
        binding.emailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.emailRecyclerView.setAdapter(emailAdapter);
    }

    private void setupObservers() {
        viewModel.getEmailList().observe(getViewLifecycleOwner(), emails -> {
            emailAdapter.submitList(emails);
            binding.sendInvitesButton.setEnabled(!emails.isEmpty());
        });

        viewModel.getIsSending().observe(getViewLifecycleOwner(), isSending -> {
            if (isSending) {
                binding.sendInvitesButton.setText("Sending Invites...");
                binding.sendInvitesButton.setEnabled(false);
            } else {
                binding.sendInvitesButton.setText("Send Invites");
                binding.sendInvitesButton.setEnabled(true);
            }
        });

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBackToEventDetails() {
        int eventId = getArguments() != null ? getArguments().getInt("eventId", -1) : -1;

        if (eventId != -1) {
            Intent intent = new Intent(getContext(), EventDetailsActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Event ID is missing!", Toast.LENGTH_SHORT).show();
        }
    }
}
