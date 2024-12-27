package com.example.eventplanner.clients;

import android.content.Context;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Check if the 'skip' header is present
        if (originalRequest.header("skip") != null) {
            return chain.proceed(originalRequest); // Skip adding the token
        }

        // Retrieve the token from SharedPreferences
        String token = getTokenFromSharedPreferences();
        if (token != null && !token.isEmpty()) {
            // Add Authorization header with the token
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            return chain.proceed(newRequest); // Proceed with the new request
        }

        // Proceed with the original request if no token
        return chain.proceed(originalRequest);
    }

    // Retrieve the JWT token from SharedPreferences
    private String getTokenFromSharedPreferences() {
        // You can get the token from SharedPreferences (or wherever it's stored)
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("user_token", null);
    }
}
