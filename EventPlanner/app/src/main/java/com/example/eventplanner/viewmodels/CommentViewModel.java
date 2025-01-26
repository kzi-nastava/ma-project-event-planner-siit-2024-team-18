package com.example.eventplanner.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Comment;
import com.example.eventplanner.models.CommentRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentViewModel extends ViewModel {

    private final MutableLiveData<List<CommentRequest>> commentsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<List<CommentRequest>> getComments() {
        return commentsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public void fetchComments() {
        loading.setValue(true);
        ClientUtils.getCommentService(context).getPendingComments().enqueue(new Callback<List<CommentRequest>>() {
            @Override
            public void onResponse(Call<List<CommentRequest>> call, Response<List<CommentRequest>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    commentsLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to fetch comments.");
                }
            }

            @Override
            public void onFailure(Call<List<CommentRequest>> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void handleCommentAction(int commentId, boolean approve) {
        loading.setValue(true);
        Call<Comment> call = approve
                ? ClientUtils.getCommentService(context).approveComment(commentId)
                : ClientUtils.getCommentService(context).removeComment(commentId);

        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    removeCommentById(commentId);
                } else {
                    errorMessage.setValue("Failed to " + (approve ? "approve" : "remove") + " the comment.");
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                loading.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    private void removeCommentById(int commentId) {
        List<CommentRequest> currentComments = commentsLiveData.getValue();
        if (currentComments != null) {
            currentComments.removeIf(comment -> comment.getId() == commentId);
            commentsLiveData.setValue(currentComments);
        }
    }
}
