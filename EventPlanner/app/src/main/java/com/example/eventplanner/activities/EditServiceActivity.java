package com.example.eventplanner.activities;

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

import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.models.ServiceCategory;
import com.google.android.material.slider.Slider;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditServiceActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private EditText editServiceName, editServiceDescription, editServicePrice, editServiceSpecifics, editServiceDiscount, editReservationDate, editCancellationDateDate, editReservationTime;
    private SwitchCompat switchAvailability;
    private Button btnSave;
    private ImageView btnClose, btnPickTime, btnSelectPictures, btnClearPictures;
    private Service service;
    private Spinner editServiceCategory, editServiceEventType;
    private Slider sliderDuration;
    private TextView errorServiceName;
    private RadioGroup radioGroupReservationType;
    private LinearLayout selectedImagesContainer;
    private List<ServiceCategory> categories;
    private List<EventType> eventTypes;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_service, findViewById(R.id.content_frame));

        // initialize views
        editServiceName = findViewById(R.id.editServiceName);
        editServiceSpecifics = findViewById(R.id.editServiceSpecifics);
        editServiceDescription = findViewById(R.id.editServiceDescription);
        editServicePrice = findViewById(R.id.editServicePrice);
        editServiceDiscount = findViewById(R.id.editServiceDiscount);
        editServiceCategory = findViewById(R.id.editServiceCategory);
        editServiceEventType = findViewById(R.id.editServiceEventType);
        switchAvailability = findViewById(R.id.switchAvailability);
        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);
        editReservationDate = findViewById(R.id.editReservationDate);
        editCancellationDateDate = findViewById(R.id.editCancellationDate);
        sliderDuration = findViewById(R.id.sliderDuration);
        radioGroupReservationType = findViewById(R.id.radioGroupReservationType);
        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        errorServiceName = findViewById(R.id.errorServiceName);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);

        // creating dummy data
        loadCategories();
        loadEventTypes();

        // time picker
        editReservationTime = findViewById(R.id.editSelectTime);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnPickTime.setOnClickListener(view -> showTimePicker());

        // retrieve data from intent
        service = (Service) getIntent().getSerializableExtra("service");
        position = getIntent().getIntExtra("position", -1);

        // populate fields
        if (service != null) {
            editServiceName.setText(service.getName());
            editServiceSpecifics.setText(service.getSpecifics());
            editServiceDescription.setText(R.string.lorem_ipsum);
            editServicePrice.setText(String.valueOf(service.getPrice()));
            editServiceDiscount.setText(String.valueOf(service.getDiscount()));
            switchAvailability.setChecked(service.isAvailable());
            sliderDuration.setValue(service.getDuration());

            // filling spinners with data
            ArrayAdapter<ServiceCategory> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editServiceCategory.setAdapter(categoryAdapter);

            ArrayAdapter<EventType> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
            eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            editServiceEventType.setAdapter(eventTypeAdapter);

            setSpinnerSelection(editServiceCategory, service.getCategory());
            setSpinnerSelection(editServiceEventType, service.getEventType());

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

            // set dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            editReservationDate.setText(service.getReservationDate() != null
                    ? service.getReservationDate().toLocalDate().format(dateFormatter) : "");
            editCancellationDateDate.setText(service.getCancellationDate() != null
                    ? service.getCancellationDate().toLocalDate().format(dateFormatter) : "");

            // set time
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedTime = service.getReservationDate().toLocalTime().format(timeFormatter);
            editReservationTime.setText(formattedTime);

            // set radio button
            for (int i = 0; i < radioGroupReservationType.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radioGroupReservationType.getChildAt(i);
                if (radioButton.getText().toString().equalsIgnoreCase(service.getReservationType())) {
                    radioButton.setChecked(true);
                    break;
                }
            }
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

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (TimePicker view, int hourOfDay, int minuteOfHour) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    editReservationTime.setText(selectedTime);
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
    private void saveEditedService() {

        boolean isValid = true;

        if (TextUtils.isEmpty(editServiceName.getText())) {
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
        categories.add(new ServiceCategory("Category"));
        categories.add(new ServiceCategory("Food"));
        categories.add(new ServiceCategory("Music"));
        categories.add(new ServiceCategory("Media"));
        categories.add(new ServiceCategory("Venue"));
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
