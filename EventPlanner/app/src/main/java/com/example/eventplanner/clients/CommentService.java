package com.example.eventplanner.clients;

import com.example.eventplanner.models.Comment;
import com.example.eventplanner.models.CommentRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CommentService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type: application/json"
    })
    @GET("comments")
    Call<List<CommentRequest>> getPendingComments();

    @PUT("comments/{commentId}/approve")
    Call<Comment> approveComment(@Path("commentId") int commentId);

    @PUT("comments/{commentId}/remove")
    Call<Comment> removeComment(@Path("commentId") int commentId);
}
