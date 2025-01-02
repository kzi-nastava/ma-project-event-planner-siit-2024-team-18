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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.models.Service;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;
import com.example.eventplanner.viewmodels.ServiceDetailsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateServiceActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private List<Category> listCategories = new ArrayList<>();
    private List<EventType> listEventTypes = new ArrayList<>();
    private List<String> selectedEventTypes = new ArrayList<>();
    private TextInputEditText serviceName, eventTypes, serviceDescription, servicePrice, discount, location, specifics, reservationDeadline, cancellationDeadline, workingHoursStart, workingHoursEnd;
    private ImageView btnSelectPictures, btnClearPictures, btnWorkingHoursStart, btnWorkingHoursEnd, btnClose;
    private MaterialButton btnSaveNewService;
    private TextView errorServiceName, errorServiceDescription, errorServiceSpecifics, errorServiceCategory, errorServiceEventTypes, errorServiceLocation, errorServiceReservationDeadline, errorServiceCancellationDeadline, errorServicePrice, errorServiceDiscount, errorServiceImages, errorServiceReservationType, errorServiceStartTime, errorServiceEndTime;
    private LinearLayout selectedImagesContainer;
    private Slider duration, minEngagement, maxEngagement;
    private RadioGroup reservationType;
    private SwitchCompat isVisible, isAvailable;
    private AutoCompleteTextView categories;
    private Service service;
    private EventTypeCardViewModel eventTypeViewModel;
    private CategoryCardViewModel categoryViewModel;
    private ServiceDetailsViewModel serviceDetailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_add_service, findViewById(R.id.content_frame));
        service = new Service();

        initializeViews();
        loadViewModels();
        setupClickListeners();
    }

    private void initializeViews() {
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.setContext(this);
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryViewModel.setContext(this);
        serviceDetailsViewModel = new ViewModelProvider(this).get(ServiceDetailsViewModel.class);
        serviceDetailsViewModel.setContext(this);

        serviceName = findViewById(R.id.createServiceName);
        serviceDescription = findViewById(R.id.createServiceDescription);
        servicePrice = findViewById(R.id.createServicePrice);
        discount = findViewById(R.id.createServiceDiscount);
        location = findViewById(R.id.inputServiceLocation);
        specifics = findViewById(R.id.inputServiceSpecifics);
        reservationDeadline = findViewById(R.id.inputServiceReservationDeadline);
        cancellationDeadline = findViewById(R.id.inputServiceCancellationDeadline);
        workingHoursStart = findViewById(R.id.editStartTime);
        workingHoursEnd = findViewById(R.id.editEndTime);
        categories = findViewById(R.id.addServiceCategory);
        eventTypes = findViewById(R.id.editTextEventTypes);

        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        btnWorkingHoursStart = findViewById(R.id.btnPickStartTime);
        btnWorkingHoursEnd = findViewById(R.id.btnPickEndTime);
        btnClose = findViewById(R.id.btnClose);
        btnSaveNewService = findViewById(R.id.btnSaveNewService);

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

    private void loadViewModels() {
        eventTypeViewModel.fetchEventTypes();
        categoryViewModel.fetchCategories();

        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.getEventTypes().observe(this, eventTypes -> {
            if (eventTypes != null) {
                this.listEventTypes = eventTypes;
                setupEventTypesMultiSelect();
            }
        });

        eventTypeViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                this.listCategories = categories;
                setupCategoryAutoComplete();
            }
        });

        categoryViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        serviceDetailsViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Service Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        serviceDetailsViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectPictures.setOnClickListener(v -> openGallery());
        btnClearPictures.setOnClickListener(v -> clearSelectedImages());
        btnWorkingHoursStart.setOnClickListener(v -> showTimePicker(workingHoursStart));
        btnWorkingHoursEnd.setOnClickListener(v -> showTimePicker(workingHoursEnd));
        btnClose.setOnClickListener(v -> finish());
        btnSaveNewService.setOnClickListener(v -> saveNewService());
    }

    private void setupEventTypesMultiSelect() {
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
                        if (selectedEventTypes != null && !selectedEventTypes.isEmpty()) {
                            String selectedTypes = String.join(", ", selectedEventTypes);
                            eventTypes.setText(selectedTypes);
                        } else {
                            eventTypes.setText("No event types selected");
                        }                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            editText.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void setupCategoryAutoComplete() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getCategoryNames());
        categories.setAdapter(categoryAdapter);
        categories.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                categories.showDropDown();
            }
        });
    }

    private List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category category : listCategories) {
            names.add(category.getName());
        }
        return names;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST);
    }

    private void clearSelectedImages() {
        imageUris.clear();
        selectedImagesContainer.removeAllViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    addImageToContainer(imageUri);
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                addImageToContainer(imageUri);
                imageUris.add(imageUri);
            }
        }
    }

    private void addImageToContainer(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setImageURI(imageUri);
        selectedImagesContainer.addView(imageView);
    }

    private void saveNewService() {
        if (!validateInputs()) {
            Toast.makeText(this, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
            return;
        }

        serviceDetailsViewModel.addNewService(updateService());
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

        if (!validateField(categories, errorServiceCategory)) {
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
        service.setCategory(categories.getText().toString());
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
