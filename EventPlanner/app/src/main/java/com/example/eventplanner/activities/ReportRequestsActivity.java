package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ReportCardAdapter;
import com.example.eventplanner.viewmodels.ReportViewModel;

public class ReportRequestsActivity extends AppCompatActivity {

    private ReportViewModel reportViewModel;
    private ReportCardAdapter reportCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_requests);

        RecyclerView reportsRecyclerView = findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));



        reportCardAdapter = new ReportCardAdapter(
                this,
                (reportId, approve) -> reportViewModel.handleReportAction(reportId, approve)
        );
        reportsRecyclerView.setAdapter(reportCardAdapter);

        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        observeViewModel();

        reportViewModel.setContext(this);
        reportViewModel.fetchReports();
    }

    private void observeViewModel() {
        reportViewModel.getReports().observe(this, reports -> {
            if (reports != null && !reports.isEmpty()) {
                findViewById(R.id.noReportsMessage).setVisibility(View.GONE);
                findViewById(R.id.reportsRecyclerView).setVisibility(View.VISIBLE);
                reportCardAdapter.updateReports(reports);
            } else {
                findViewById(R.id.noReportsMessage).setVisibility(View.VISIBLE);
                findViewById(R.id.reportsRecyclerView).setVisibility(View.GONE);
            }
        });

        reportViewModel.isLoading().observe(this, isLoading -> {
            findViewById(R.id.loadingReportsMessage).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        reportViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
