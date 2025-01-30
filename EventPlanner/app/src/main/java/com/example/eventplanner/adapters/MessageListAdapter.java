package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Message;

import java.util.List;

public class MessageListAdapter extends android.widget.BaseAdapter {
    private final Context context;
    private List<Message> messageList;

    public MessageListAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder();
            holder.senderName = convertView.findViewById(R.id.senderName);
            holder.messageContent = convertView.findViewById(R.id.messageContent);
            holder.timestamp = convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messageList.get(position);
//        holder.senderName.setText(message.getSenderName());
        holder.messageContent.setText(message.getContent());
        holder.timestamp.setText(message.getDate().toString());

        return convertView;
    }

    public void updateMessageList(List<Message> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView senderName;
        TextView messageContent;
        TextView timestamp;
    }
}
