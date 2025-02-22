package com.example.project2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private LineChart lineChart;
    private EditText editTextDate, editTextWeight;
    private Button btnSetGoalWeight, addDataButton, clearDataButton, btnSearch, btnSort;
    private Spinner sortSpinner;
    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private WeightAdapter adapter;
    private String currentSortOrder = "date_asc"; // Default sorting order

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
        btnSearch = findViewById(R.id.btn_search);
        btnSort = findViewById(R.id.btn_sort);
        sortSpinner = findViewById(R.id.sortSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        dbHelper = new DatabaseHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Ensure scrolling is enabled
        recyclerView.setNestedScrollingEnabled(true);

        // Initialize RecyclerView Adapter with Edit and Delete handlers
        adapter = new WeightAdapter(new ArrayList<>(),
                this::showEditDialog,
                this::confirmAndDeleteEntry
        );
        recyclerView.setAdapter(adapter);

        // Load weight data
        loadWeightData();

        // Button Listeners
        addDataButton.setOnClickListener(v -> addWeightData());
        clearDataButton.setOnClickListener(v -> confirmClearData());
        btnSetGoalWeight.setOnClickListener(v -> showSetGoalWeightDialog());
        btnSearch.setOnClickListener(v -> showSearchDialog());
        btnSort.setOnClickListener(v -> {
            String selectedSort = sortSpinner.getSelectedItem().toString();

            switch (selectedSort) {
                case "Sort by Date (Ascending)":
                    currentSortOrder = "date_asc";
                    break;
                case "Sort by Date (Descending)":
                    currentSortOrder = "date_desc";
                    break;
                case "Sort by Weight (Ascending)":
                    currentSortOrder = "weight_asc";
                    break;
                case "Sort by Weight (Descending)":
                    currentSortOrder = "weight_desc";
                    break;
                default:
                    currentSortOrder = "date_asc"; // Fallback to default sorting
                    break;
            }

            loadWeightData(); // Refresh data with new sorting
        });
    }

    private void loadWeightData() {
        List<DatabaseHelper.WeightEntry> weightEntries = dbHelper.getAllData(currentSortOrder);
        adapter.updateData(weightEntries);
        updateChart(weightEntries);

        double goalWeight = dbHelper.getGoalWeight();
        TextView goalWeightText = findViewById(R.id.goalWeightText);
        goalWeightText.setText(goalWeight > 0 ? "Goal Weight: " + goalWeight + " lbs" : "Goal Weight: Not Set");
    }

    private void addWeightData() {
        String date = editTextDate.getText().toString().trim();
        String weightStr = editTextWeight.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(weightStr)) {
            showToast("Please fill in both date and weight.");
            return;
        }

        if (!date.matches("^\\d{1,2}/\\d{1,2}/\\d{2}$")) {
            showToast("Invalid date format. Use MM/DD/YY.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            if (weight < 1 || weight > 500) {
                showToast("Please enter a realistic weight value (1-500 lbs).");
                return;
            }
            dbHelper.insertData(date, weight);
            loadWeightData();
            showToast("Data added successfully!");
            editTextDate.setText("");
            editTextWeight.setText("");
        } catch (NumberFormatException e) {
            showToast("Invalid weight value.");
        }
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Clear Data")
                .setMessage("Are you sure you want to clear all your data? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean isCleared = dbHelper.clearUserData();
                    if (isCleared) {
                        showToast("All data cleared!");
                        loadWeightData();
                    } else {
                        showToast("Failed to clear data.");
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
                try {
                    double goalWeight = Double.parseDouble(goalWeightStr);
                    dbHelper.setGoalWeight(goalWeight);
                    loadWeightData(); // Refresh UI
                    showToast("Goal Weight Set Successfully!");
                } catch (NumberFormatException e) {
                    showToast("Invalid Goal Weight.");
                }
            } else {
                showToast("Invalid Goal Weight.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Entries");

        View view = getLayoutInflater().inflate(R.layout.dialog_search, null);
        builder.setView(view);

        final EditText startDateInput = view.findViewById(R.id.startDate);
        final EditText endDateInput = view.findViewById(R.id.endDate);
        final EditText minWeightInput = view.findViewById(R.id.minWeight);
        final EditText maxWeightInput = view.findViewById(R.id.maxWeight);

        builder.setPositiveButton("Search", (dialog, which) -> {
            String startDate = startDateInput.getText().toString().trim();
            String endDate = endDateInput.getText().toString().trim();
            String minWeightStr = minWeightInput.getText().toString().trim();
            String maxWeightStr = maxWeightInput.getText().toString().trim();

            if (!startDate.isEmpty() && !endDate.isEmpty() && !minWeightStr.isEmpty() && !maxWeightStr.isEmpty()) {
                try {
                    double minWeight = Double.parseDouble(minWeightStr);
                    double maxWeight = Double.parseDouble(maxWeightStr);
                    List<DatabaseHelper.WeightEntry> results = dbHelper.searchEntries(startDate, endDate, minWeight, maxWeight);
                    adapter.updateData(results);
                } catch (NumberFormatException e) {
                    showToast("Invalid weight range.");
                }
            } else {
                showToast("Please fill all fields.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateChart(List<DatabaseHelper.WeightEntry> weightEntries) {
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");

        // Ensure we don't process an empty list (prevents crashes)
        if (weightEntries.isEmpty()) {
            lineChart.clear();
            return;
        }

        for (DatabaseHelper.WeightEntry entry : weightEntries) {
            try {
                if (entry.getDate() == null || entry.getDate().isEmpty()) continue;

                // Ensure dates are correctly sorted even in descending order
                Date date = sdf.parse(entry.getDate());
                if (date != null) {
                    entries.add(new Entry((float) date.getTime(), (float) entry.getWeight()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Sort entries to ensure proper rendering (necessary for descending order)
        entries.sort((e1, e2) -> Float.compare(e1.getX(), e2.getX()));

        if (entries.isEmpty()) {
            lineChart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
        dataSet.setColor(Color.CYAN);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // Remove extra labels from top
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ""; // Hides X-axis labels
            }
        });

        // Add goal weight line if set
        double goalWeight = dbHelper.getGoalWeight();
        if (goalWeight > 0) {
            LimitLine goalLine = new LimitLine((float) goalWeight, "Goal Weight");
            goalLine.setLineColor(Color.RED);
            goalLine.setLineWidth(2f);
            goalLine.setTextColor(Color.BLACK);
            goalLine.setTextSize(12f);
            lineChart.getAxisLeft().addLimitLine(goalLine);
        }
    }


    private void showEditDialog(DatabaseHelper.WeightEntry entry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Entry");

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
