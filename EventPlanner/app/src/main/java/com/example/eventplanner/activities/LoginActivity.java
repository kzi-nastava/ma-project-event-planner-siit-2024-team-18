package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.models.AuthResponse;
import com.example.eventplanner.models.Login;
import com.example.eventplanner.viewmodels.CommunicationViewModel;
import com.example.eventplanner.viewmodels.LoginViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;
import com.example.eventplanner.websocket.WebSocketManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private LoginViewModel viewModel;
    private UserViewModel userViewModel;
    private CommunicationViewModel communicationViewModel;
    private WebSocketManager webSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeViews();

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            boolean emailValid = ValidateEmail(email);
            boolean passwordValid = ValidatePassword(password);

            if (emailValid && passwordValid) {
                performLogin(email, password);
            }
        });

        TextView signUpTextView = findViewById(R.id.text_sign_up);
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void InitializeViews() {
        emailErrorTextView = findViewById(R.id.text_email_error);
        passwordErrorTextView = findViewById(R.id.text_password_error);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        signInButton = findViewById(R.id.button_sign_in);
        viewModel = LoginViewModel.getInstance(getApplicationContext());
    }

    private boolean ValidateEmail(String email) {
        if (email.isEmpty()) {
            emailErrorTextView.setText(R.string.please_enter_e_mail_address);
            emailErrorTextView.setVisibility(View.VISIBLE);
            return false;
        } else if (!email.matches(EMAIL_REGEX)) {
            emailErrorTextView.setText(R.string.e_mail_address_is_invalid);
            emailErrorTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            emailErrorTextView.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidatePassword(String password) {
        if (password.isEmpty()) {
            passwordErrorTextView.setText(R.string.please_enter_password);
            passwordErrorTextView.setVisibility(View.VISIBLE);
            return false;
        } else {
            passwordErrorTextView.setVisibility(View.GONE);
            return true;
        }
    }

    private void performLogin(String email, String password) {
        Login login = new Login(email, password);

        viewModel.login(login).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    viewModel.saveToken(token);
                    handleWebsockets();

                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleWebsockets() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setContext(this);

        userViewModel.getLoggedUser().observe(this, loggedUser -> {
            if (loggedUser != null) {
                communicationViewModel = CommunicationViewModel.getInstance();
                communicationViewModel.initialize(this);
            }
        });

        userViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.fetchLoggedUser();
    }

    private void handleError(int statusCode) {
        if (statusCode == 404) {
            Toast.makeText(this, "User with email " + emailEditText.getText().toString() + " not found.", Toast.LENGTH_SHORT).show();
        } else if (statusCode == 400) {
            Toast.makeText(this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
        }
    }
}