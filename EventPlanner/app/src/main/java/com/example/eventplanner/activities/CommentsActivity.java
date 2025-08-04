package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.fragments.CommentListFragment;
import com.example.eventplanner.viewmodels.CommentViewModel;

public class CommentsActivity extends BaseActivity {
    private ImageView btnBack;
    private int solutionId;
    private CommentViewModel commentViewModel;
    private CommentListFragment commentListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_comments, findViewById(R.id.content_frame));

        solutionId = getIntent().getIntExtra("solutionId", -1);
        initializeViews();
        initializeCommentsFragment();
        setupListeners();
    }

    private void initializeViews() {
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        commentViewModel.setContext(this);
        btnBack = findViewById(R.id.btnBack);
    }

    private void initializeCommentsFragment() {
        commentListFragment = CommentListFragment.newInstance(solutionId);

        FragmentTransition.to(commentListFragment, this, false, R.id.listViewComments);
    }

    private void setupListeners() {
        setupBackButton();
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(view -> finish());
    }
}
