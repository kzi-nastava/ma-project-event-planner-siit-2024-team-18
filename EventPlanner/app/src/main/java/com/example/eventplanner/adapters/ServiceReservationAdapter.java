package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.models.ReservationDetails;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceReservationAdapter extends RecyclerView.Adapter<ServiceReservationAdapter.ReservationViewHolder> {

    private final Context context;
    private final List<ReservationDetails> reservations = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    public ServiceReservationAdapter(Context context) {
        this.context = context;
    }

    public void updateReservations(Collection<ReservationDetails> newReservations) {
        reservations.clear();
        reservations.addAll(newReservations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        ReservationDetails reservation = reservations.get(position);

        holder.dateTextView.setText(String.format("Date: %s", reservation.getDate().format(dateFormatter)));
        holder.timeFromTextView.setText(String.format("From: %s", reservation.getTimeFrom().format(timeFormatter)));
        holder.timeToTextView.setText(String.format("To: %s", reservation.getTimeTo().format(timeFormatter)));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView timeFromTextView;
        TextView timeToTextView;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeFromTextView = itemView.findViewById(R.id.time_from_text_view);
            timeToTextView = itemView.findViewById(R.id.time_to_text_view);
        }
    }
}
