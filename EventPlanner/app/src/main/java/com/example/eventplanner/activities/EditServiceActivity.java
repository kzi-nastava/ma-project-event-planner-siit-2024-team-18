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
    private List<EventType> listEventTypes = new ArrayList<>();
    List<String> selectedEventTypes = new ArrayList<>();
    private TextInputEditText serviceName, eventTypes, serviceDescription, servicePrice, discount, location, specifics, reservationDeadline, cancellationDeadline, workingHoursStart, workingHoursEnd;
    private ImageView btnClose, btnSelectPictures, btnClearPictures, btnWorkingHoursStart, btnWorkingHoursEnd;
    private TextView errorServiceName, errorServiceDescription, errorServiceSpecifics, errorServiceCategory, errorServiceEventTypes, errorServiceLocation, errorServiceReservationDeadline, errorServiceCancellationDeadline, errorServicePrice, errorServiceDiscount, errorServiceImages, errorServiceReservationType, errorServiceStartTime, errorServiceEndTime;
    private Button btnSave;
    private Service service;
    private Spinner category;
    private Slider duration, minEngagement, maxEngagement;
    private RadioGroup reservationType;
    private LinearLayout selectedImagesContainer;
    private SwitchCompat isVisible, isAvailable;
    private int serviceId;
    private ServiceDetailsViewModel serviceDetailsViewModel;
    private EventTypeCardViewModel eventTypeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_service, findViewById(R.id.content_frame));

        initializeViews();

        serviceId = getIntent().getIntExtra("serviceId", -1);

        loadViewModels();
        setupListeners();
    }

    private void loadViewModels() {
        eventTypeViewModel.fetchEventTypes();
        serviceDetailsViewModel.fetchServiceById(serviceId);

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

        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        serviceDetailsViewModel.setContext(this);
        serviceDetailsViewModel.getService().observe(this, service -> {
            if (service != null) {
                this.service = service;
                populateFields();
            }
        });

        serviceDetailsViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Service Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        serviceDetailsViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        serviceDetailsViewModel.setContext(this);
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);

        serviceName = findViewById(R.id.editServiceName);
        serviceDescription = findViewById(R.id.editServiceDescription);
        servicePrice = findViewById(R.id.editServicePrice);
        discount = findViewById(R.id.editServiceDiscount);
        location = findViewById(R.id.inputServiceLocation);
        specifics = findViewById(R.id.editServiceSpecifics);
        reservationDeadline = findViewById(R.id.inputServiceReservationDeadline);
        cancellationDeadline = findViewById(R.id.inputServiceCancellationDeadline);
        workingHoursStart = findViewById(R.id.editStartTime);
        workingHoursEnd = findViewById(R.id.editEndTime);
        category = findViewById(R.id.categoryDisabled);
        eventTypes = findViewById(R.id.editTextEventTypes);

        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        btnWorkingHoursStart = findViewById(R.id.btnPickStartTime);
        btnWorkingHoursEnd = findViewById(R.id.btnPickEndTime);
        btnClose = findViewById(R.id.btnClose);
        btnSave = findViewById(R.id.btnSave);

        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);

        errorServiceName = findViewById(R.id.errorServiceName);
        errorServiceDescription = findViewById(R.id.errorServiceDescription);
        errorServiceSpecifics = findViewById(R.id.errorServiceSpecifics);
        errorServiceCategory = findViewById(R.id.errorServiceCategory);
        errorServiceEventTypes = findViewById(R.id.errorServiceEventTypes);
        errorServiceLocation = findViewById(R.id.errorServiceLocation);
        errorServiceReservationDeadline = findViewById(R.id.errorServiceReservationDeadline);
        errorServiceCancellationDeadline = findViewById(R.id.errorServiceCancellationDeadline);
        errorServicePrice = findViewById(R.id.errorServicePrice);
        errorServiceDiscount = findViewById(R.id.errorServiceDiscount);
        errorServiceImages = findViewById(R.id.errorServiceImages);
        errorServiceReservationType = findViewById(R.id.errorServiceReservationType);
        errorServiceStartTime = findViewById(R.id.errorServiceStartTime);
        errorServiceEndTime = findViewById(R.id.errorServiceEndTime);

        duration = findViewById(R.id.sliderDuration);
        minEngagement = findViewById(R.id.sliderFrom);
        maxEngagement = findViewById(R.id.sliderTo);

        reservationType = findViewById(R.id.radioGroupReservationType);
        isVisible = findViewById(R.id.switchVisivility);
        isAvailable = findViewById(R.id.switchAvailability);
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
            reservationType.check(R.id.radioAuto);
        } else if ("AUTOMATIC".equalsIgnoreCase(service.getReservationType())) {
            reservationType.check(R.id.radioManual);
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
        selectedEventTypes = new ArrayList<>(service.getEventTypes());
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
                Uri imageUri = Uri.parse(imageUrl);
                imageUris.add(imageUri);
                addImageToContainer(imageUri.toString(), false);
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

    private void saveEditedService() {
        if (!validateInputs()) {
            Toast.makeText(this, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
            return;
        }

        serviceDetailsViewModel.editService(serviceId, updateService());
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (!validateField(serviceName, errorServiceName)) {
            isValid = false;
        }

        if (!validateField(serviceDescription, errorServiceDescription)) {
            isValid = false;
        }

        if (!validateField(specifics, errorServiceSpecifics)) {
            isValid = false;
        }

        if (selectedEventTypes.isEmpty()) {
            errorServiceEventTypes.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceEventTypes.setVisibility(View.GONE);
        }

        if (!validateField(location, errorServiceLocation)) {
            isValid = false;
        }

        if (!validateField(reservationDeadline, errorServiceReservationDeadline)) {
            isValid = false;
        }

        if (!validateField(cancellationDeadline, errorServiceCancellationDeadline)) {
            isValid = false;
        }

        if (!validateField(servicePrice, errorServicePrice)) {
            isValid = false;
        }

        if (!validateField(discount, errorServiceDiscount)) {
            isValid = false;
        }

        if (imageUris.isEmpty()) {
            errorServiceImages.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceImages.setVisibility(View.GONE);
        }

        if (getSelectedReservationType() == null) {
            errorServiceReservationType.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorServiceReservationType.setVisibility(View.GONE);
        }

        if (!validateField(workingHoursStart, errorServiceStartTime)) {
            isValid = false;
        }

        if (!validateField(workingHoursEnd, errorServiceEndTime)) {
            isValid = false;
        }

        return isValid;
    }

    private boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    private String getSelectedReservationType() {
        int selectedId = reservationType.getCheckedRadioButtonId();
        if (selectedId == R.id.radioManual) {
            return "MANUAL";
        } else if (selectedId == R.id.radioAuto) {
            return "AUTOMATIC";
        }
        return null;
    }

    private Service updateService() {
        service.setName(serviceName.getText().toString());
        service.setDescription(serviceDescription.getText().toString());
        service.setSpecifics(specifics.getText().toString());
        service.setCategory(category.getSelectedItem().toString());
        service.setEventTypes(selectedEventTypes.toArray(new String[0]));
        service.setLocation(location.getText().toString());
        service.setReservationDeadline(Integer.parseInt(reservationDeadline.getText().toString()));
        service.setCancellationDeadline(Integer.parseInt(cancellationDeadline.getText().toString()));
        service.setPrice(Double.parseDouble(servicePrice.getText().toString()));
        service.setDiscount(Double.parseDouble(discount.getText().toString()));

        List<String> allImagePaths = new ArrayList<>();
        imageUris.forEach(uri -> allImagePaths.add(uri.toString()));
        if (service.getImages() != null) {
            allImagePaths.addAll(List.of(service.getImages()));
        }
        service.setImages(allImagePaths.toArray(new String[0]));

        service.setDuration((int) duration.getValue());
        service.setMinEngagement((int) minEngagement.getValue());
        service.setMaxEngagement((int) maxEngagement.getValue());
        service.setVisible(isVisible.isChecked());
        service.setAvailable(isAvailable.isChecked());
        service.setReservationType(getSelectedReservationType());
        service.setWorkingHoursStart(workingHoursStart.getText().toString());
        service.setWorkingHoursEnd(workingHoursEnd.getText().toString());

        return service;
    }
}
