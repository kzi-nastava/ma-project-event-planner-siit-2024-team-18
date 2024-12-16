package com.example.eventplanner.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.eventplanner.adapters.ServiceListAdapter;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.R;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;
import com.example.eventplanner.viewmodels.ServiceDetailsViewModel;
import com.example.eventplanner.viewmodels.ServiceListViewModel;
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
    private List<EventType> listEventTypes;
    private SwitchCompat isVisible, isAvailable;
    private List<String> selectedEventTypeList = new ArrayList<>();
    private int serviceId, position;
    private ServiceDetailsViewModel serviceViewModel;
    private EventTypeCardViewModel eventTypeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_service, findViewById(R.id.content_frame));

        // Initialize views
        initializeViews();

        // Retrieve data from intent
        serviceId = getIntent().getIntExtra("serviceId", -1);
        position = getIntent().getIntExtra("position", -1);

        loadViewModels();

        // Set button listeners
        setupListeners();
    }

    private void loadViewModels() {
        eventTypeViewModel.fetchEventTypes();
        serviceViewModel.fetchServiceById(serviceId);

        // ViewModel setup
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.getEventTypes().observe(this, eventTypes -> {
            if (eventTypes != null) {
                this.listEventTypes = eventTypes;
            }
        });

        eventTypeViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        serviceViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        serviceViewModel.getService().observe(this, service -> {
            if (service != null) {
                this.service = service;
                populateFields();
            }
        });

        serviceViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        serviceViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
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
    }

    private void populateFields() {
        serviceName.setText(service.getName());
        serviceDescription.setText(service.getDescription());
        servicePrice.setText(String.valueOf(service.getPrice()));
        discount.setText(String.valueOf(service.getDiscount()));
        isVisible.setChecked(service.isVisible());
        isAvailable.setChecked(service.isAvailable());
        location.setText(service.getLocation());
        loadExistingImages();

        if ("MANUAL".equalsIgnoreCase(service.getReservationType())) {
            reservationType.check(R.id.radioButtonManual);
        } else if ("AUTOMATIC".equalsIgnoreCase(service.getReservationType())) {
            reservationType.check(R.id.radioButtonAutomatic);
        }

        specifics.setText(service.getSpecifics());
        if (service.getDuration() != 0) {
            duration.setValue(service.getDuration());
        } else {
            minEngagement.setValue(service.getMinEngagement());
            maxEngagement.setValue(service.getMaxEngagement());
        }
        reservationDeadline.setText(String.valueOf(service.getReservationDeadline()));
        cancellationDeadline.setText(String.valueOf(service.getCancellationDeadline()));

        workingHoursStart.setText(service.getWorkingHoursStart().substring(0, 5));
        workingHoursEnd.setText(service.getWorkingHoursEnd().substring(0, 5));

        initializeEventTypes();

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,new String[]{service.getCategory()});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);
        category.setEnabled(false);
    }

    private void setupListeners() {
        btnSelectPictures.setOnClickListener(v -> openGallery());
        btnClearPictures.setOnClickListener(v -> clearSelectedImages());
        btnWorkingHoursStart.setOnClickListener(view -> showTimePicker(workingHoursStart));
        btnWorkingHoursEnd.setOnClickListener(view -> showTimePicker(workingHoursEnd));
        btnClose.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveEditedService());
    }

    private void initializeEventTypes() {
        List<String> selectedEventTypes = new ArrayList<>(service.getEventTypes());
        setEventTypesSelection(selectedEventTypes);
        setupEventTypesMultiSelect(selectedEventTypes);
    }

    private void setEventTypesSelection(List<String> selectedEventTypes) {
        if (selectedEventTypes != null && !selectedEventTypes.isEmpty()) {
            String selectedTypes = String.join(", ", selectedEventTypes);
            eventTypes.setText(selectedTypes);
        } else {
            eventTypes.setText("No event types selected");
        }
    }

    private void setupEventTypesMultiSelect(List<String> selectedEventTypes) {
        eventTypes.setOnClickListener(view -> {
            String[] eventTypeArray = listEventTypes.stream()
                    .map(EventType::getName)
                    .toArray(String[]::new);

            boolean[] selectedItems = new boolean[eventTypeArray.length];

            for (int i = 0; i < eventTypeArray.length; i++) {
                selectedItems[i] = selectedEventTypes.contains(eventTypeArray[i]);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Event Types")
                    .setMultiChoiceItems(eventTypeArray, selectedItems, (dialog, which, isChecked) -> {
                        if (isChecked) {
                            if (!selectedEventTypes.contains(eventTypeArray[which])) {
                                selectedEventTypes.add(eventTypeArray[which]);
                            }
                        } else {
                            selectedEventTypes.remove(eventTypeArray[which]);
                        }
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
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

    private void loadExistingImages() {
        String[] existingImages = service.getImages();
        if (existingImages != null) {
            for (String imageUrl : existingImages) {
                addImageToContainer(imageUrl, false);
            }
        }
    }

    private void addImageToContainer(String imageUrlOrUri, boolean isNewImage) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);

        if (isNewImage) {
            Uri imageUri = Uri.parse(imageUrlOrUri);
            imageView.setImageURI(imageUri);
            imageUris.add(imageUri);
        } else {
            Glide.with(this)
                    .load(imageUrlOrUri)
                    .into(imageView);
        }

        selectedImagesContainer.addView(imageView);
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
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageToContainer(imageUri.toString(), true);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                addImageToContainer(imageUri.toString(), true);
            }
        }
    }

    // not implemented completely
    // TODO: merge reservation date and time together
    private void saveEditedService() {
        boolean isValid = true;

        // Validate service name
        if (TextUtils.isEmpty(serviceName.getText())) {
            errorServiceName.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceName.setVisibility(View.GONE);
        }

        if (isValid) {
            // Retrieve selected Reservation Type
            int selectedId = reservationType.getCheckedRadioButtonId();
            String selectedReservationType = null;

            if (selectedId == R.id.radioButtonManual) {
                selectedReservationType = "MANUAL";
            } else if (selectedId == R.id.radioButtonAutomatic) {
                selectedReservationType = "AUTOMATIC";
            }

            // Set reservation type to the service object (if applicable)
            service.setReservationType(selectedReservationType);

            // Combine images (new + existing)
            List<String> allImagePaths = new ArrayList<>();

            // Add new image URIs
            for (Uri uri : imageUris) {
                allImagePaths.add(uri.toString());
            }

            // Add existing image URLs
            String[] existingImages = service.getImages();
            if (existingImages != null) {
                allImagePaths.addAll(List.of(existingImages));
            }

            // Update the service's images
            service.setImages(allImagePaths.toArray(new String[0]));

            // Save other service details (this part might involve updating your ViewModel or API)
            service.setName(serviceName.getText().toString());
            service.setDescription(serviceDescription.getText().toString());
            service.setPrice(Double.parseDouble(servicePrice.getText().toString()));
            service.setDiscount(Double.parseDouble(discount.getText().toString()));
            service.setVisible(isVisible.isChecked());
            service.setAvailable(isAvailable.isChecked());
            service.setLocation(location.getText().toString());

            if (!TextUtils.isEmpty(specifics.getText())) {
                service.setSpecifics(specifics.getText().toString());
            }

            if (duration.getValue() > 0) {
                service.setDuration((int) duration.getValue());
            } else {
                service.setMinEngagement((int) minEngagement.getValue());
                service.setMaxEngagement((int) maxEngagement.getValue());
            }

            if (!TextUtils.isEmpty(reservationDeadline.getText())) {
                service.setReservationDeadline(Integer.parseInt(reservationDeadline.getText().toString()));
            }

            if (!TextUtils.isEmpty(cancellationDeadline.getText())) {
                service.setCancellationDeadline(Integer.parseInt(cancellationDeadline.getText().toString()));
            }

            service.setWorkingHoursStart(workingHoursStart.getText().toString());
            service.setWorkingHoursEnd(workingHoursEnd.getText().toString());

            // Save the service via ViewModel or API
//            serviceViewModel.updateService(service);

            Toast.makeText(this, "Service Updated Successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to the list of all services
            Intent intent = new Intent(EditServiceActivity.this, AllServicesActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
