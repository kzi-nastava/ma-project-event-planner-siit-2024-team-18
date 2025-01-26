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
import com.example.eventplanner.models.CommentRequest;

import java.util.ArrayList;
import java.util.List;

public class CommentCardAdapter extends RecyclerView.Adapter<CommentCardAdapter.CommentViewHolder> {

    private final Context context;
    private final List<CommentRequest> comments = new ArrayList<>();
    private final OnCommentActionListener onActionListener;

    public interface OnCommentActionListener {
        void onAction(int commentId, boolean approve);
    }

    public CommentCardAdapter(Context context, OnCommentActionListener onActionListener) {
        this.context = context;
        this.onActionListener = onActionListener;
    }

    public void updateComments(List<CommentRequest> newComments) {
        comments.clear();
        comments.addAll(newComments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_card, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentRequest comment = comments.get(position);

        holder.commentTitle.setText("Comment Request");
        holder.commentStatus.setText(comment.getStatus());
        holder.commentText.setText(comment.getContent());
        holder.commentDate.setText(comment.getDate().toLocalDate().toString());

        holder.approveButton.setOnClickListener(v -> onActionListener.onAction(comment.getId(), true));
        holder.removeButton.setOnClickListener(v -> onActionListener.onAction(comment.getId(), false));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTitle, commentStatus, commentText, commentDate;
        Button approveButton, removeButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTitle = itemView.findViewById(R.id.commentTitle);
            commentStatus = itemView.findViewById(R.id.commentStatus);
            commentText = itemView.findViewById(R.id.commentText);
            commentDate = itemView.findViewById(R.id.commentDate);
            approveButton = itemView.findViewById(R.id.approveButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
