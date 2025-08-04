package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.models.ReportRequest;

import java.util.ArrayList;
import java.util.List;

public class ReportCardAdapter extends RecyclerView.Adapter<ReportCardAdapter.ReportViewHolder> {

    private final Context context;
    private final List<ReportRequest> reports = new ArrayList<>();
    private final OnReportActionListener onActionListener;

    public interface OnReportActionListener {
        void onAction(int reportId, boolean approve);
    }

    public ReportCardAdapter(Context context, OnReportActionListener onActionListener) {
        this.context = context;
        this.onActionListener = onActionListener;
    }

    public void updateReports(List<ReportRequest> newReports) {
        reports.clear();
        reports.addAll(newReports);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report_card, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportRequest report = reports.get(position);

        holder.reportTitle.setText(String.format("%s %s", report.getReportedFirstName(), report.getReportedLastName()));
        holder.reporterName.setText(String.format("Reported by: %s %s", report.getReporterFirstName(), report.getReporterLastName()));
        holder.reportDescription.setText(report.getDescription());
        holder.reportDate.setText(report.getReportedDate().toLocalDate().toString());

        holder.approveButton.setOnClickListener(v -> onActionListener.onAction(report.getId(), true));
        holder.removeButton.setOnClickListener(v -> onActionListener.onAction(report.getId(), false));
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reportTitle, reporterName, reportDescription, reportDate;
        Button approveButton, removeButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportTitle = itemView.findViewById(R.id.reportTitle);
            reporterName = itemView.findViewById(R.id.reporterName);
            reportDescription = itemView.findViewById(R.id.reportDescription);
            reportDate = itemView.findViewById(R.id.reportDate);
            approveButton = itemView.findViewById(R.id.approveButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
