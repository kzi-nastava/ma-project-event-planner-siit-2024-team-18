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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.example.eventplanner.models.Service;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditServiceActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private TextInputEditText serviceName, eventTypes, serviceDescription, servicePrice, discount, location, specifics, reservationDeadline, cancellationDeadline, workingHoursStart, workingHoursEnd;
    private ImageView btnClose, btnSelectPictures, btnClearPictures, btnWorkingHoursStart, btnWorkingHoursEnd;
    private TextView errorServiceName;
    private Button btnSave;
    private Service service;
    private Spinner category;
    private Slider duration, minEngagement, maxEngagement;
    private RadioGroup reservationType;
    private LinearLayout selectedImagesContainer;
    private List<Category> categories;
    private List<EventType> listEventTypes;
    private SwitchCompat isVisible, isAvailable;
    private List<String> selectedEventTypeList = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_service, findViewById(R.id.content_frame));

        // initialize views
        serviceName = findViewById(R.id.editServiceName);
        serviceDescription = findViewById(R.id.editServiceDescription);
        servicePrice = findViewById(R.id.editServicePrice);
        discount = findViewById(R.id.editServiceDiscount);
        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        isVisible = findViewById(R.id.switchVisivility);
        isAvailable = findViewById(R.id.switchAvailability);
        category = findViewById(R.id.categoryDisabled);
        eventTypes = findViewById(R.id.editTextEventTypes);
        location = findViewById(R.id.inputServiceLocation);
        reservationType = findViewById(R.id.radioGroupReservationType);
        specifics = findViewById(R.id.editServiceSpecifics);
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

        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);

        // creating dummy data
        loadCategories();
        loadEventTypes();

        // retrieve data from intent
        service = (Service) getIntent().getParcelableExtra("service");
        position = getIntent().getIntExtra("position", -1);

        // populate fields
        if (service != null) {
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            servicePrice.setText(String.valueOf(service.getPrice()));
            discount.setText(String.valueOf(service.getDiscount()));
            isVisible.setChecked(service.isVisible());
            isAvailable.setChecked(service.isAvailable());
            location.setText(service.getLocation());
            specifics.setText(service.getSpecifics());
            duration.setValue(service.getDuration());
            minEngagement.setValue(service.getMinEngagement());
            maxEngagement.setValue(service.getMaxEngagement());
            reservationDeadline.setText(String.valueOf(service.getReservationDeadline()));
            cancellationDeadline.setText(String.valueOf(service.getCancellationDeadline()));

            workingHoursStart.setText("09:00");
            workingHoursEnd.setText("16:45");

            initializeEventTypes();

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

            // set radio button
            for (int i = 0; i < reservationType.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) reservationType.getChildAt(i);
                if (radioButton.getText().toString().equalsIgnoreCase(service.getReservationType())) {
                    radioButton.setChecked(true);
                    break;
                }
            }

            btnWorkingHoursStart.setOnClickListener(view -> showTimePicker(workingHoursStart));
            btnWorkingHoursEnd.setOnClickListener(view -> showTimePicker(workingHoursEnd));

            ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category.setAdapter(categoryAdapter);

            setSpinnerSelection(category, service.getCategory());

            category.setEnabled(false);
        }

        // closing activity
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            finish();
        });

        btnSave.setOnClickListener(v -> saveEditedService());
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void initializeEventTypes() {
        // Step 1: Load all available event types
        loadEventTypes(); // Populates listEventTypes

        // Step 2: Get pre-selected event types
        List<String> selectedEventTypes = service.getEventTypes();

        // Step 3: Populate input field with pre-selected types
        setEventTypesSelection(selectedEventTypes);

        // Step 4: Set up the multi-select dialog
        setupEventTypesMultiSelect(listEventTypes, selectedEventTypes);
    }

    private void setEventTypesSelection(List<String> selectedEventTypes) {
        if (selectedEventTypes != null && !selectedEventTypes.isEmpty()) {
            String selectedTypes = String.join(", ", selectedEventTypes);
            eventTypes.setText(selectedTypes); // Update the input field
        } else {
            eventTypes.setText("No event types selected");
        }
    }

    private void setupEventTypesMultiSelect(List<EventType> allEventTypes, List<String> selectedEventTypes) {
        eventTypes.setOnClickListener(view -> {
            // Convert event types to a string array
            String[] eventTypeArray = allEventTypes.stream()
                    .map(EventType::getType)
                    .toArray(String[]::new);

            // Boolean array to track selected items
            boolean[] selectedItems = new boolean[eventTypeArray.length];

            // Pre-check items that match selectedEventTypes
            for (int i = 0; i < eventTypeArray.length; i++) {
                selectedItems[i] = selectedEventTypes.contains(eventTypeArray[i]);
            }

            // Multi-select dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Event Types")
                    .setMultiChoiceItems(eventTypeArray, selectedItems, (dialog, which, isChecked) -> {
                        if (isChecked) {
                            if (!selectedEventTypes.contains(eventTypeArray[which])) {
                                selectedEventTypes.add(eventTypeArray[which]); // Add to selected if not already present
                            }
                        } else {
                            selectedEventTypes.remove(eventTypeArray[which]); // Remove from selected
                        }
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Update the input field with selected items
                        setEventTypesSelection(selectedEventTypes);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
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
    private void saveEditedService() {

        boolean isValid = true;

        if (TextUtils.isEmpty(serviceName.getText())) {
            errorServiceName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceName.setVisibility(View.GONE);
        }

        if (isValid) {
            Toast.makeText(this, "Service Updated Successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EditServiceActivity.this, AllServicesActivity.class);
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
