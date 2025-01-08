package com.example.eventplanner.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.models.Product;
import com.example.eventplanner.models.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PricelistViewModel extends ViewModel {
    private Context context;
    private final MutableLiveData<ArrayList<Service>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private MutableLiveData<Boolean> isProductSelected = new MutableLiveData<>(true);

    public LiveData<Boolean> getIsProductSelected() {
        return isProductSelected;
    }

    public void setIsProductSelected(boolean isProductSelected) {
        this.isProductSelected.postValue(isProductSelected);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }


    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public LiveData<ArrayList<Service>> getServices() {
        return servicesLiveData;
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return productsLiveData;
    }

    public void fetchPricelistItems() {
        getServicesBackend();
        getProductsBackend();
    }

    public void editPricelistItemService(int id, Service service) {
        Call<Service> call = ClientUtils.getPricelistService(this.context).editService(id, service);
        call.enqueue(new Callback<Service>() {
            @Override
            public void onResponse(Call<Service> call, Response<Service> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchPricelistItems();
                } else {
                    errorMessage.postValue("Failed to edit service. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Service> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void editPricelistItemProduct(int id, Product product) {
        Call<Product> call = ClientUtils.getPricelistService(this.context).editProduct(id, product);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    success.postValue(true);
                    fetchPricelistItems();
                } else {
                    errorMessage.postValue("Failed to edit product. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }


    public void getProductsBackend() {
        Call<ArrayList<Product>> call2 = ClientUtils.getProductService(this.context).getAllByCreator();
        call2.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call2, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    productsLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch products. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call2, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });

    }

    public void getServicesBackend() {
        Call<ArrayList<Service>> call1 = ClientUtils.getServiceService(this.context).getAllByCreator();
        call1.enqueue(new Callback<ArrayList<Service>>() {
            @Override
            public void onResponse(Call<ArrayList<Service>> call1, Response<ArrayList<Service>> response) {
                if (response.isSuccessful()) {
                    servicesLiveData.postValue(response.body());
                } else {
                    errorMessage.postValue("Failed to fetch services. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Service>> call1, Throwable t) {
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void generatePDF(List<Map<String, Object>> data) {
        getIsProductSelected().observeForever(isProductSelected -> {
            if (isProductSelected != null && isProductSelected) {
                String type = "products";
                generatePDFRequest(type, data);
            } else {
                String type = "services";
                generatePDFRequest(type, data);
            }
        });
    }

    private void generatePDFRequest(String type, List<Map<String, Object>> data) {
        Call<ResponseBody> call = ClientUtils.getPricelistService(this.context).generatePDF(type, data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] pdfContent = response.body().bytes();
                        savePDFToFile(pdfContent, type + "_pricelist.pdf");
                        Toast.makeText(context, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error reading PDF data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to generate PDF. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PDF Error", "Error: " + t.getMessage());
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePDFToFile(byte[] pdfContent, String fileName) {
        try {
            java.io.File file = new java.io.File(context.getExternalFilesDir(null), fileName);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(pdfContent);
            fos.close();

            Log.e("PDF Error", "PDF saved at: " + file.getAbsolutePath());
            Toast.makeText(context, "PDF saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            openPDF(file);

        } catch (Exception e) {
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openPDF(java.io.File file) {
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
