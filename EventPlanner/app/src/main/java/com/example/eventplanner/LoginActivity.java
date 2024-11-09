package com.example.eventplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private TextView emailErrorTextView;
    private TextView passwordErrorTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;

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

            ValidateEmail(email);
            ValidatePassword(password);

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
    }

    private void ValidateEmail(String email) {
        if (email.isEmpty()) {
            emailErrorTextView.setText("Please enter e-mail address.");
            emailErrorTextView.setVisibility(View.VISIBLE);
        } else if (!email.matches(EMAIL_REGEX)) {
            emailErrorTextView.setText("E-mail address is invalid.");
            emailErrorTextView.setVisibility(View.VISIBLE);
        } else {
            emailErrorTextView.setVisibility(View.GONE);
        }
    }

    private void ValidatePassword(String password) {
        if (password.isEmpty()) {
            passwordErrorTextView.setText("Please enter password.");
            passwordErrorTextView.setVisibility(View.VISIBLE);
        } else {
            passwordErrorTextView.setVisibility(View.GONE);
        }
    }
}