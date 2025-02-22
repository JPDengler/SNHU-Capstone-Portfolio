package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;  // Input fields for username and password
    private Button loginButton, createAccountButton, smsPermissionButton;  // Buttons for actions
    private DatabaseHelper databaseHelper;  // Database helper for user authentication and account management

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.btn_login);
        createAccountButton = findViewById(R.id.btn_create_account);
        smsPermissionButton = findViewById(R.id.btn_request_sms);

        // Instantiate the DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Set up login button functionality setOnClickListener
        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();  // Retrieve username input
            String password = passwordEditText.getText().toString().trim();  // Retrieve password input

            // Weakness: No input validation for empty or improperly formatted fields
            // Planned Enhancement: Add checks to ensure username and password fields are not empty and meet formatting requirements
            if (databaseHelper.checkUserCredentials(username, password)) {
                // Credentials match, redirect to DataDisplayActivity
                Intent intent = new Intent(MainActivity.this, DataDisplayActivity.class);
                intent.putExtra("USERNAME", username);  // Pass the username to the next activity
                startActivity(intent);
            } else {
                // Weakness: Generic error message for failed login attempts
                // Planned Enhancement: Provide specific error messages for different failure scenarios
                Toast.makeText(MainActivity.this, "Invalid login. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up create account button functionality
        createAccountButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();  // Retrieve username input
            String password = passwordEditText.getText().toString().trim();  // Retrieve password input

            // Weakness: No validation to ensure both fields are filled
            // Planned Enhancement: Validate that both fields are non-empty before attempting account creation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                // Add user to the database and provide feedback
                long result = databaseHelper.addUser(username, password, 0.0);  // Add user with a default weight of 0.0
                if (result > 0) {
                    // Account successfully created
                    Toast.makeText(MainActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Weakness: No detailed feedback on why account creation failed
                    // Planned Enhancement: Provide error messages to specify reasons for failure (e.g., username already exists)
                    Toast.makeText(MainActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up SMS permission request button functionality
        smsPermissionButton.setOnClickListener(view -> {
            // Redirect to the SmsPermissionActivity
            Intent intent = new Intent(MainActivity.this, SmsPermissionActivity.class);
            startActivity(intent);
        });
    }
}
