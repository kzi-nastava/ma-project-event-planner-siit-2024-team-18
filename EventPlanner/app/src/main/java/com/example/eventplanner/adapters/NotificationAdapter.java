package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.details.EventDetailsActivity;
import com.example.eventplanner.activities.details.ProductDetailsActivity;
import com.example.eventplanner.activities.details.ServiceDetailsActivity;
import com.example.eventplanner.models.NotificationModel;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public class NotificationAdapter extends ListAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final CommunicationViewModel viewModel;


    public NotificationAdapter(Context context, CommunicationViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = getItem(position);
        holder.bind(notification);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final TextView date;

        private final View container;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            content = itemView.findViewById(R.id.notificationContent);
            date = itemView.findViewById(R.id.notificationDate);
            container = itemView.findViewById(R.id.notificationCardContainer);
        }

        public void bind(NotificationModel notification) {
            title.setText(notification.getTitle());
            content.setText(notification.getContent());

            if (notification.getDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault());
                date.setText(notification.getDate().format(formatter));
            } else {
                date.setText("");
            }

            int seenColor = 0xFFD4EDDA;
            int unseenColor = 0xFFFFFFFF;

            container.setBackgroundColor(notification.isSeen() ? seenColor : unseenColor);

            itemView.setOnClickListener(v -> {
                if (!notification.isSeen()) {
                    viewModel.markNotificationAsSeen(notification.getId());
                    notification.setSeen(true);
                    notifyItemChanged(getAdapterPosition());
                }

                switch (notification.getNotificationType()) {
                    case "EVENT":
                        Intent eventIntent = new Intent(context, EventDetailsActivity.class);
                        eventIntent.putExtra("eventId", notification.getItemId());
                        context.startActivity(eventIntent);
                        break;
                    case "SERVICE":
                        Intent serviceIntent = new Intent(context, ServiceDetailsActivity.class);
                        serviceIntent.putExtra("solutionId", notification.getItemId());
                        context.startActivity(serviceIntent);
                        break;
                    case "PRODUCT":
                        Intent productIntent = new Intent(context, ProductDetailsActivity.class);
                        productIntent.putExtra("solutionId", notification.getItemId());
                        context.startActivity(productIntent);
                        break;
                }
            });
        }
    }

    public static final DiffUtil.ItemCallback<NotificationModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationModel oldItem, @NonNull NotificationModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };
}
