package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.User;

import java.util.ArrayList;

public class MessageListAdapter extends android.widget.BaseAdapter {
    private final Context context;
    private User loggedUser;
    private ArrayList<Message> messageList;

    public MessageListAdapter(Context context, User loggedUser) {
        this.context = context;
        this.messageList = new ArrayList<>();
        this.loggedUser = loggedUser;
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
            holder.messageRoot = convertView.findViewById(R.id.messageRoot);
            holder.messageContent = convertView.findViewById(R.id.messageContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Message message = messageList.get(position);
        boolean isSentByLoggedUser = message.getSenderId() == loggedUser.getId();

        holder.messageRoot.setGravity(isSentByLoggedUser ? Gravity.START : Gravity.END);
        if (isSentByLoggedUser) {
            holder.messageContent.setBackgroundResource(R.drawable.message_background_sent);
            holder.messageContent.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.messageContent.setBackgroundResource(R.drawable.message_background_received);
            holder.messageContent.setTextColor(context.getResources().getColor(R.color.primary));
        }

        holder.messageContent.setText(message.getContent());

        return convertView;
    }

    public void updateMessageList(ArrayList<Message> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    public void addNewMessage(Message newMessage) {
        this.messageList.add(newMessage);
        notifyDataSetChanged();
    }

    public boolean containsMessage(Message newMessage) {
        for (Message msg : this.messageList) {
            if (msg.getId() == newMessage.getId()) {
                return true;
            }
        }
        return false;
    }

    private static class ViewHolder {
        LinearLayout messageRoot;
        TextView messageContent;
    }
}
