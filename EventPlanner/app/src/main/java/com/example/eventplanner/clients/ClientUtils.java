package com.example.eventplanner.clients;

import android.content.Context;

import com.example.eventplanner.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {

    public static final String SERVICE_API_PATH = "http://" + BuildConfig.IP_ADDR + ":8080/api/";

    public static OkHttpClient test(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new AuthInterceptor(context))
                .build();
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DATE_TIME_FORMATTER)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                String dateTimeStr = json.getAsString();
                if (dateTimeStr.contains(".")) {
                    dateTimeStr = dateTimeStr.substring(0, Math.min(dateTimeStr.indexOf('.') + 4, dateTimeStr.length()));
                }
                return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            })
            .create();

    public static Retrofit retrofit(Context context) {
        return new Retrofit.Builder()
                .baseUrl(SERVICE_API_PATH)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(test(context))
                .build();
    }

    public static ServiceService getServiceService(Context context) {
        return retrofit(context).create(ServiceService.class);
    }

    public static EventTypeService getEventTypeService(Context context) {
        return retrofit(context).create(EventTypeService.class);
    }

    public static CategoryService getCategoryService(Context context) {
        return retrofit(context).create(CategoryService.class);
    }

    public static BudgetService getBudgetService(Context context) {
        return retrofit(context).create(BudgetService.class);
    }

    public static PricelistService getPricelistService(Context context) {
        return retrofit(context).create(PricelistService.class);
    }

    public static EventService getEventService(Context context) {
        return retrofit(context).create(EventService.class);
    }

    public static ProductService getProductService(Context context) {
        return retrofit(context).create(ProductService.class);
    }

    public static SolutionService getSolutionService(Context context) {
        return retrofit(context).create(SolutionService.class);
    }

    public static ReservationService getReservationService(Context context) {
        return retrofit(context).create(ReservationService.class);
    }

    public static EmailService getEmailService(Context context) {
        return retrofit(context).create(EmailService.class);
    }

    public static InviteService getInviteService(Context context) {
        return retrofit(context).create(InviteService.class);
    }

    public static AuthService getAuthService(Context context) {
        return retrofit(context).create(AuthService.class);
    }

    public static UserService getUserService(Context context) {
        return retrofit(context).create(UserService.class);
    }

    public static RegistrationRequestService getRegistrationRequestService(Context context) {
        return retrofit(context).create(RegistrationRequestService.class);
    }
}
