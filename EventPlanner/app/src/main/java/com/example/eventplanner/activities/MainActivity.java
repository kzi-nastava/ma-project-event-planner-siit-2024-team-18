package com.example.eventplanner.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.UpdatedInvite;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri data = getIntent().getData();
        if (data != null) {
            handleDeepLink(data);
        } else {
            showSplashScreen();
        }
    }

    private void showSplashScreen() {
        int SPLASH_TIME_OUT = 250;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void handleDeepLink(Uri data) {
        String inviteIdString = data.getQueryParameter("inviteId");
        if (inviteIdString != null) {
            int inviteId = Integer.parseInt(inviteIdString);
            acceptInvite(inviteId);
        } else {
            Log.e("MainActivity", "Invite ID is missing in the URL.");
        }
    }

    private void showSplashScreenToLogin() {
        int SPLASH_TIME_OUT = 250;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void acceptInvite(int inviteId) {
        ClientUtils.inviteService.acceptInvite(inviteId).enqueue(new Callback<UpdatedInvite>() {
            @Override
            public void onResponse(Call<UpdatedInvite> call, Response<UpdatedInvite> response) {
                if (response.isSuccessful()) {
                    UpdatedInvite updatedInvite = response.body();
                    if (updatedInvite != null) {
                        Log.d("MainActivity", "Invite accepted: " + updatedInvite.toString());

                        if (updatedInvite.isLoggedIn()) {
                            showSplashScreen();
                        } else {
                            showSplashScreenToLogin();
                        }
                    }
                } else {
                    Log.e("MainActivity", "Failed to accept invite. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UpdatedInvite> call, Throwable t) {
                Log.e("MainActivity", "Error accepting invite: " + t.getMessage());
            }
        });
    }
}
