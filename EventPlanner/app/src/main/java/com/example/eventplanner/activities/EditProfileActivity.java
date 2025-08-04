package com.example.eventplanner.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.clients.UserService;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.models.UpdateUser;
import com.example.eventplanner.models.User;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {
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
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmNewPassword;

    private TextView errorCompanyName;
    private TextView errorDescription;
    private TextView errorFirstName;
    private TextView errorLastName;
    private TextView errorAddress;
    private TextView errorPhoneNumber;
    private TextView errorOldPassword;
    private TextView errorNewPassword;
    private TextView errorConfirmNewPassword;
    private TextView errorCategories;
    private TextView errorEventTypes;

    private Button selectCategories;
    private Button selectEventTypes;
    private Button selectPhotoButton;
    private Button editButton;

    private ImageView profilePhoto;
    private ImageView delete;

    private User user;

    private final List<String> categories = new ArrayList<>();
    private List<String> selectedCategories = new ArrayList<>();

    private final List<String> eventTypes = new ArrayList<>();
    private List<String> selectedEventTypes = new ArrayList<>();

    private CategoryCardViewModel categoryCardViewModel;
    private EventTypeCardViewModel eventTypeCardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_profile, findViewById(R.id.content_frame));

        InitializeComponents();
        getLoggedUser();

        ImageView backArrow = findViewById(R.id.backImage);
        backArrow.setOnClickListener(v -> super.onBackPressed());

        selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        delete.setOnClickListener(v -> {
            delete.setVisibility(View.GONE);
            profilePhoto.setImageResource(R.drawable.profile_photo_placeholder);
        });

        selectCategories.setOnClickListener(v -> {
            boolean[] checkedItems = new boolean[categories.size()];

            for (int i = 0; i < categories.size(); i++) {
                checkedItems[i] = selectedCategories.contains(categories.get(i));
            }

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Categories")
                    .setMultiChoiceItems(
                            categories.toArray(new CharSequence[0]),
                            checkedItems,
                            (dialog, which, isChecked) -> {
                                String selectedItem = categories.get(which);
                                if (isChecked) {
                                    selectedCategories.add(selectedItem);
                                } else {
                                    selectedCategories.remove(selectedItem);
                                }
                            })
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (selectedCategories.isEmpty()) {
                            selectCategories.setText(R.string.select_categories);
                        } else {
                            selectCategories.setText(selectedCategories.toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        selectEventTypes.setOnClickListener(v -> {
            boolean[] checkedItems = new boolean[eventTypes.size()];

            for (int i = 0; i < eventTypes.size(); i++) {
                checkedItems[i] = selectedEventTypes.contains(eventTypes.get(i));
            }

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Event Types")
                    .setMultiChoiceItems(
                            eventTypes.toArray(new CharSequence[0]),
                            checkedItems,
                            (dialog, which, isChecked) -> {
                                String selectedItem = eventTypes.get(which);
                                if (isChecked) {
                                    selectedEventTypes.add(selectedItem);
                                } else {
                                    selectedEventTypes.remove(selectedItem);
                                }
                            })
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (selectedEventTypes.isEmpty()) {
                            selectEventTypes.setText(R.string.select_event_types);
                        } else {
                            selectEventTypes.setText(selectedEventTypes.toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        editButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String companyName = editTextCompanyName.getText().toString().trim();
            String address = editTextAddress.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String oldPassword = editTextOldPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmNewPassword = editTextConfirmNewPassword.getText().toString().trim();

            boolean firstNameValid = ValidateFirstName(firstName);
            boolean lastNameValid = ValidateLastName(lastName);
            boolean addressValid = ValidateAddress(address);
            boolean phoneNumberValid = ValidatePhoneNumber(phoneNumber);
            boolean passwordValid = ValidatePassword(oldPassword, newPassword, confirmNewPassword);
            boolean companyNameValid = true;
            boolean descriptionValid = true;
            boolean categoriesValid = true;
            boolean eventTypesValid = true;

            if (user.getRole().equals("SERVICE_PRODUCT_PROVIDER")) {
                companyNameValid = ValidateCompanyName(companyName);
                descriptionValid = ValidateDescription(description);
                categoriesValid = ValidateCategories();
                eventTypesValid = ValidateEventTypes();
            }

            if (firstNameValid && lastNameValid && addressValid && phoneNumberValid &&
                    passwordValid && companyNameValid && descriptionValid && categoriesValid && eventTypesValid) {
                UpdateUser updateUser = new UpdateUser(user.getId(), email, firstName, lastName, user.getRole(), companyName, user.getImage(), address, phoneNumber, description, selectedCategories, selectedEventTypes, oldPassword, newPassword);

                UserService userService = ClientUtils.getUserService(this);
                Call<Void> call;
                call = userService.updateProfile(updateUser);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "You have successfuly updated your profile data.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EditProfileActivity.this, UserProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(EditProfileActivity.this, "Old password is incorrect.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

        editTextDescription = findViewById(R.id.edit_text_description);
        editTextCompanyName = findViewById(R.id.edit_text_company_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextFirstName = findViewById(R.id.edit_text_first_name);
        editTextLastName = findViewById(R.id.edit_text_last_name);
        editTextAddress = findViewById(R.id.edit_text_address);
        editTextPhoneNumber = findViewById(R.id.edit_text_phone_number);
        editTextOldPassword = findViewById(R.id.edit_text_old_password);
        editTextNewPassword = findViewById(R.id.edit_text_new_password);
        editTextConfirmNewPassword = findViewById(R.id.edit_text_confirm_new_password);

        errorCompanyName = findViewById(R.id.text_company_name_error);
        errorDescription = findViewById(R.id.text_description_error);
        errorFirstName = findViewById(R.id.text_first_name_error);
        errorLastName = findViewById(R.id.text_last_name_error);
        errorAddress = findViewById(R.id.text_address_error);
        errorPhoneNumber = findViewById(R.id.text_phone_number_error);
        errorOldPassword = findViewById(R.id.text_old_password_error);
        errorNewPassword = findViewById(R.id.text_new_password_error);
        errorConfirmNewPassword = findViewById(R.id.text_confirm_new_password_error);
        errorCategories = findViewById(R.id.text_categories_error);
        errorEventTypes = findViewById(R.id.text_event_types_error);

        selectPhotoButton = findViewById(R.id.button_select_photo);
        editButton = findViewById(R.id.button_edit);
        selectCategories = findViewById(R.id.btn_select_categories);
        selectEventTypes = findViewById(R.id.btn_select_event_types);

        profilePhoto = findViewById(R.id.image_view_profile_photo);
        delete = findViewById(R.id.delete);

        categoryCardViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryCardViewModel.setContext(this);
        eventTypeCardViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeCardViewModel.setContext(this);
    }

    private void getLoggedUser() {
        Call<User> call = ClientUtils.getUserService(this).getUserProfile();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    Log.d("UserProfileActivity", "User fetched: " + user.toString());

                    populateFields();
                } else {
                    Log.e("UserProfileActivity", "Failed to fetch user: " + response.message());
                    Toast.makeText(EditProfileActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserProfileActivity", "API call failed", t);
                Toast.makeText(EditProfileActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SetVisibilityForComponents(int id) {
        layoutCompanyName.setVisibility(id);
        editTextCompanyName.setVisibility(id);
        layoutDescription.setVisibility(id);
        editTextDescription.setVisibility(id);
        layoutCategories.setVisibility(id);
        selectCategories.setVisibility(id);
        layoutEventTypes.setVisibility(id);
        selectEventTypes.setVisibility(id);
    }

    private void populateFields() {
        Glide.with(this)
                .load(user.getImage())
                .placeholder(R.drawable.profile_photo_placeholder)
                .error(R.drawable.profile_photo_placeholder)
                .into(profilePhoto);
        editTextEmail.setText(user.getEmail());
        editTextFirstName.setText(user.getFirstName());
        editTextLastName.setText(user.getLastName());
        editTextAddress.setText(user.getAddress());
        editTextPhoneNumber.setText(user.getPhone());
        if (user.getRole().equals("SERVICE_PRODUCT_PROVIDER")) {
            SetVisibilityForComponents(View.VISIBLE);
            categoryCardViewModel.fetchCategories();
            categoryCardViewModel.getCategories().observeForever(categoryList -> {
                categories.clear();
                for (Category category : categoryList) {
                    categories.add(category.getName());
                }
            });

            eventTypeCardViewModel.fetchEventTypes();
            eventTypeCardViewModel.getEventTypes().observeForever(eventTypeList -> {
                eventTypes.clear();
                for (EventType eventType : eventTypeList) {
                    eventTypes.add(eventType.getName());
                }
            });
            editTextCompanyName.setText(user.getCompanyName());
            editTextDescription.setText(user.getDescription());
            selectedEventTypes = user.getEventTypes();
            selectEventTypes.setText(selectedEventTypes.toString());
            selectedCategories = user.getCategories();
            selectCategories.setText(selectedCategories.toString());
        }
    }

    private boolean ValidateFirstName(String firstName) {
        if (firstName.isEmpty()) {
            errorFirstName.setText(R.string.please_enter_first_name);
            errorFirstName.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorFirstName.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidateLastName(String lastName) {
        if (lastName.isEmpty()) {
            errorLastName.setText(R.string.please_enter_last_name);
            errorLastName.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorLastName.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidateCompanyName(String companyName) {
        if (companyName.isEmpty()) {
            errorCompanyName.setText(R.string.please_enter_company_name);
            errorCompanyName.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorCompanyName.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidateDescription(String description) {
        if (description.isEmpty()) {
            errorDescription.setText(R.string.please_enter_description);
            errorDescription.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorDescription.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidateAddress(String address) {
        if (address.isEmpty()) {
            errorAddress.setText(R.string.please_enter_address);
            errorAddress.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorAddress.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            errorPhoneNumber.setText(R.string.please_enter_phone_number);
            errorPhoneNumber.setVisibility(View.VISIBLE);
            return false;
        } else if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
            errorPhoneNumber.setText(R.string.phone_number_is_invalid);
            errorPhoneNumber.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorPhoneNumber.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidatePassword(String oldPassword, String newPassword, String confirmNewPassword) {
        boolean isValid = true;

        errorOldPassword.setText("");
        errorOldPassword.setVisibility(View.GONE);
        errorNewPassword.setText("");
        errorNewPassword.setVisibility(View.GONE);
        errorConfirmNewPassword.setText("");
        errorConfirmNewPassword.setVisibility(View.GONE);

        if (oldPassword != null && !oldPassword.isEmpty()) {
            if (newPassword == null || newPassword.isEmpty() || confirmNewPassword == null || confirmNewPassword.isEmpty()) {
                if (newPassword == null || newPassword.isEmpty()) {
                    errorNewPassword.setText("New Password is required.");
                    errorNewPassword.setVisibility(View.VISIBLE);
                    isValid = false;
                }
                if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
                    errorConfirmNewPassword.setText("Confirm New Password is required.");
                    errorConfirmNewPassword.setVisibility(View.VISIBLE);
                    isValid = false;
                }
            } else if (!newPassword.equals(confirmNewPassword)) {
                errorConfirmNewPassword.setText("New Password and Confirm New Password do not match.");
                errorConfirmNewPassword.setVisibility(View.VISIBLE);
                isValid = false;
            }
        } else if ((newPassword != null && !newPassword.isEmpty()) || (confirmNewPassword != null && !confirmNewPassword.isEmpty())) {
            if (oldPassword == null || oldPassword.isEmpty()) {
                errorOldPassword.setText("Old Password is required.");
                errorOldPassword.setVisibility(View.VISIBLE);
                isValid = false;
            }
            if (!newPassword.equals(confirmNewPassword)) {
                errorConfirmNewPassword.setText("New Password and Confirm New Password do not match.");
                errorConfirmNewPassword.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        return isValid;
    }


    private boolean ValidateCategories() {
        if (selectedCategories.isEmpty()) {
            errorCategories.setText(R.string.please_select_categories);
            errorCategories.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorCategories.setVisibility(View.GONE);
            return true;
        }
    }

    private boolean ValidateEventTypes() {
        if (selectedEventTypes.isEmpty()) {
            errorEventTypes.setText(R.string.please_select_event_types);
            errorEventTypes.setVisibility(View.VISIBLE);
            return false;
        } else {
            errorEventTypes.setVisibility(View.GONE);
            return true;
        }
    }
}