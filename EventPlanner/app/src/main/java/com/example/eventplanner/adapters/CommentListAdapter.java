package com.example.eventplanner.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private ArrayList<Comment> comments;
    private TextView commentContent;
    public CommentListAdapter(Activity context) {
        super(context, R.layout.comment_card);
        this.comments = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_card, parent, false);
        }

        Comment comment = getItem(position);

        initializeViews(convertView);
        populateFields(comment);

        return convertView;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Nullable
    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeViews(View convertView) {
        commentContent = convertView.findViewById(R.id.commentContent);
    }

    private void populateFields(Comment comment) {
        commentContent.setText(comment.getContent());
    }

    public void updateCommentList(List<Comment> allComments) {
        if (allComments != null) {
            this.comments.clear();
            this.comments.addAll(allComments);
            notifyDataSetChanged();
        }
    }
}
