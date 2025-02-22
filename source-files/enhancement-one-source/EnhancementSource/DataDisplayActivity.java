package com.example.project2;

import android.view.View;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private LineChart lineChart;
    private EditText editTextDate, editTextWeight;
    private Button btnSetGoalWeight, addDataButton, clearDataButton;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private WeightAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Initialize views
        lineChart = findViewById(R.id.lineChart);
        editTextDate = findViewById(R.id.editTextDate);
        editTextWeight = findViewById(R.id.editTextWeight);
        btnSetGoalWeight = findViewById(R.id.btn_set_goal_weight);
        addDataButton = findViewById(R.id.add_data_button);
        clearDataButton = findViewById(R.id.btn_clear_data);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize RecyclerView Adapter with Edit and Delete handlers
        adapter = new WeightAdapter(new ArrayList<>(),
                this::showEditDialog, // Edit Action
                this::confirmAndDeleteEntry // Delete Action
        );
        recyclerView.setAdapter(adapter);

        // Load weight data and update chart initially
        loadWeightData();

        // Add Data button logic
        addDataButton.setOnClickListener(v -> {
            String date = editTextDate.getText().toString().trim();
            String weightStr = editTextWeight.getText().toString().trim();

            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(weightStr)) {
                showToast("Please fill in both date and weight.");
                return;
            }

            // Validate date format (MM/DD/YY)
            if (!date.matches("^\\d{1,2}/\\d{1,2}/\\d{2}$")) {
                showToast("Invalid date format. Use MM/DD/YY.");
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                if (weight <= 0 || weight > 1000) {
                    showToast("Please enter a realistic weight value.");
                    return;
                }
                dbHelper.insertData(date, weight); // Save to DB
                loadWeightData(); // Refresh RecyclerView and chart
                showToast("Data added successfully!");
                editTextDate.setText("");
                editTextWeight.setText("");
            } catch (NumberFormatException e) {
                showToast("Invalid weight value.");
            }
        });

        // Clear Data button logic
        clearDataButton.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Confirm Clear Data")
                .setMessage("Are you sure you want to clear all your data? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean isCleared = dbHelper.clearUserData();
                    if (isCleared) {
                        showToast("All data cleared!");
                        loadWeightData(); // Refresh RecyclerView and chart
                    } else {
                        showToast("Failed to clear data.");
                    }
                })
                .setNegativeButton("No", null)
                .show());

        // Set Goal Weight button logic
        btnSetGoalWeight.setOnClickListener(v -> showSetGoalWeightDialog());
    }

    private void loadWeightData() {
        List<DatabaseHelper.WeightEntry> weightEntries = dbHelper.getAllData();
        adapter.updateData(weightEntries); // Update RecyclerView
        updateChart(weightEntries); // Update Chart

        // Display goal weight
        double goalWeight = dbHelper.getGoalWeight();
        TextView goalWeightText = findViewById(R.id.goalWeightText);
        if (goalWeight > 0) {
            goalWeightText.setText("Goal Weight: " + goalWeight + " lbs");
        } else {
            goalWeightText.setText("Goal Weight: Not Set");
        }
    }

    private void updateChart(List<DatabaseHelper.WeightEntry> weightEntries) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        for (DatabaseHelper.WeightEntry entry : weightEntries) {
            try {
                if (entry.getDate() == null || entry.getDate().isEmpty()) continue;
                Date date = sdf.parse(entry.getDate());
                entries.add(new Entry(date.getTime(), (float) entry.getWeight()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
        dataSet.setColor(getResources().getColor(R.color.teal_200));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Add Goal Weight Marker
        double goalWeight = dbHelper.getGoalWeight();
        if (goalWeight > 0) {
            LimitLine goalLine = new LimitLine((float) goalWeight, "Goal Weight");
            goalLine.setLineWidth(2f);
            goalLine.setTextSize(12f);
            lineChart.getAxisLeft().addLimitLine(goalLine);
        }

        // Configure X-Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

            @Override
            public String getFormattedValue(float value) {
                long timestamp = (long) value; // Convert float to long
                Date date = new Date(timestamp);
                return sdf.format(date);
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        Description description = new Description();
        description.setText("Weight Progress");
        lineChart.setDescription(description);
        lineChart.invalidate(); // Refresh the chart
    }

    private void showSetGoalWeightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Goal Weight");

        final EditText input = new EditText(this);
        input.setHint("Enter your goal weight (e.g., 150.0)");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String goalWeightStr = input.getText().toString();
            if (!goalWeightStr.isEmpty()) {
                double goalWeight = Double.parseDouble(goalWeightStr);
                dbHelper.setGoalWeight(goalWeight);
                loadWeightData();
                showToast("Goal Weight Set Successfully!");
            } else {
                showToast("Invalid Goal Weight.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showEditDialog(DatabaseHelper.WeightEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Entry");

        // Inflate the dialog layout
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_entry, null);
        builder.setView(view);

        final EditText editDate = view.findViewById(R.id.editDate);
        final EditText editWeight = view.findViewById(R.id.editWeight);

        editDate.setText(entry.getDate());
        editWeight.setText(String.valueOf(entry.getWeight()));

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDate = editDate.getText().toString();
            String newWeightStr = editWeight.getText().toString();

            if (!TextUtils.isEmpty(newDate) && !TextUtils.isEmpty(newWeightStr)) {
                double newWeight = Double.parseDouble(newWeightStr);
                dbHelper.updateWeight(entry.getId(), newDate, newWeight);
                loadWeightData();
                showToast("Entry updated!");
            } else {
                showToast("Invalid input.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void confirmAndDeleteEntry(DatabaseHelper.WeightEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteData(entry.getId());
                    loadWeightData();
                    showToast("Entry deleted!");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
