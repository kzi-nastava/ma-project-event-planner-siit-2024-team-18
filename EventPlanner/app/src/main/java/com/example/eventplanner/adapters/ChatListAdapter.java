package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Chat;
import com.example.eventplanner.viewmodels.CommunicationViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends ArrayAdapter<Chat> {
    private CommunicationViewModel communicationViewModel;
    private LinearLayout chatCard;
    private ArrayList<Chat> chats;
    private ImageView chatImage;
    private TextView chatName, chatLastMessage, chatDate;

    public ChatListAdapter(Activity context, CommunicationViewModel communicationViewModel) {
        super(context, R.layout.chat_card);
        this.chats = new ArrayList<>();
        this.communicationViewModel = communicationViewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_card, parent, false);
        }

        Chat chat = getItem(position);

        initializeViews(convertView);
        populateFields(chat);
        setupListeners(chat);

        return convertView;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Nullable
    @Override
    public Chat getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        chatCard = convertView.findViewById(R.id.chatCard);
        chatImage = convertView.findViewById(R.id.chatImage);
        chatName = convertView.findViewById(R.id.chatName);
        chatLastMessage = convertView.findViewById(R.id.chatLastMessage);
        chatDate = convertView.findViewById(R.id.chatDate);
    }

    // TODO: get information from the chat message for logged user
    private void populateFields(Chat chat) {
//        chatName.setText(chat.getName());
//        chatImage.setImageURI(chat.getId());
    }

    private void setupListeners(Chat chat) {
//        messageListFragment = MessageListFragment.newInstance();
//        FragmentTransition.to(messageListFragment, this, false, R.id.listViewCategories);
    }

    public void updateChatsList(List<Chat> allChats) {
        if (allChats != null) {
            this.chats.clear();
            this.chats.addAll(allChats);
            notifyDataSetChanged();
        }
    }
}
