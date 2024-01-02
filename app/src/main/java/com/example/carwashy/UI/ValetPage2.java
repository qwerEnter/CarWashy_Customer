package com.example.carwashy.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.Model.Customer;
import com.example.carwashy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ValetPage2 extends AppCompatActivity {

    private DatabaseReference customerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valet2);

        EditText phoneTextView = findViewById(R.id.phonenumber);
        EditText addressTextView = findViewById(R.id.useraddress);

        Button saveButton = findViewById(R.id.buttonsavevalet);

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Set up the Customer database reference
            customerReference = FirebaseDatabase.getInstance().getReference("Customer").child(currentUserId);

            // Retrieve and display data
            retrieveAndDisplayData(phoneTextView, addressTextView);

            saveButton.setOnClickListener(view -> {
                // Get the values from the EditText fields
                String phone = phoneTextView.getText().toString().trim();
                String currentAddress = addressTextView.getText().toString().trim();

                // Check if the fields are empty
                if (phone.isEmpty() || currentAddress.isEmpty()) {
                    // Show a Toast message
                    Toast.makeText(ValetPage2.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                } else {
                    // Save the values using SharedPreferences
                    saveDataToBookingPage(phone, currentAddress);

                    // Move to BookingPage
                    moveToBookingPage();
                }
            });
        }
    }

    private void retrieveAndDisplayData(EditText phoneTextView, EditText addressTextView) {
        // Retrieve data from the Customer database
        customerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Customer data found, extract and display information
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    if (customer != null) {
                        // Set values in EditText fields
                        phoneTextView.setText(customer.getPhonenumber());
                        addressTextView.setText(customer.getAddress());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if any
                Toast.makeText(ValetPage2.this, "Data retrieval failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataToBookingPage(String phone, String currentAddress) {
        // Using SharedPreferences to store the data temporarily
        SharedPreferences preferencesD = getSharedPreferences("ServicePageDataE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesD.edit();

        // Save the data
        editor.putString("phone", phone);
        editor.putString("currentaddress", currentAddress);

        // Apply the changes
        editor.apply();
    }

    private void moveToBookingPage() {
        Intent intent = new Intent(ValetPage2.this, BookingPage.class);
        startActivity(intent);
        finish(); // Optional: Finish the current activity to prevent going back
    }
}
