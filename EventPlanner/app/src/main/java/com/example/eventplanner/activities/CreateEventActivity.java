package com.example.eventplanner.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Category;
import com.example.eventplanner.models.EventType;
import com.example.eventplanner.models.Event;
import com.example.eventplanner.models.LocationAutocompleteResponseDTO;
import com.example.eventplanner.models.LocationDetailResponseDTO;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;
import com.example.eventplanner.viewmodels.EventDetailsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private List<EventType> listEventTypes = new ArrayList<>();
    private TextInputEditText eventName, eventDescription, maxParticipants, eventDate, eventTime;
    private AutoCompleteTextView location;
    private ImageView btnSelectPictures, btnClearPictures, btnPickEventDate, btnPickEventTime, btnClose;
    private MaterialButton btnSaveNewEvent;
    private TextView errorEventName, errorEventDescription, errorEventType, errorEventLocation, errorEventMaxParticipants, errorEventImages, errorEventPrivacyType, errorEventDate, errorEventTime;
    private LinearLayout selectedImagesContainer;
    private RadioGroup privacyType;
    private Spinner eventTypes;
    private EventTypeCardViewModel eventTypeViewModel;
    private EventDetailsViewModel eventDetailsViewModel;
    private ArrayAdapter<String> locationAdapter;
    private List<LocationAutocompleteResponseDTO> locationSuggestions = new ArrayList<>();
    private LocationDetailResponseDTO selectedLocationDetails = null;

    private Call<List<LocationAutocompleteResponseDTO>> locationCall;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_create_event, findViewById(R.id.content_frame));

        initializeViews();
        loadViewModels();
        setupClickListeners();
    }

    private void initializeViews() {
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.setContext(this);
        eventDetailsViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        eventDetailsViewModel.setContext(this);

        eventName = findViewById(R.id.createEventName);
        eventDescription = findViewById(R.id.createEventDescription);
        maxParticipants = findViewById(R.id.inputEventMaxParticipants);
        eventDate = findViewById(R.id.editEventDate);
        eventTime = findViewById(R.id.editEventTime);

        location = findViewById(R.id.inputEventLocation);
        locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        location.setAdapter(locationAdapter);
        location.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                location.showDropDown();
            }
        });
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if (locationRunnable != null) {
                    handler.removeCallbacks(locationRunnable);
                }

                if (!query.isEmpty() && query.length() >= 2) {
                    locationRunnable = () -> fetchLocationSuggestions(query);
                    handler.postDelayed(locationRunnable, 400);
                } else {
                    locationAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        location.setOnItemClickListener((parent, view, position, id) -> {
            String selectedDisplayName = (String) parent.getItemAtPosition(position);
            location.setText(selectedDisplayName);

            ClientUtils.getLocationService(this).getLocationDetails(selectedDisplayName)
                    .enqueue(new Callback<LocationDetailResponseDTO>() {
                        @Override
                        public void onResponse(Call<LocationDetailResponseDTO> call, Response<LocationDetailResponseDTO> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                selectedLocationDetails = response.body();
                            }
                        }

                        @Override
                        public void onFailure(Call<LocationDetailResponseDTO> call, Throwable t) {
                            Toast.makeText(CreateEventActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        eventTypes = findViewById(R.id.addEventType);

        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        btnPickEventDate = findViewById(R.id.btnPickEventDate);
        btnPickEventTime = findViewById(R.id.btnPickEventTime);
        btnClose = findViewById(R.id.btnClose);
        btnSaveNewEvent = findViewById(R.id.btnSaveNewEvent);

        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);

        errorEventName = findViewById(R.id.errorEventName);
        errorEventDescription = findViewById(R.id.errorEventDescription);
        errorEventLocation = findViewById(R.id.errorEventLocation);
        errorEventImages = findViewById(R.id.errorEventImages);
        errorEventType = findViewById(R.id.errorEventType);
        errorEventMaxParticipants = findViewById(R.id.errorEventMaxParticipants);
        errorEventPrivacyType = findViewById(R.id.errorEventPrivacyType);
        errorEventDate = findViewById(R.id.errorEventDate);
        errorEventTime = findViewById(R.id.errorEventTime);

        privacyType = findViewById(R.id.radioGroupPrivacyType);
    }

    private void fetchLocationSuggestions(String query) {
        if (locationCall != null) {
            locationCall.cancel();
        }

        locationCall = ClientUtils.getLocationService(this).getAutocompleteLocations(query);
        locationCall.enqueue(new Callback<List<LocationAutocompleteResponseDTO>>() {
            @Override
            public void onResponse(Call<List<LocationAutocompleteResponseDTO>> call, Response<List<LocationAutocompleteResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    locationSuggestions = response.body();
                    List<String> names = new ArrayList<>();
                    for (LocationAutocompleteResponseDTO suggestion : locationSuggestions) {
                        names.add(suggestion.getDisplayName());
                    }
                    locationAdapter.clear();
                    locationAdapter.addAll(names);
                    locationAdapter.notifyDataSetChanged();
                    location.showDropDown();
                }
            }

            @Override
            public void onFailure(Call<List<LocationAutocompleteResponseDTO>> call, Throwable t) {
                if (!call.isCanceled()) {
                    Toast.makeText(CreateEventActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadViewModels() {
        eventTypeViewModel.fetchEventTypes();

        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.getEventTypes().observe(this, eventTypes -> {
            if (eventTypes != null) {
                this.listEventTypes = eventTypes;
                setupEventTypeSpinner();
            }
        });

        eventTypeViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        eventDetailsViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Event Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        eventDetailsViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectPictures.setOnClickListener(v -> openGallery());
        btnClearPictures.setOnClickListener(v -> clearSelectedImages());
        btnPickEventDate.setOnClickListener(v -> showDatePicker(eventDate));
        btnPickEventTime.setOnClickListener(v -> showTimePicker(eventTime));
        btnClose.setOnClickListener(v -> finish());
        btnSaveNewEvent.setOnClickListener(v -> saveNewEvent());
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(selectedDate);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            editText.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void setupEventTypeSpinner() {
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getEventTypeNames());
        eventTypes.setAdapter(eventTypeAdapter);
//        categories.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                categories.showDropDown();
//            }
//        });
    }

    private List<String> getEventTypeNames() {
        List<String> names = new ArrayList<>();
        for (EventType eventType : listEventTypes) {
            names.add(eventType.getName());
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

    private void saveNewEvent() {
        if (!validateInputs()) {
            Toast.makeText(this, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
            return;
        }

        updateEvent();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (!validateField(eventName, errorEventName)) {
            isValid = false;
        }

        if (!validateField(eventDescription, errorEventDescription)) {
            isValid = false;
        }

        if (!validateField(maxParticipants, errorEventMaxParticipants)) {
            isValid = false;
        }

        String input = maxParticipants.getText().toString().trim();
        if (!input.matches("^[1-9][0-9]*$")) {
            isValid = false;
            errorEventMaxParticipants.setVisibility(View.VISIBLE);
        } else {
            errorEventMaxParticipants.setVisibility(View.GONE);
        }

        if (eventTypes.getSelectedItem().toString().trim().isEmpty()) {
            isValid = false;
            errorEventType.setVisibility(View.VISIBLE);
        } else {
            errorEventType.setVisibility(View.GONE);
        }

        if (!validateField(location, errorEventLocation) || selectedLocationDetails == null) {
            isValid = false;
            errorEventLocation.setVisibility(View.VISIBLE);
        } else {
            errorEventLocation.setVisibility(View.GONE);
        }

        if (imageUris.isEmpty()) {
            errorEventImages.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorEventImages.setVisibility(View.GONE);
        }

        if (getSelectedPrivacyType() == null) {
            errorEventPrivacyType.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorEventPrivacyType.setVisibility(View.GONE);
        }

        if (!validateField(eventDate, errorEventDate)) {
            isValid = false;
        }

        if (!validateField(eventTime, errorEventTime)) {
            isValid = false;
        }

        return isValid;
    }

    private boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    private String getSelectedPrivacyType() {
        int selectedId = privacyType.getCheckedRadioButtonId();
        if (selectedId == R.id.radioPrivate) {
            return "PRIVATE";
        } else if (selectedId == R.id.radioPublic) {
            return "PUBLIC";
        }
        return null;
    }

    private String getCombinedDateTimeForBackend() {
        if (!eventDate.getText().toString().trim().isEmpty() && !eventTime.getText().toString().trim().isEmpty()) {
            return eventDate.getText().toString().trim() + "T" + eventTime.getText().toString().trim() + ":00";
        } else {
            return "";
        }
    }

    private void updateEvent() {
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), eventName.getText().toString().trim());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), eventDescription.getText().toString().trim());
        RequestBody maxParticipantsBody = RequestBody.create(MediaType.parse("text/plain"), maxParticipants.getText().toString().trim());
        RequestBody eventTypeBody = RequestBody.create(MediaType.parse("text/plain"), eventTypes.getSelectedItem().toString().trim());
        RequestBody locationNameBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getName());
        RequestBody cityBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getCity());
        RequestBody countryBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getCountry());
        RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getLatitude().toString());
        RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getLongitude().toString());
        RequestBody privacyTypeBody = RequestBody.create(MediaType.parse("text/plain"), getSelectedPrivacyType());
        RequestBody dateTimeBody = RequestBody.create(MediaType.parse("text/plain"), getCombinedDateTimeForBackend());

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : imageUris) {
            try {
                if (uri.toString().startsWith("content://")) {
                    File file = new File(getRealPathFromURI(uri));
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), fileBody);
                    imageParts.add(imagePart);
                } else if (uri.toString().startsWith("data:image")) {
                    File file = convertBase64ToFile(uri.toString());
                    if (file != null) {
                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), fileBody);
                        imageParts.add(imagePart);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        eventDetailsViewModel.addNewEvent(nameBody, descriptionBody, maxParticipantsBody, eventTypeBody, locationNameBody, cityBody, countryBody, latitudeBody, longitudeBody, privacyTypeBody, dateTimeBody, imageParts);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        String fileName = cursor.getString(nameIndex);
        cursor.close();

        return result != null ? result : fileName;
    }

    private File convertBase64ToFile(String base64String) {
        try {
            String base64Image = base64String.split(",")[1];
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

            String fileName = "image_" + System.currentTimeMillis() + ".png";
            File file = new File(getCacheDir(), fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(decodedBytes);
            }

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
