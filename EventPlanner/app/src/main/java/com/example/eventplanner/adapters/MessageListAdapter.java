package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.models.Message;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import java.util.ArrayList;

public class MessageListAdapter extends android.widget.BaseAdapter {
    private CommunicationViewModel communicationViewModel;
    private final Context context;
    private Chat chat;
    private User loggedUser;
    private ArrayList<User> allUsers;
    private ArrayList<Message> messageList;

    public MessageListAdapter(Context context, CommunicationViewModel communicationViewModel, Chat chat, User loggedUser, ArrayList<User> allUsers) {
        this.context = context;
        this.messageList = new ArrayList<>();
        this.chat = chat;
        this.loggedUser = loggedUser;
        this.allUsers = allUsers;
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
        holder.senderName.setText(getUserFullName(loggedUser.getId() != chat.getUser2() ? chat.getUser1() : chat.getUser2()));
        holder.messageContent.setText(message.getContent());
        holder.timestamp.setText(message.getDate().toString());

        return convertView;
    }

    private String getUserFullName(int userId) {
        if (allUsers == null) return "Unknown User";

        for (User user : allUsers) {
            if (user.getId() == userId) {
                return user.getFirstName() + " " + user.getLastName();
            }
        }

        return "Unknown User";
    }

    public void updateMessageList(ArrayList<Message> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView senderName;
        TextView messageContent;
        TextView timestamp;
    }
}
