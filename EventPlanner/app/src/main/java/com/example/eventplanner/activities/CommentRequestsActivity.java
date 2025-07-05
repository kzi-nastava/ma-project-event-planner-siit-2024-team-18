package com.example.eventplanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CommentCardAdapter;
import com.example.eventplanner.viewmodels.CommentViewModel;

public class CommentRequestsActivity extends AppCompatActivity {

    private CommentViewModel commentViewModel;
    private CommentCardAdapter commentCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_requests);

        RecyclerView commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentCardAdapter = new CommentCardAdapter(
                this,
                (commentId, approve) -> commentViewModel.handleCommentAction(commentId, approve)
        );
        commentsRecyclerView.setAdapter(commentCardAdapter);

        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        observeViewModel();

        commentViewModel.setContext(this);
        commentViewModel.fetchComments();
    }

    private void observeViewModel() {
        commentViewModel.getComments().observe(this, comments -> {
            if (comments != null && !comments.isEmpty()) {
                findViewById(R.id.noCommentsMessage).setVisibility(View.GONE);
                findViewById(R.id.commentsRecyclerView).setVisibility(View.VISIBLE);
                commentCardAdapter.updateComments(comments);
            } else {
                findViewById(R.id.noCommentsMessage).setVisibility(View.VISIBLE);
                findViewById(R.id.commentsRecyclerView).setVisibility(View.GONE);
            }
        });

        commentViewModel.isLoading().observe(this, isLoading -> {
            findViewById(R.id.loadingCommentsMessage).setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        commentViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
