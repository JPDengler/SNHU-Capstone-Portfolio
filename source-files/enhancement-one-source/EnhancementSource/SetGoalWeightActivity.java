package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SetGoalWeightActivity extends AppCompatActivity {

    private EditText goalWeightEditText;
    private Button saveGoalWeightButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal_weight);

        goalWeightEditText = findViewById(R.id.goal_weight_input);
        saveGoalWeightButton = findViewById(R.id.save_goal_weight_button);
        dbHelper = new DatabaseHelper(this);

        saveGoalWeightButton.setOnClickListener(v -> {
            String goalWeightStr = goalWeightEditText.getText().toString().trim();
            if (TextUtils.isEmpty(goalWeightStr)) {
                showToast("Please enter a goal weight.");
                return;
            }

            try {
                double goalWeight = Double.parseDouble(goalWeightStr);
                if (goalWeight <= 0 || goalWeight > 1000) {
                    showToast("Please enter a realistic weight value.");
                    return;
                }
                dbHelper.setGoalWeight(goalWeight);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("goalWeight", goalWeight);
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (NumberFormatException e) {
                showToast("Invalid goal weight.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
