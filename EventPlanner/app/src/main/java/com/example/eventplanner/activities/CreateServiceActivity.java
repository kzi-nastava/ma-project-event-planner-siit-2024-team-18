package com.example.eventplanner.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateServiceActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private TextInputEditText serviceName, eventTypes, serviceDescription, servicePrice, discount, location, specifics, reservationDeadline, cancellationDeadline, workingHoursStart, workingHoursEnd;
    private ImageView btnClose, btnSelectPictures, btnClearPictures, btnWorkingHoursStart, btnWorkingHoursEnd;
    private TextView errorServiceName;
    private MaterialButton btnSaveNewService;
    private LinearLayout selectedImagesContainer;
    private List<Category> categories;
    private List<EventType> listEventTypes;
    private AutoCompleteTextView category;
    private Slider duration, minEngagement, maxEngagement;
    private RadioGroup reservationType;
    private SwitchCompat isVisible, isAvailable;
    private List<String> selectedEventTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_service, findViewById(R.id.content_frame));

        // initialize views
        serviceName = findViewById(R.id.createServiceName);
        serviceDescription = findViewById(R.id.createServiceDescription);
        servicePrice = findViewById(R.id.createServicePrice);
        discount = findViewById(R.id.createServiceDiscount);
        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        isVisible = findViewById(R.id.switchVisivility);
        isAvailable = findViewById(R.id.switchAvailability);
        category = findViewById(R.id.addServiceCategory);
        eventTypes = findViewById(R.id.editTextEventTypes);
        location = findViewById(R.id.inputServiceLocation);
        reservationType = findViewById(R.id.radioGroupReservationType);
        specifics = findViewById(R.id.inputServiceSpecifics);
        duration = findViewById(R.id.sliderDuration);
        minEngagement = findViewById(R.id.sliderFrom);
        maxEngagement = findViewById(R.id.sliderTo);
        reservationDeadline = findViewById(R.id.inputServiceReservationDeadline);
        cancellationDeadline = findViewById(R.id.inputServiceCancellationDeadline);
        workingHoursStart = findViewById(R.id.editStartTime);
        btnWorkingHoursStart = findViewById(R.id.btnPickStartTime);
        workingHoursEnd = findViewById(R.id.editEndTime);
        btnWorkingHoursEnd = findViewById(R.id.btnPickEndTime);

        errorServiceName = findViewById(R.id.errorServiceName);

        btnSaveNewService = findViewById(R.id.btnSaveNewService);

        // creating dummy data
        loadCategories();
        loadEventTypes();

        setupCategoryAutoComplete();
        setupEventTypesMultiSelect();

        // working with images
        btnSelectPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnClearPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectedImages();
            }
        });

        btnWorkingHoursStart.setOnClickListener(view -> showTimePicker(workingHoursStart));
        btnWorkingHoursEnd.setOnClickListener(view -> showTimePicker(workingHoursEnd));

        // closing activity
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            finish();
        });

        // save service
        btnSaveNewService.setOnClickListener(v -> saveNewService());
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    editText.setText(selectedTime);
                },
                hour, minute, true
        );

        timePickerDialog.show();
    }

    private void setupCategoryAutoComplete() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Arrays.asList("Category", "Food", "Music", "Media", "Venue")));
        category.setAdapter(categoryAdapter);

        category.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                category.showDropDown();
            }
        });

        category.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            category.setText(selectedCategory);
        });
    }

    private void setupEventTypesMultiSelect() {
        String[] eventTypeNames = new String[listEventTypes.size()];
        boolean[] selectedItems = new boolean[listEventTypes.size()];
        for (int i = 0; i < listEventTypes.size(); i++) {
            eventTypeNames[i] = listEventTypes.get(i).getName();
            selectedItems[i] = false;
        }

        eventTypes.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Event Types");

            builder.setMultiChoiceItems(eventTypeNames, selectedItems, (dialog, which, isChecked) -> {
                selectedItems[which] = isChecked;
            });

            builder.setPositiveButton("OK", (dialog, which) -> {
                selectedEventTypeList.clear(); // Clear previous selections
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i]) {
                        selectedEventTypeList.add(eventTypeNames[i]);
                    }
                }
                updateEventTypesInput();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            builder.show();
        });
    }

    private void updateEventTypesInput() {
        if (selectedEventTypeList.isEmpty()) {
            eventTypes.setText("");
        } else {
            eventTypes.setText(String.join(", ", selectedEventTypeList));
        }
    }

    private void clearSelectedImages() {
        imageUris.clear();
        selectedImagesContainer.removeAllViews();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // multiple images selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    addImageToContainer(imageUri);
                }
            } else if (data.getData() != null) {
                // single image selected
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                addImageToContainer(imageUri);
            }
        }
    }

    private void addImageToContainer(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);
        imageView.setImageURI(imageUri);
        selectedImagesContainer.addView(imageView);
    }

    // not implemented completely
    // TODO: merge reservation date and time together
    private void saveNewService() {
        boolean isValid = true;

        if (TextUtils.isEmpty(serviceName.getText())) {
            errorServiceName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceName.setVisibility(View.GONE);
        }

        if (isValid) {
            Toast.makeText(CreateServiceActivity.this, "Service submitted!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateServiceActivity.this, AllServicesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadCategories() {
        categories = new ArrayList<>();
        categories.add(new Category("Category"));
        categories.add(new Category("Food"));
        categories.add(new Category("Music"));
        categories.add(new Category("Media"));
        categories.add(new Category("Venue"));
    }

    private void loadEventTypes() {
        listEventTypes = new ArrayList<>();
    }
}
