package com.example.carwashy.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.R;

public class ValetPage2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valet2);

        EditText phoneTextView = findViewById(R.id.phonenumber);
        EditText addressTextView = findViewById(R.id.useraddress);

        Button saveButton = findViewById(R.id.buttonsavevalet);
        saveButton.setOnClickListener(view -> {
            // Get the values from the EditText fields
            String phone = phoneTextView.getText().toString().trim();
            String currentaddress = addressTextView.getText().toString().trim();

            // Check if the fields are empty
            if (phone.isEmpty() || currentaddress.isEmpty()) {
                // Show a Toast message
                Toast.makeText(ValetPage2.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
            } else {
                // Save the values using SharedPreferences
                saveDataToBookingPage(phone, currentaddress);

                // Move to BookingPage
                moveToBookingPage();
            }
        });
    }

    private void saveDataToBookingPage(String phone, String currentaddress) {
        // Using SharedPreferences to store the data temporarily
        SharedPreferences preferencesD = getSharedPreferences("ServicePageDataE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesD.edit();

        // Save the data
        editor.putString("phone", phone);
        editor.putString("currentaddress", currentaddress);

        // Apply the changes
        editor.apply();
    }

    private void moveToBookingPage() {
        Intent intent = new Intent(ValetPage2.this, BookingPage.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent going back
    }
}
