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
import androidx.fragment.app.Fragment;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.HomeScreenActivity;

public class ContentUnavailableFragment extends Fragment {

    public ContentUnavailableFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_unavailable, container, false);

        Button btnGoBack = view.findViewById(R.id.btn_go_back);

        btnGoBack.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HomeScreenActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        TextView contactSupport = view.findViewById(R.id.tv_support_email);
        contactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:eventplannerteam18@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I need assistance with...");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        return view;
    }
}
