package com.example.eventplanner.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.google.android.material.button.MaterialButton;
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
    private TextInputEditText serviceName, serviceDescription, servicePrice, discount, location, specifics, reservationDeadline, cancellationDeadline, workingHoursStart, workingHoursEnd;
    private ImageView btnClose, btnSelectPictures, btnClearPictures, btnWorkingHoursStart, btnWorkingHoursEnd;
    private Spinner eventTypes;
    private TextView errorServiceName;
    private MaterialButton btnSaveNewService;
    private LinearLayout selectedImagesContainer;
    private List<Category> categories;
    private List<EventType> listEventTypes;
    private AutoCompleteTextView category;
    private Slider duration, minEngagement, maxEngagement;
    private RadioGroup reservationType;
    private SwitchCompat isVisible, isAvailable;

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
        eventTypes = findViewById(R.id.spinnerEventTypeCreate);
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

        // filling spinners with data
//        ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
//        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        categorySpinner.setAdapter(categoryAdapter);
//
//        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
//        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        eventTypeSpinner.setAdapter(eventTypeAdapter);

        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listEventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypes.setAdapter(eventTypeAdapter);


        btnWorkingHoursStart.setOnClickListener(view -> showTimePicker(workingHoursStart));

        // Show TimePickerDialog when clicking the clock icon for end time
        btnWorkingHoursEnd.setOnClickListener(view -> showTimePicker(workingHoursEnd));


        // save service
        btnSaveNewService.setOnClickListener(v -> saveNewService());

        // closing activity
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    // Function to display Material TimePicker
    private void showTimePicker(TextInputEditText editText) {
        // Get the current time as default values
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    // Format and display the selected time
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    editText.setText(selectedTime);
                },
                hour, minute, true // Use 24-hour format
        );

        timePickerDialog.show();
    }

    // Functional interface for callback when time is picked
    private interface TimePickedListener {
        void onTimePicked(int hour, int minute);
    }

    private void setupCategoryAutoComplete() {
        // Set up the adapter for AutoCompleteTextView
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Arrays.asList("Category", "Food", "Music", "Media", "Venue")));
        category.setAdapter(categoryAdapter);

        // Allow user to input custom text
        category.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                category.showDropDown();
            }
        });

        // Handle the selected or entered item
        category.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            category.setText(selectedCategory);
        });
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
        listEventTypes.add(new EventType("Event Type"));
        listEventTypes.add(new EventType("Wedding"));
        listEventTypes.add(new EventType("Party"));
        listEventTypes.add(new EventType("Birthday"));
        listEventTypes.add(new EventType("Conference"));
    }
}
