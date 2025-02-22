package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetGoalWeightActivity extends AppCompatActivity {

    private EditText goalWeightEditText;  // Input field for goal weight
    private Button saveGoalButton;  // Button to save the goal weight
    private DatabaseHelper databaseHelper;  // Helper for database operations
    private String username;  // Logged-in user's username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal_weight);

        // Initialize UI elements
        goalWeightEditText = findViewById(R.id.goal_weight_edittext);
        saveGoalButton = findViewById(R.id.btn_save_goal);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the username from the previous activity
        username = getIntent().getStringExtra("USERNAME");

        // Set up "Save Goal" button functionality
        saveGoalButton.setOnClickListener(view -> {
            String goalWeightStr = goalWeightEditText.getText().toString().trim();  // Get user's input

            // Weakness: No validation for negative or unrealistic inputs
            // Planned Enhancement: Add validation to ensure goal weight is a positive and realistic value
            if (!goalWeightStr.isEmpty()) {
                double goalWeightValue = Double.parseDouble(goalWeightStr);

                // Save the goal weight in the database
                long result = databaseHelper.setGoalWeight(username, goalWeightValue);

                if (result > 0) {
                    // Weakness: Minimal feedback provided
                    // Planned Enhancement: Add a confirmation dialog for successful updates
                    Toast.makeText(SetGoalWeightActivity.this, "Goal weight set successfully", Toast.LENGTH_SHORT).show();

                    // Redirect to DataDisplayActivity after saving the goal weight
                    Intent intent = new Intent(SetGoalWeightActivity.this, DataDisplayActivity.class);
                    intent.putExtra("USERNAME", username);  // Pass username to next activity
                    startActivity(intent);
                } else {
                    // Weakness: Generic error message on failure
                    // Planned Enhancement: Provide detailed error messages (e.g., database error)
                    Toast.makeText(SetGoalWeightActivity.this, "Failed to set goal weight", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Weakness: No detailed feedback for empty input
                // Planned Enhancement: Provide clear error messages for empty or invalid fields
                Toast.makeText(SetGoalWeightActivity.this, "Please enter a valid goal weight", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
