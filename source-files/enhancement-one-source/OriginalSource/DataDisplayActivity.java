package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DataDisplayActivity extends AppCompatActivity {

    private TextView goalWeightDisplay, dataGrid;  // Displays the user's goal weight and logged data
    private EditText dateInput, weightInput;  // Input fields for new date and weight entries
    private Button setGoalWeightButton, addDataButton, clearDataButton;  // Action buttons for various operations
    private DatabaseHelper databaseHelper;  // Database helper for managing data
    private String username;  // Stores the username of the logged-in user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        // Initialize UI elements
        goalWeightDisplay = findViewById(R.id.goal_weight_display);
        dataGrid = findViewById(R.id.data_grid_layout);
        dateInput = findViewById(R.id.editTextDate);
        weightInput = findViewById(R.id.editTextWeight);
        setGoalWeightButton = findViewById(R.id.btn_set_goal_weight);
        addDataButton = findViewById(R.id.add_data_button);
        clearDataButton = findViewById(R.id.btn_clear_data);

        // Instantiate the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the username from the previous activity
        username = getIntent().getStringExtra("USERNAME");

        // Display the user's goal weight, if set
        double goalWeight = databaseHelper.getGoalWeight(username);
        if (goalWeight > 0) {
            goalWeightDisplay.setText("Goal Weight: " + goalWeight);
        } else {
            goalWeightDisplay.setText("Goal Weight not set");  // Weakness: No interactivity to set goal directly here
            // Planned Enhancement: Add a prompt or shortcut to guide users to set their goal weight
        }

        // Display all logged weight data
        displayAllData();  // Weakness: Data presented in a plain text format with no interactivity
        // Planned Enhancement: Integrate MPAndroidChart to visualize weight trends interactively

        // Set up the "Set Goal Weight" button
        setGoalWeightButton.setOnClickListener(view -> {
            Intent intent = new Intent(DataDisplayActivity.this, SetGoalWeightActivity.class);
            intent.putExtra("USERNAME", username);  // Pass the username to the next activity
            startActivity(intent);
        });

        // Set up the "Add Data" button
        addDataButton.setOnClickListener(view -> {
            String date = dateInput.getText().toString().trim();  // Retrieve date input
            String weightStr = weightInput.getText().toString().trim();  // Retrieve weight input

            // Weakness: No validation for date format or negative weight inputs
            // Planned Enhancement: Validate input to ensure proper date format and realistic, positive weight values
            if (!date.isEmpty() && !weightStr.isEmpty()) {
                double weight = Double.parseDouble(weightStr);
                long result = databaseHelper.insertData(username, date, weight);  // Add data to the database
                if (result != -1) {
                    // Success feedback
                    Toast.makeText(DataDisplayActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                    dateInput.setText("");  // Clear input fields
                    weightInput.setText("");
                    displayAllData();  // Refresh the data grid
                } else {
                    // Weakness: Generic error message for data insertion failure
                    // Planned Enhancement: Provide specific feedback (e.g., database error, duplicate entry)
                    Toast.makeText(DataDisplayActivity.this, "Failed to add data", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Weakness: Minimal user feedback for empty fields
                // Planned Enhancement: Provide detailed messages for each empty field
                Toast.makeText(DataDisplayActivity.this, "Please enter both date and weight", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the "Clear Data" button
        clearDataButton.setOnClickListener(view -> {
            new AlertDialog.Builder(DataDisplayActivity.this)
                    .setTitle("Clear All Data")
                    .setMessage("Are you sure you want to delete all your entries?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean result = databaseHelper.clearUserData(username);  // Clear all user data from the database
                        if (result) {
                            dataGrid.setText("");  // Clear the displayed data
                            Toast.makeText(DataDisplayActivity.this, "Data cleared successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Weakness: Generic error message for data clearing failure
                            // Planned Enhancement: Provide detailed error messages for specific failure scenarios
                            Toast.makeText(DataDisplayActivity.this, "Failed to clear data", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    // Method to display all logged data for the user
    private void displayAllData() {
        dataGrid.setText("");  // Clear previous data display
        List<String[]> data = databaseHelper.getAllData(username);  // Retrieve all user data
        if (data.isEmpty()) {
            dataGrid.append("No data available.\n");  // Display message for no data
        } else {
            // Weakness: Plain text display lacks visualization
            // Planned Enhancement: Use charts to display trends and summaries interactively
            for (String[] entry : data) {
                String date = entry[0];
                String weight = entry[1];
                // Display each entry in the grid
                if (date != null && !date.isEmpty() && weight != null && !weight.isEmpty()) {
                    dataGrid.append(date + " - " + weight + "\n");
                } else {
                    dataGrid.append("N/A - N/A\n");  // Handle null or empty entries gracefully
                }
            }
        }
    }
}
