package com.example.project2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 101; // Code for SMS permission
    private EditText usernameEditText;
    private EditText passwordEditText; // Placeholder for future password functionality
    private Button loginButton;
    private Button createAccountButton; // Add the Create Account button
    private Button smsPermissionButton; // Add the SMS Permission button
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Match IDs in activity_main.xml
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.btn_login);
        createAccountButton = findViewById(R.id.btn_create_account); // Initialize Create Account button
        smsPermissionButton = findViewById(R.id.btn_request_sms); // Initialize SMS Permission button
        dbHelper = new DatabaseHelper(this);

        // Set up login button logic
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                showToast("Username cannot be empty.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                showToast("Password cannot be empty.");
                return;
            }

            boolean isValid = dbHelper.checkUserCredentials(username);
            if (isValid) {
                Intent intent = new Intent(MainActivity.this, DataDisplayActivity.class);
                startActivity(intent);
            } else {
                showToast("Invalid username or password. Please try again.");
            }
        });

        // Set up create account button logic
        createAccountButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                showToast("Username cannot be empty.");
                return;
            }

            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Account Creation")
                    .setMessage("Are you sure you want to create an account with username: " + username + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean isCreated = dbHelper.addUser(username);
                        if (isCreated) {
                            showToast("Account created successfully!");
                            passwordEditText.setText(""); // Clear only the password field
                        } else {
                            showToast("Failed to create account. Try again.");
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Set up SMS permission button logic
        smsPermissionButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request SMS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            } else {
                showToast("SMS Permission Already Granted!");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("SMS Permission Granted!");
            } else {
                showToast("SMS Permission Denied!");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
