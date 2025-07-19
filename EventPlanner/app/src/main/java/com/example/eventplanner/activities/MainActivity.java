package com.example.eventplanner.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.UpdatedInvite;
import com.example.eventplanner.models.User;
import com.example.eventplanner.utils.NotificationHelper;
import com.example.eventplanner.utils.PermissionHelper;
import com.example.eventplanner.viewmodels.CommunicationViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;
import com.example.eventplanner.websocket.WebSocketManager;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    private UserViewModel userViewModel;
    private CommunicationViewModel communicationViewModel;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!PermissionHelper.hasNotificationPermission(this)) {
            PermissionHelper.requestNotificationPermission(this);
        }

        NotificationHelper.createChannel(getApplicationContext());

        getLoggedUser();

        Uri data = getIntent().getData();
        if (data != null) {
            handleDeepLink(data);
        } else {
            showSplashScreen();
        }
    }

    private void getLoggedUser() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setContext(this);

        userViewModel.getLoggedUser().observe(this, loggedUser -> {
            if (loggedUser != null) {
                this.loggedUser = loggedUser;
                communicationViewModel = CommunicationViewModel.getInstance();
                communicationViewModel.initialize(this);

                initializeWebSocketManager();
            }
        });

        userViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.fetchLoggedUser();
    }

    private void initializeWebSocketManager() {
        webSocketManager = WebSocketManager.getInstance(this, loggedUser, communicationViewModel);
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
        String idString = data.getQueryParameter("id");
        if (inviteIdString != null) {
            int inviteId = Integer.parseInt(inviteIdString);
            acceptInvite(inviteId);
        } else if (idString != null) {
            int id = Integer.parseInt(idString);
            activateAccount(id);
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
        ClientUtils.getInviteService(this).acceptInvite(inviteId).enqueue(new Callback<UpdatedInvite>() {
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
                    showSplashScreen();
                }
            }

            @Override
            public void onFailure(Call<UpdatedInvite> call, Throwable t) {
                Log.e("MainActivity", "Error accepting invite: " + t.getMessage());
            }
        });
    }

    private void activateAccount(int id) {
        ClientUtils.getRegistrationRequestService(this).activateAccount(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Account activated", Toast.LENGTH_LONG).show();
                    showSplashScreenToLogin();
                } else if (response.code() == 400) {
                    Toast.makeText(MainActivity.this, "Activation link expired", Toast.LENGTH_LONG).show();
                    showSplashScreenToLogin();
                } else {
                    Log.e("MainActivity", "Error in account activation: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MainActivity", "Error in communicating with server: " + t.getMessage());
            }
        });
    }
}
