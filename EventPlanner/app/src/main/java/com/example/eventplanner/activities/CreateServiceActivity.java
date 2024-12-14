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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateServiceActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private EditText createServiceName, createServiceDescription, createServicePrice, editSelectTime;
    private ImageView btnClose, btnSelectPictures, btnPickReservationDate, btnClearPictures, btnPickTime;
    private Spinner categorySpinner, eventTypeSpinner;
    private TextInputEditText editReservationDate;
    private TextView errorServiceName;
    private MaterialButton btnSaveNewService;
    private LinearLayout selectedImagesContainer;
    private List<Category> categories;
    private List<EventType> eventTypes;
    private AutoCompleteTextView categoryAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_service, findViewById(R.id.content_frame));

        // initialize views
        createServiceName = findViewById(R.id.createServiceName);
        createServiceDescription = findViewById(R.id.createServiceDescription);
        createServicePrice = findViewById(R.id.createServicePrice);
        btnSaveNewService = findViewById(R.id.btnSaveNewService);
        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        editReservationDate = findViewById(R.id.createReservationDate);
        btnPickReservationDate = findViewById(R.id.btnPickReservationDate);
        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        eventTypeSpinner = findViewById(R.id.spinnerEventTypeCreate);
        errorServiceName = findViewById(R.id.errorServiceName);
        categoryAutoComplete = findViewById(R.id.addServiceCategory);

        // creating dummy data
        loadCategories();
        loadEventTypes();

        setupCategoryAutoComplete();

        // time picker
        editSelectTime = findViewById(R.id.editSelectTime);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnPickTime.setOnClickListener(view -> showTimePicker());

        // date picker
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            editReservationDate.setText(sdf.format(calendar.getTime()));
        };

        btnPickReservationDate.setOnClickListener(v -> {
            new DatePickerDialog(CreateServiceActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

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

        ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        // save service
        btnSaveNewService.setOnClickListener(v -> saveNewService());

        // closing activity
        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupCategoryAutoComplete() {
        // Set up the adapter for AutoCompleteTextView
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(Arrays.asList("Category", "Food", "Music", "Media", "Venue")));
        categoryAutoComplete.setAdapter(categoryAdapter);

        // Allow user to input custom text
        categoryAutoComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                categoryAutoComplete.showDropDown();
            }
        });

        // Handle the selected or entered item
        categoryAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = (String) parent.getItemAtPosition(position);
            categoryAutoComplete.setText(selectedCategory);
        });
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                editSelectTime.setText(selectedTime);
            }, hour, minute, true
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
    private void saveNewService() {
        boolean isValid = true;

        if (TextUtils.isEmpty(createServiceName.getText())) {
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
        eventTypes = new ArrayList<>();
        eventTypes.add(new EventType("Event Type"));
        eventTypes.add(new EventType("Wedding"));
        eventTypes.add(new EventType("Party"));
        eventTypes.add(new EventType("Birthday"));
        eventTypes.add(new EventType("Conference"));
    }
}
