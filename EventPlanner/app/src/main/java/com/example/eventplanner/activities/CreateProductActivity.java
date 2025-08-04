package com.example.eventplanner.activities;

import android.app.AlertDialog;
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
import com.example.eventplanner.models.LocationAutocompleteResponseDTO;
import com.example.eventplanner.models.LocationDetailResponseDTO;
import com.example.eventplanner.viewmodels.CategoryCardViewModel;
import com.example.eventplanner.viewmodels.EventTypeCardViewModel;
import com.example.eventplanner.viewmodels.ProductDetailsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateProductActivity extends BaseActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private List<Category> listCategories = new ArrayList<>();
    private List<EventType> listEventTypes = new ArrayList<>();
    private List<String> selectedEventTypes = new ArrayList<>();
    private TextInputEditText productName, eventTypes, productDescription, productPrice, discount;
    AutoCompleteTextView location;
    private ImageView btnSelectPictures, btnClearPictures, btnClose;
    private MaterialButton btnSaveNewProduct;
    private TextView errorProductName, errorProductDescription, errorProductCategory, errorProductEventTypes, errorProductLocation, errorProductPrice, errorProductDiscount, errorProductImages;
    private LinearLayout selectedImagesContainer;
    private SwitchCompat isVisible, isAvailable;
    private AutoCompleteTextView categories;
    private EventTypeCardViewModel eventTypeViewModel;
    private CategoryCardViewModel categoryViewModel;
    private ProductDetailsViewModel productDetailsViewModel;
    private ArrayAdapter<String> locationAdapter;
    private List<LocationAutocompleteResponseDTO> locationSuggestions = new ArrayList<>();
    private LocationDetailResponseDTO selectedLocationDetails = null;

    private Call<List<LocationAutocompleteResponseDTO>> locationCall;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_create_product, findViewById(R.id.content_frame));

        initializeViews();
        loadViewModels();
        setupClickListeners();
    }

    private void initializeViews() {
        eventTypeViewModel = new ViewModelProvider(this).get(EventTypeCardViewModel.class);
        eventTypeViewModel.setContext(this);
        categoryViewModel = new ViewModelProvider(this).get(CategoryCardViewModel.class);
        categoryViewModel.setContext(this);
        productDetailsViewModel = new ViewModelProvider(this).get(ProductDetailsViewModel.class);
        productDetailsViewModel.setContext(this);

        productName = findViewById(R.id.createProductName);
        productDescription = findViewById(R.id.createProductDescription);
        productPrice = findViewById(R.id.createProductPrice);
        discount = findViewById(R.id.createProductDiscount);

        location = findViewById(R.id.inputProductLocation);
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
                            Toast.makeText(CreateProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        categories = findViewById(R.id.addProductCategory);
        eventTypes = findViewById(R.id.editTextEventTypes);

        btnSelectPictures = findViewById(R.id.btnSelectPictures);
        btnClearPictures = findViewById(R.id.btnClearPictures);
        btnClose = findViewById(R.id.btnClose);
        btnSaveNewProduct = findViewById(R.id.btnSaveNewProduct);

        selectedImagesContainer = findViewById(R.id.selectedImagesContainer);

        errorProductName = findViewById(R.id.errorProductName);
        errorProductDescription = findViewById(R.id.errorProductDescription);
        errorProductCategory = findViewById(R.id.errorProductCategory);
        errorProductEventTypes = findViewById(R.id.errorProductEventTypes);
        errorProductLocation = findViewById(R.id.errorProductLocation);
        errorProductPrice = findViewById(R.id.errorProductPrice);
        errorProductDiscount = findViewById(R.id.errorProductDiscount);
        errorProductImages = findViewById(R.id.errorProductImages);

        isVisible = findViewById(R.id.switchVisibility);
        isAvailable = findViewById(R.id.switchAvailability);
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
                    Toast.makeText(CreateProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        productDetailsViewModel.getSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Product Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        productDetailsViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnSelectPictures.setOnClickListener(v -> openGallery());
        btnClearPictures.setOnClickListener(v -> clearSelectedImages());
        btnClose.setOnClickListener(v -> finish());
        btnSaveNewProduct.setOnClickListener(v -> saveNewProduct());
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

    private void saveNewProduct() {
        if (!validateInputs()) {
            Toast.makeText(this, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProduct();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (!validateField(productName, errorProductName)) {
            isValid = false;
        }

        if (!validateField(productDescription, errorProductDescription)) {
            isValid = false;
        }

        if (!validateField(categories, errorProductCategory)) {
            isValid = false;
        }

        if (selectedEventTypes.isEmpty()) {
            errorProductEventTypes.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorProductEventTypes.setVisibility(View.GONE);
        }

        if (!validateField(location, errorProductLocation) || selectedLocationDetails == null) {
            isValid = false;
            errorProductLocation.setVisibility(View.VISIBLE);
        } else {
            errorProductLocation.setVisibility(View.GONE);
        }

        if (!validateField(productPrice, errorProductPrice)) {
            isValid = false;
        }

        if (!validateField(discount, errorProductDiscount)) {
            isValid = false;
        }

        if (imageUris.isEmpty()) {
            errorProductImages.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            errorProductImages.setVisibility(View.GONE);
        }

        return isValid;
    }

    private boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    private void updateProduct() {
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), productName.getText().toString().trim());
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), productDescription.getText().toString().trim());
        RequestBody categoryBody = RequestBody.create(MediaType.parse("text/plain"), categories.getText().toString().trim());
        RequestBody eventTypesBody = RequestBody.create(MediaType.parse("text/plain"), TextUtils.join(",", selectedEventTypes));
        RequestBody locationBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getName());
        RequestBody cityBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getCity());
        RequestBody countryBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getCountry());
        RequestBody latitudeBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getLatitude().toString());
        RequestBody longitudeBody = RequestBody.create(MediaType.parse("text/plain"), selectedLocationDetails.getLongitude().toString());
        RequestBody priceBody = RequestBody.create(MediaType.parse("text/plain"), productPrice.getText().toString().trim());
        RequestBody discountBody = RequestBody.create(MediaType.parse("text/plain"), discount.getText().toString().trim());
        RequestBody visibleBody = RequestBody.create(MediaType.parse("text/plain"), isVisible.isChecked() ? "1" : "0");
        RequestBody availableBody = RequestBody.create(MediaType.parse("text/plain"), isAvailable.isChecked() ? "1" : "0");

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

        productDetailsViewModel.addNewProduct(nameBody, descriptionBody, categoryBody, eventTypesBody, locationBody, cityBody, countryBody, latitudeBody, longitudeBody,
                priceBody, discountBody, visibleBody, availableBody, imageParts);
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
