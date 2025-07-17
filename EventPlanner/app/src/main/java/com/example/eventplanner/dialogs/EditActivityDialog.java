package com.example.eventplanner.dialogs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.example.eventplanner.models.EventActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

public class EditActivityDialog {
    private static TextInputEditText activityName, description, location, startDate, startTime, endDate, endTime;
    private static TextView errorActivityName, errorDescription, errorLocation, errorStartDate, errorStartTime, errorEndDate, errorEndTime, errorDateTime;
    private static View dialogView;
    private static EventActivity activity;
    private static ImageView btnStartDate, btnStartTime, btnEndDate, btnEndTime;

    public interface EditActivityListener {
        void onActivityEdited(EventActivity activity);
    }

    public static void show(Context context, EventActivity editActivity, EditActivityDialog.EditActivityListener listener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_edit_activity, null);
        activity = editActivity;

        initializeViews(context);
        populateFields();
        createDialog(context, listener);
    }

    private static void initializeViews(Context context) {
        activityName = dialogView.findViewById(R.id.name);
        description = dialogView.findViewById(R.id.description);
        location = dialogView.findViewById(R.id.location);
        startDate = dialogView.findViewById(R.id.editStartDate);
        startTime = dialogView.findViewById(R.id.editStartTime);
        startDate.setEnabled(false);
        startTime.setEnabled(false);
        endDate = dialogView.findViewById(R.id.editEndDate);
        endTime = dialogView.findViewById(R.id.editEndTime);

        errorActivityName = dialogView.findViewById(R.id.errorActivityName);
        errorDescription = dialogView.findViewById(R.id.errorDescription);
        errorLocation = dialogView.findViewById(R.id.errorLocation);
        errorStartDate = dialogView.findViewById(R.id.errorStartDate);
        errorStartTime = dialogView.findViewById(R.id.errorStartTime);
        errorEndDate = dialogView.findViewById(R.id.errorEndDate);
        errorEndTime = dialogView.findViewById(R.id.errorEndTime);
        errorDateTime = dialogView.findViewById(R.id.errorDateTime);

        btnStartDate = dialogView.findViewById(R.id.btnStartDate);
        btnStartTime = dialogView.findViewById(R.id.btnStartTime);
        btnEndDate = dialogView.findViewById(R.id.btnEndDate);
        btnEndTime = dialogView.findViewById(R.id.btnEndTime);

        btnStartDate.setEnabled(false);
        btnStartTime.setEnabled(false);
        btnEndDate.setOnClickListener(v -> showDatePicker(endDate, context));
        btnEndTime.setOnClickListener(v -> showTimePicker(endTime, context));
    }

    private static void showDatePicker(TextInputEditText editText, Context context) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(selectedDate);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private static void showTimePicker(TextInputEditText editText, Context context) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            editText.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private static void populateFields() {
        activityName.setText(activity.getName());
        description.setText(activity.getDescription());
        location.setText(activity.getLocation());
        startDate.setText(activity.getStartDate().toLocalDate().toString());
        startTime.setText(activity.getStartDate().toLocalTime().toString());
        endDate.setText(activity.getEndDate().toLocalDate().toString());
        endTime.setText(activity.getEndDate().toLocalTime().toString());
    }

    private static void createDialog(Context context, EditActivityDialog.EditActivityListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Edit Budget Item")
                .setPositiveButton("Edit", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean isValid = true;

                if (!validateField(activityName, errorActivityName)) {
                    isValid = false;
                }

                if (!validateField(description, errorDescription)) {
                    isValid = false;
                }

                if (!validateField(location, errorLocation)) {
                    isValid = false;
                }

                if (!validateField(startDate, errorStartDate)) {
                    isValid = false;
                }

                if (!validateField(startTime, errorStartTime)) {
                    isValid = false;
                }

                if (!validateField(endDate, errorEndDate)) {
                    isValid = false;
                }

                if (!validateField(endTime, errorEndTime)) {
                    isValid = false;
                }

                if (!endDate.getText().toString().trim().isEmpty() && !endTime.getText().toString().trim().isEmpty()) {
                    LocalDateTime startDateTime = LocalDateTime.parse(startDate.getText().toString().trim() + "T" + startTime.getText().toString().trim() + ":00");
                    LocalDateTime endDateTime = LocalDateTime.parse(endDate.getText().toString().trim() + "T" + endTime.getText().toString().trim() + ":00");
                    if (startDateTime.isAfter(endDateTime)) {
                        isValid = false;
                        errorDateTime.setVisibility(View.VISIBLE);
                    } else {
                        errorDateTime.setVisibility(View.GONE);
                    }
                }

                if (!isValid) {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                } else {
                    String activityNameTrimmed = activityName.getText().toString().trim();
                    String descriptionTrimmed = description.getText().toString().trim();
                    String locationTrimmed = location.getText().toString().trim();
                    String startDateTrimmed = startDate.getText().toString().trim();
                    String startTimeTrimmed = startTime.getText().toString().trim();
                    String endDateTrimmed = endDate.getText().toString().trim();
                    String endTimeTrimmed = endTime.getText().toString().trim();

                    activity.setName(activityNameTrimmed);
                    activity.setDescription(descriptionTrimmed);
                    activity.setLocation(locationTrimmed);
                    activity.setStartDate(LocalDateTime.parse(startDateTrimmed + "T" + startTimeTrimmed + ":00"));
                    activity.setEndDate(LocalDateTime.parse(endDateTrimmed + "T" + endTimeTrimmed + ":00"));

                    Toast.makeText(context, "Activity updated.", Toast.LENGTH_SHORT).show();
                    listener.onActivityEdited(activity);
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private static boolean validateField(EditText field, View errorView) {
        boolean isValid = !TextUtils.isEmpty(field.getText());
        errorView.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }
}
