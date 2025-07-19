package com.example.eventplanner.activities.details;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.eventplanner.FragmentTransition;
import com.example.eventplanner.R;
import com.example.eventplanner.activities.BaseActivity;
import com.example.eventplanner.adapters.ImageSliderAdapter;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.fragments.ActivityDetailsListFragment;
import com.example.eventplanner.fragments.ActivityListFragment;
import com.example.eventplanner.fragments.BudgetDetailsListFragment;
import com.example.eventplanner.fragments.ContentUnavailableFragment;
import com.example.eventplanner.models.AttendanceStat;
import com.example.eventplanner.models.EventDetails;
import com.example.eventplanner.viewmodels.AgendaViewModel;
import com.example.eventplanner.viewmodels.BudgetViewModel;
import com.example.eventplanner.viewmodels.EventDetailsViewModel;
import com.example.eventplanner.viewmodels.LoginViewModel;
import com.example.eventplanner.viewmodels.UserViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends BaseActivity {
    private EventDetailsViewModel eventDetailsViewModel;
    private LoginViewModel loginViewModel;
    private UserViewModel userViewModel;
    private AgendaViewModel agendaViewModel;
    private BudgetViewModel budgetViewModel;
    private ViewPager2 eventImageSlider;
    private LinearLayout sliderDotsContainer;
    private List<ImageView> dots;
    private TextView eventName, description, eventType, locationName, city, country, startDate, endDate, maxParticipants, privacyType, creator;
    private EventDetails event;
    boolean isJoined, isFavourite, isLoggedIn;
    String loggedUserRole, loggedUserEmail;
    private MapView mapView;
    private Button btnBack, btnJoin, btnLeave, btnAddToFavourites, btnRemoveFromFavourites, btnPdfReport, btnAttendance;
    private ActivityDetailsListFragment activityDetailsListFragment;
    private BudgetDetailsListFragment budgetDetailsListFragment;
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        getLayoutInflater().inflate(R.layout.activity_event_details, findViewById(R.id.content_frame));
        loginViewModel = LoginViewModel.getInstance(getApplicationContext());

        this.isLoggedIn = loginViewModel.isLoggedIn();
        this.loggedUserRole = loginViewModel.getRole();
        this.loggedUserEmail = loginViewModel.getUserEmail();

        eventImageSlider = findViewById(R.id.event_image_slider);
        sliderDotsContainer = findViewById(R.id.slider_dots_container);
        lineChart = findViewById(R.id.attendanceChart);

        eventName = findViewById(R.id.event_name);
        description = findViewById(R.id.description);
        eventType = findViewById(R.id.event_type);
        locationName = findViewById(R.id.location_name);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        maxParticipants = findViewById(R.id.max_participants);
        privacyType = findViewById(R.id.privacy_type);
        creator = findViewById(R.id.creator);

        int eventId = getIntent().getIntExtra("eventId", -1);

        if (eventId != -1) {
            setupViewModel();
            eventDetailsViewModel.fetchEventDetailsById(eventId);
            if (this.isLoggedIn) {
                checkIfEventIsFavourite(eventId);
                checkIfUserIsJoinedToEvent(eventId);
            }
        } else {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show();
        }

        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
        });
        btnJoin = findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(v -> {
            userViewModel.joinEvent(eventId);
        });
        btnLeave = findViewById(R.id.btn_leave);
        btnLeave.setOnClickListener(v -> {
            userViewModel.leaveEvent(eventId);
        });
        btnAddToFavourites = findViewById(R.id.btn_add_fav);
        btnAddToFavourites.setOnClickListener(v -> {
            userViewModel.addToFavourites(eventId);
        });
        btnRemoveFromFavourites = findViewById(R.id.btn_remove_fav);
        btnRemoveFromFavourites.setOnClickListener(v -> {
            userViewModel.removeFromFavourites(eventId);
        });
        btnPdfReport = findViewById(R.id.btn_download);
        btnPdfReport.setOnClickListener(v -> {
            downloadEventPdf(eventId);
        });
        btnAttendance = findViewById(R.id.btn_attendance);
        btnAttendance.setOnClickListener(v -> {
            downloadAttendancePdf(eventId);
        });

        agendaViewModel = new ViewModelProvider(this).get(AgendaViewModel.class);
        agendaViewModel.setContext(this);
        activityDetailsListFragment = ActivityDetailsListFragment.newInstance(agendaViewModel, eventId);
        FragmentTransition.to(activityDetailsListFragment, this, false, R.id.listViewActivities);
    }

    private void setupViewModel() {
        eventDetailsViewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        eventDetailsViewModel.setContext(this);

        eventDetailsViewModel.getEventDetails().observe(this, this::displayEventDetails);

        eventDetailsViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null) {
                if (errorMessage.startsWith("Network error: End of input at line 1 column 1 path $")) {
                    navigateToUnavailableContentFragment();
                } else {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayEventDetails(EventDetails event) {
        if (event != null) {
            this.event = event;

            eventName.setText(event.getName());
            description.setText(String.format("Description: %s", event.getDescription()));
            eventType.setText(String.format("Event Type: %s", event.getEventType()));
            locationName.setText(String.format("Location: %s", event.getLocationName()));
            city.setText(String.format("City: %s", event.getCity()));
            country.setText(String.format("Country: %s", event.getCountry()));
            startDate.setText(String.format("Start Date: %s", event.getStartDate().toString()));
            endDate.setText(String.format("End Date: %s", event.getEndDate().toString()));
            maxParticipants.setText(String.format("Max Participants: %s", event.getMaxParticipants()));
            privacyType.setText(String.format("Privacy Type: %s", event.getPrivacyType()));
            creator.setText(String.format("Creator: %s", event.getCreator()));

            if (event.getImages() != null && event.getImages().length != 0) {
                setupImageSlider();
            }

            GeoPoint startPoint = new GeoPoint(event.getLatitude(), event.getLongitude());
            IMapController mapController = mapView.getController();
            mapController.setZoom(15.0);
            mapController.setCenter(startPoint);

            Marker marker = new Marker(mapView);
            marker.setPosition(startPoint);
            marker.setTitle("Event Location");
            mapView.getOverlays().add(marker);

            if (!(isLoggedIn &&
                    !isJoined &&
                    "PUBLIC".equals(event.getPrivacyType()) &&
                    event.getStartDate().isAfter(LocalDateTime.now()) &&
                    !event.getCreator().equals(loggedUserEmail))) {
                btnJoin.setVisibility(View.GONE);
            }

            if (!(isLoggedIn &&
                    isJoined &&
                    event.getStartDate().isAfter(LocalDateTime.now()) &&
                    !event.getCreator().equals(loggedUserEmail))) {
                btnLeave.setVisibility(View.GONE);
            }

            if (!(isLoggedIn && !isFavourite)) {
                btnAddToFavourites.setVisibility(View.GONE);
            }

            if (!(isLoggedIn && isFavourite)) {
                btnRemoveFromFavourites.setVisibility(View.GONE);
            }

            userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
            userViewModel.setContext(this);
            userViewModel.getAddFavSuccess().observe(this, success -> {
                if (success) {
                    isFavourite = true;
                    btnAddToFavourites.setVisibility(View.GONE);
                    btnRemoveFromFavourites.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Added to favourites.", Toast.LENGTH_SHORT).show();
                }
            });
            userViewModel.getRemoveFavSuccess().observe(this, success -> {
                if (success) {
                    isFavourite = false;
                    btnAddToFavourites.setVisibility(View.VISIBLE);
                    btnRemoveFromFavourites.setVisibility(View.GONE);
                    Toast.makeText(this, "Removed from favourites.", Toast.LENGTH_SHORT).show();
                }
            });
            userViewModel.getJoinSuccess().observe(this, success -> {
                if (success) {
                    isJoined = true;
                    btnJoin.setVisibility(View.GONE);
                    btnLeave.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Joined to event.", Toast.LENGTH_SHORT).show();
                }
            });
            userViewModel.getLeaveSuccess().observe(this, success -> {
                if (success) {
                    isJoined = false;
                    btnLeave.setVisibility(View.GONE);
                    if (event.getPrivacyType().equals("PUBLIC")) {
                        btnJoin.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(this, "Left the event.", Toast.LENGTH_SHORT).show();
                }
            });
            userViewModel.getErrorMessage().observe(this, error -> {
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            });

            if (isLoggedIn && loggedUserEmail.equals(event.getCreator())) {
                budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
                budgetViewModel.setContext(this);
                budgetDetailsListFragment = BudgetDetailsListFragment.newInstance(budgetViewModel, event.getId());
                FragmentTransition.to(budgetDetailsListFragment, this, false, R.id.listViewBudget);
            }

            if (isLoggedIn && (loggedUserEmail.equals(event.getCreator()) || loggedUserRole.equals("ADMIN"))) {
                btnAttendance.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                loadChartData(event.getId());
            } else {
                lineChart.setVisibility(View.GONE);
                btnAttendance.setVisibility(View.GONE);
            }
        }
    }

    private void navigateToUnavailableContentFragment() {
        ContentUnavailableFragment fragment = new ContentUnavailableFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    private void setupPaginationDots(int size) {
        dots = new ArrayList<>();
        sliderDotsContainer.removeAllViews();

        for (int i = 0; i < size; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.inactive_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            sliderDotsContainer.addView(dot);
            dots.add(dot);
        }

        if (!dots.isEmpty()) {
            dots.get(0).setImageResource(R.drawable.active_dot);
        }
    }

    private void updatePaginationDots(int activePosition) {
        for (int i = 0; i < dots.size(); i++) {
            if (i == activePosition) {
                dots.get(i).setImageResource(R.drawable.active_dot);
            } else {
                dots.get(i).setImageResource(R.drawable.inactive_dot);
            }
        }
    }

    private void setupImageSlider() {
        String[] images = event.getImages();
        ImageSliderAdapter adapter = new ImageSliderAdapter(this, images);
        eventImageSlider.setAdapter(adapter);

        setupPaginationDots(images.length);

        eventImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updatePaginationDots(position);
            }
        });
    }

    private void loadChartData(int eventId) {
        ClientUtils.getEventService(getApplicationContext()).getAttendanceStats(eventId).enqueue(new Callback<List<AttendanceStat>>() {
            @Override
            public void onResponse(Call<List<AttendanceStat>> call, Response<List<AttendanceStat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showLineChart(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceStat>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLineChart(List<AttendanceStat> stats) {
        if (stats == null || stats.isEmpty()) {
            lineChart.clear();
            lineChart.setNoDataText("No attendance data yet");
            lineChart.setNoDataTextColor(Color.LTGRAY);
            lineChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD);
            lineChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < stats.size(); i++) {
            AttendanceStat stat = stats.get(i);
            entries.add(new Entry(i, stat.getCount()));
            labels.add(stat.getDate());
        }

        LineDataSet dataSet = new LineDataSet(entries, "Attendees Over Time");
        dataSet.setColor(Color.GREEN);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(Color.DKGRAY);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#AA66CC"));  // semi-transparent fill
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smooth curves

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);

        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.WHITE);
        lineChart.setExtraOffsets(10, 10, 10, 10);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.animateX(700, Easing.EaseInOutQuad);
        lineChart.invalidate();
    }

    public void checkIfEventIsFavourite(int eventId) {
        Call<Boolean> call = ClientUtils.getUserService(getApplicationContext()).isEventInFavourites(eventId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavourite = response.body();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAVOURITE_CHECK", "Error: " + t.getMessage());
            }
        });
    }

    public void checkIfUserIsJoinedToEvent(int eventId) {
        Call<Boolean> call = ClientUtils.getUserService(getApplicationContext()).isUserJoinedToEvent(eventId);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isJoined = response.body();
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("JOINED_CHECK", "Error: " + t.getMessage());
            }
        });
    }

    public void downloadEventPdf(int eventId) {
        Call<ResponseBody> call = ClientUtils.getEventService(getApplicationContext()).downloadEventPdf(eventId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File outputFile = new File(downloadsDir, "event_" + eventId + ".pdf");

                        InputStream inputStream = response.body().byteStream();
                        FileOutputStream outputStream = new FileOutputStream(outputFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        Toast.makeText(getApplicationContext(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to save PDF", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "PDF download failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void downloadAttendancePdf(int eventId) {
        Call<ResponseBody> call = ClientUtils.getEventService(getApplicationContext()).downloadAttendancePdf(eventId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File outputFile = new File(downloadsDir, "event_attendance_" + eventId + ".pdf");

                        InputStream inputStream = response.body().byteStream();
                        FileOutputStream outputStream = new FileOutputStream(outputFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        outputStream.flush();
                        outputStream.close();
                        inputStream.close();

                        Toast.makeText(getApplicationContext(), "PDF saved to Downloads!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to save PDF", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "PDF download failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
