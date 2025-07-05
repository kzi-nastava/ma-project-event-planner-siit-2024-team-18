package com.example.eventplanner.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.OtherUserProfileActivity;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Report;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportSubmissionFragment extends Fragment {

    private EditText reasonInput;
    private Button submitButton;

    private ImageButton closeButton;
    private int reportedUserId;

    public ReportSubmissionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_submission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reasonInput = view.findViewById(R.id.reason_input);
        submitButton = view.findViewById(R.id.submit_report_button);
        closeButton = view.findViewById(R.id.close_report_button);

        if (getArguments() != null) {
            reportedUserId = getArguments().getInt("userId", -1);
        }

        submitButton.setOnClickListener(v -> {
            String reason = reasonInput.getText().toString().trim();
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(getContext(), "Please enter a reason for reporting.", Toast.LENGTH_SHORT).show();
                return;
            }
            submitReport(reason);
        });

        closeButton.setOnClickListener(v -> navigateBackToOtherUser());
    }

    private void submitReport(String reason) {
        Report report = new Report();
        report.setDescription(reason);
        report.setReportedId(reportedUserId);

        Call<Report> call = ClientUtils.getReportService(getContext()).createReport(report);
        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Failed to submit the report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                Toast.makeText(getContext(), "Error submitting the report.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBackToOtherUser() {
        Intent intent = new Intent(getContext(), OtherUserProfileActivity.class);
        intent.putExtra("userId", reportedUserId);
        startActivity(intent);
        getActivity().finish();
    }
}
