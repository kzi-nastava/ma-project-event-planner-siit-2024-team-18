package com.example.eventplanner.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.eventplanner.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class CreateEventTypeDialog {
    private static EditText eventTypeName;
    private static EditText description;
    private static TextView errorEventTypeName;
    private static TextView errorDescription;
    private static TextView errorCategories;
    private static Button selectCategories;
    private static List<String> categories = new ArrayList<>();
    private static final List<String> selectedCategories = new ArrayList<>();
    private static View dialogView;
    private static Context dialogContext;

    public interface CreateEventTypeListener {
        void onEventTypeCreated(String name, String description, List<String> categories);
    }

    public static void show(Context context, List<String> allCategories, CreateEventTypeListener listener) {
        dialogContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogView = inflater.inflate(R.layout.dialog_create_event_type, null);

        initializeViews(allCategories);
        createDialog(context, listener);
    }

    private static void initializeViews(List<String> allCategories) {
        eventTypeName = dialogView.findViewById(R.id.edit_text_event_type);
        errorEventTypeName = dialogView.findViewById(R.id.text_event_type_error);
        description = dialogView.findViewById(R.id.edit_text_description);
        errorDescription = dialogView.findViewById(R.id.text_description_error);
        selectCategories = dialogView.findViewById(R.id.btn_select_categories);
        errorCategories = dialogView.findViewById(R.id.text_categories_error);

        selectCategories.setOnClickListener(v -> {
            boolean[] checkedItems = new boolean[categories.size()];

            for (int i = 0; i < categories.size(); i++) {
                checkedItems[i] = selectedCategories.contains(categories.get(i));
            }

            new MaterialAlertDialogBuilder(dialogContext)
                    .setTitle("Select Categories")
                    .setMultiChoiceItems(
                            categories.toArray(new CharSequence[0]),
                            checkedItems,
                            (dialog, which, isChecked) -> {
                                String selectedItem = categories.get(which);
                                if (isChecked) {
                                    selectedCategories.add(selectedItem);
                                } else {
                                    selectedCategories.remove(selectedItem);
                                }
                            })
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (selectedCategories.isEmpty()) {
                            selectCategories.setText("");
                        } else {
                            selectCategories.setText(selectedCategories.toString());
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        categories = allCategories;
    }

    private static void createDialog(Context context, CreateEventTypeListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setView(dialogView)
                .setTitle("Create Event Type")
                .setPositiveButton("Create", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean isValid = true;

                if (eventTypeName.getText().toString().trim().isEmpty()) {
                    errorEventTypeName.setVisibility(View.VISIBLE);
                    errorEventTypeName.setText("Please enter event type name.");
                    isValid = false;
                }
                else {
                    errorEventTypeName.setVisibility(View.GONE);
                }

                if (description.getText().toString().trim().isEmpty()) {
                    errorDescription.setVisibility(View.VISIBLE);
                    errorDescription.setText("Please enter description.");
                    isValid = false;
                }
                else {
                    errorDescription.setVisibility(View.GONE);
                }

                if (selectedCategories.isEmpty()) {
                    errorCategories.setVisibility(View.VISIBLE);
                    errorCategories.setText(R.string.please_select_categories);
                    isValid = false;
                } else {
                    errorCategories.setVisibility(View.GONE);
                }

                if (!isValid) {
                    Toast.makeText(context, "Invalid Input Detected!", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onEventTypeCreated(eventTypeName.getText().toString().trim(), description.getText().toString().trim(), selectedCategories);
                    Toast.makeText(context, "Event type successfuly created.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }
}
