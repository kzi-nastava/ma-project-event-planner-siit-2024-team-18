package com.example.eventplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PHONE_NUMBER_REGEX = "^\\d{9,10}$";

    private LinearLayout layoutCompanyName;
    private LinearLayout layoutDescription;
    private LinearLayout layoutCategories;
    private LinearLayout layoutEventTypes;

    private EditText editTextCompanyName;
    private EditText editTextDescription;
    private EditText editTextEmail;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextAddress;
    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private TextView errorCompanyName;
    private TextView errorDescription;
    private TextView errorEmail;
    private TextView errorFirstName;
    private TextView errorLastName;
    private TextView errorAddress;
    private TextView errorPhoneNumber;
    private TextView errorPassword;
    private TextView errorConfirmPassword;

    private Spinner spinnerCategories;
    private Spinner spinnerEventTypes;

    private Button selectPhotoButton;
    private Button signUpButton;

    private RadioGroup role;

    private ImageView profilePhoto;
    private ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();

        ImageView backArrow = findViewById(R.id.backImage);
        backArrow.setOnClickListener(v -> super.onBackPressed());

        role = findViewById(R.id.radioGroupRole);
        role.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioEventOrganizer) {
                SetVisibilityForComponents(View.GONE);
                errorDescription.setVisibility(View.GONE);
                errorCompanyName.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioServiceProductProvider) {
                SetVisibilityForComponents(View.VISIBLE);
            }
        });

        selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        delete.setOnClickListener(v -> {
            delete.setVisibility(View.GONE);
            profilePhoto.setImageResource(R.drawable.profile_photo_placeholder);
        });



        signUpButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String companyName = editTextCompanyName.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            ValidateEmail(email);
            ValidateFirstName(firstName);
            ValidateLastName(lastName);
            ValidateAddress(address);
            ValidatePhoneNumber(phoneNumber);
            ValidatePassword(password, confirmPassword);

            if (role.getCheckedRadioButtonId() == R.id.radioServiceProductProvider) {
                ValidateCompanyName(companyName);
                ValidateDescription(description);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                profilePhoto.setImageURI(selectedImageUri);
                delete.setVisibility(View.VISIBLE);
            }
        }
    }

    private void InitializeComponents() {
        layoutCompanyName = findViewById(R.id.layout_company_name);
        layoutDescription = findViewById(R.id.layout_description);
        layoutCategories = findViewById(R.id.layout_categories);
        layoutEventTypes = findViewById(R.id.layout_event_types);

        spinnerCategories = findViewById(R.id.spinner_categories);
        spinnerEventTypes = findViewById(R.id.spinner_event_types);

        editTextDescription = findViewById(R.id.edit_text_description);
        editTextCompanyName = findViewById(R.id.edit_text_company_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextFirstName = findViewById(R.id.edit_text_first_name);
        editTextLastName = findViewById(R.id.edit_text_last_name);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextPhoneNumber = findViewById(R.id.edit_text_phone_number);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password);

        errorCompanyName = findViewById(R.id.text_company_name_error);
        errorDescription = findViewById(R.id.text_description_error);
        errorEmail = findViewById(R.id.text_email_error);
        errorFirstName = findViewById(R.id.text_first_name_error);
        errorLastName = findViewById(R.id.text_last_name_error);
        errorAddress = findViewById(R.id.text_address_error);
        errorPhoneNumber = findViewById(R.id.text_phone_number_error);
        errorPassword = findViewById(R.id.text_password_error);
        errorConfirmPassword = findViewById(R.id.text_confirm_password_error);

        selectPhotoButton = findViewById(R.id.button_select_photo);
        signUpButton = findViewById(R.id.button_register);

        profilePhoto = findViewById(R.id.image_view_profile_photo);
        delete = findViewById(R.id.delete);
    }

    private void SetVisibilityForComponents(int id) {
        layoutCompanyName.setVisibility(id);
        editTextCompanyName.setVisibility(id);
        layoutDescription.setVisibility(id);
        editTextDescription.setVisibility(id);
        layoutCategories.setVisibility(id);
        spinnerCategories.setVisibility(id);
        layoutEventTypes.setVisibility(id);
        spinnerEventTypes.setVisibility(id);
    }

    private void ValidateEmail(String email) {
        if (email.isEmpty()) {
            errorEmail.setText(R.string.please_enter_e_mail_address);
            errorEmail.setVisibility(View.VISIBLE);
        } else if (!email.matches(EMAIL_REGEX)) {
            errorEmail.setText(R.string.e_mail_address_is_invalid);
            errorEmail.setVisibility(View.VISIBLE);
        } else {
            errorEmail.setVisibility(View.GONE);
        }
    }

    private void ValidateFirstName(String firstName) {
        if (firstName.isEmpty()) {
            errorFirstName.setText(R.string.please_enter_first_name);
            errorFirstName.setVisibility(View.VISIBLE);
        } else {
            errorFirstName.setVisibility(View.GONE);
        }
    }

    private void ValidateLastName(String lastName) {
        if (lastName.isEmpty()) {
            errorLastName.setText(R.string.please_enter_last_name);
            errorLastName.setVisibility(View.VISIBLE);
        } else {
            errorLastName.setVisibility(View.GONE);
        }
    }

    private void ValidateCompanyName(String companyName) {
        if (companyName.isEmpty()) {
            errorCompanyName.setText(R.string.please_enter_company_name);
            errorCompanyName.setVisibility(View.VISIBLE);
        } else {
            errorCompanyName.setVisibility(View.GONE);
        }
    }

    private void ValidateDescription(String description) {
        if (description.isEmpty()) {
            errorDescription.setText(R.string.please_enter_description);
            errorDescription.setVisibility(View.VISIBLE);
        } else {
            errorDescription.setVisibility(View.GONE);
        }
    }

    private void ValidateAddress(String address) {
        if (address.isEmpty()) {
            errorAddress.setText(R.string.please_enter_address);
            errorAddress.setVisibility(View.VISIBLE);
        } else {
            errorAddress.setVisibility(View.GONE);
        }
    }

    private void ValidatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            errorPhoneNumber.setText(R.string.please_enter_phone_number);
            errorPhoneNumber.setVisibility(View.VISIBLE);
        } else if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            errorPhoneNumber.setText(R.string.phone_number_is_invalid);
            errorPhoneNumber.setVisibility(View.VISIBLE);
        } else {
            errorPhoneNumber.setVisibility(View.GONE);
        }
    }

    private void ValidatePassword(String password, String confirmPassword) {
        if (password.isEmpty()) {
            errorPassword.setText(R.string.please_enter_password);
            errorPassword.setVisibility(View.VISIBLE);
        } else {
            errorPassword.setVisibility(View.GONE);
        }
        if (confirmPassword.isEmpty()) {
            errorConfirmPassword.setText(R.string.please_confirm_password);
            errorConfirmPassword.setVisibility(View.VISIBLE);
        } else if (!password.isEmpty() && !password.equals(confirmPassword)) {
            errorConfirmPassword.setText(R.string.not_same_as_password);
            errorConfirmPassword.setVisibility(View.VISIBLE);
        } else {
            errorConfirmPassword.setVisibility(View.GONE);
        }
     }
}