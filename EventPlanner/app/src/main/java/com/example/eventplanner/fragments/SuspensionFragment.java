package com.example.eventplanner.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuspensionFragment extends DialogFragment {
    private static final String ARG_SUSPENSION_END_DATE = "suspensionEndDate";

    private String suspensionEndDate;
    private TextView remainingTimeTextView;

    public static SuspensionFragment newInstance(String suspensionEndDate) {
        SuspensionFragment fragment = new SuspensionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUSPENSION_END_DATE, suspensionEndDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suspensionEndDate = getArguments().getString(ARG_SUSPENSION_END_DATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suspension, container, false);
        remainingTimeTextView = view.findViewById(R.id.remaining_time);

        calculateRemainingTime();

        Button closeButton = view.findViewById(R.id.close_button);
        Button okButton = view.findViewById(R.id.ok_button);
        closeButton.setOnClickListener(v -> dismiss());
        okButton.setOnClickListener(v -> dismiss());

        TextView contactSupport = view.findViewById(R.id.contact_support);
        contactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:eventplannerteam18@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I need assistance with...");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        return view;
    }

    private void calculateRemainingTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date endDate = sdf.parse(suspensionEndDate);
            Date now = new Date();

            long diff = endDate.getTime() - now.getTime();

            if (diff > 0) {
                long days = diff / (1000 * 60 * 60 * 24);
                long hours = (diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
                long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);
                String remainingTime = days + " days, " + hours + " hours, and " + minutes + " minutes";
                remainingTimeTextView.setText(remainingTime);
            } else {
                remainingTimeTextView.setText("0 days, 0 hours, and 0 minutes");
            }
        } catch (ParseException e) {
            remainingTimeTextView.setText("Error calculating time.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}

