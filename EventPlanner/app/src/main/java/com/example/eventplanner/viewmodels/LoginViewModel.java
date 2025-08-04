package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.AuthResponse;
import com.example.eventplanner.models.Login;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String TOKEN_KEY = "user_token";

    private static LoginViewModel instance;
    private SharedPreferences sharedPreferences;

    private MutableLiveData<String> userRole = new MutableLiveData<>();
    private MutableLiveData<Boolean> loggedInStatus = new MutableLiveData<>();
    private Context context;

    private LoginViewModel(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userRole.setValue(getRole());
        loggedInStatus.setValue(isLoggedIn());
    }

    public static synchronized LoginViewModel getInstance(Context context) {
        if (instance == null) {
            instance = new LoginViewModel(context);
        }
        return instance;
    }

    public LiveData<Boolean> getLoggedInStatus() {
        return loggedInStatus;
    }

    public Call<AuthResponse> login(Login authRequest) {
        return ClientUtils.getAuthService(this.context).login(authRequest);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();

        userRole.setValue(getRole());
        loggedInStatus.setValue(isLoggedIn());
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN_KEY, null);
    }

    public String getRole() {
        String token = getToken();
        if (token != null) {
            try {
                JWT jwt = new JWT(token);

                Claim roleClaim = jwt.getClaim("role");
                if (roleClaim != null) {
                    List<Map> roles = roleClaim.asList(Map.class);

                    if (roles != null && !roles.isEmpty()) {
                        return (String) roles.get(0).get("authority");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserEmail() {
        String token = getToken();
        if (token != null) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("sub").asString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserFirstName() {
        String token = getToken();
        if (token != null) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("firstName").asString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserLastName() {
        String token = getToken();
        if (token != null) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("lastName").asString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getUserImage() {
        String token = getToken();
        if (token != null) {
            try {
                JWT jwt = new JWT(token);
                return jwt.getClaim("profilePhoto").asString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public LiveData<String> getUserRole() {
        return userRole;
    }

    public void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();

        userRole.setValue(null);
        loggedInStatus.setValue(false);
    }

    public void deactivateProfile() {
        Call<Void> call = ClientUtils.getUserService(this.context).deactivateProfile();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    signOut();
                } else {
                    Toast.makeText(context, "Failed to deactivate profile. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Failed to deactivate profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
