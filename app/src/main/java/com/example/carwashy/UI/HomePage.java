package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.Login;
import com.example.carwashy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        // go to reward page


        // go to reward page
        Button buttonreward = findViewById(R.id.buttonreward);
        buttonreward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, RewardPage.class);
                startActivity(intent);
            }
        });

        // go to service page
        Button buttonservice = findViewById(R.id.buttonservice);
        buttonservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, VehiclePage.class);
                startActivity(intent);
            }
        });

        // go to receipt page
        Button buttonstatus = findViewById(R.id.buttonstatus);
        buttonstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePage.this, BookingStatusPage.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Your existing code for handling item selection

            if (item.getItemId() == R.id.menu_home) {
                // No need to start CarsetPage again, it's already the current activity
                return true;
            }
            if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(HomePage.this, ProfilePage.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_logout) {
                // Show a confirmation dialog
                showLogoutConfirmationDialog();
                return true;
            }
            return false;
        });
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CarWashy");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked Yes, perform logout
            performLogout();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, dismiss the dialog
            dialog.dismiss();
        });
        builder.show();
    }

    private void performLogout() {
        // Add your logout logic here
        // For example, if you are using Firebase Authentication:
        FirebaseAuth.getInstance().signOut();

        // Redirect to the login screen or any other appropriate screen
        Intent intent = new Intent(HomePage.this, Login.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent going back
    }
}