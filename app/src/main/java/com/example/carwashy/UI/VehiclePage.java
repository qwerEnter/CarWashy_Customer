package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.VehicleAdapter;
import com.example.carwashy.Login;
import com.example.carwashy.Model.Vehicle;
import com.example.carwashy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VehiclePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VehicleAdapter vehicleAdapter;
    private List<Vehicle> vehicleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle);

        recyclerView = findViewById(R.id.rv2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vehicleList = new ArrayList<>();
        vehicleAdapter = new VehicleAdapter(vehicleList);
        recyclerView.setAdapter(vehicleAdapter);

        // Set click listener for items in the RecyclerView
        vehicleAdapter.setOnItemClickListener(vehicle -> {
            // Create Intent to move to the next activity (PremisePage)
            Intent intent = new Intent(VehiclePage.this, PremisePage.class);

            // Add relevant data to the intent
            intent.putExtra("carName", vehicle.getCarName());
            intent.putExtra("vehicleType", vehicle.getVehicleType());
            intent.putExtra("noPlate", vehicle.getNoPlate());
            intent.putExtra("model", vehicle.getModel());
            intent.putExtra("imageUri", vehicle.getImageUri());
            // Save the data to ServicePage in the background (you need to implement this method)
            saveDataToBookingPage(vehicle);

            // Start the next activity (PremisePage)
            startActivity(intent);
        });

        // Retrieve data from Firebase and update the RecyclerView
        retrieveVehicleData();

        // Go to the service page
        Button buttonAddVehicle = findViewById(R.id.buttonadd);
        buttonAddVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehiclePage.this, NewVehiclePage.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Your existing code for handling item selection

            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(VehiclePage.this, HomePage.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(VehiclePage.this, ProfilePage.class);
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
    private void retrieveVehicleData() {
        DatabaseReference vehicleReference = FirebaseDatabase.getInstance().getReference("Vehicle");
        vehicleReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vehicleList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vehicle vehicle = snapshot.getValue(Vehicle.class);
                    if (vehicle != null) {
                        vehicleList.add(vehicle);
                    }
                }
                vehicleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("VehiclePage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }
    private void saveDataToBookingPage(Vehicle vehicle) {
        // Assuming you want to use SharedPreferences to temporarily store the data
        SharedPreferences preferencesA = getSharedPreferences("ServicePageDataA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesA.edit();

        // Save the data to SharedPreferences
        editor.putString("carName", vehicle.getCarName());
        editor.putString("vehicleType", vehicle.getVehicleType());
        editor.putString("noPlate", vehicle.getNoPlate());
        editor.putString("imageUri", vehicle.getImageUri());

        // Apply the changes
        editor.apply();
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
        Intent intent = new Intent(VehiclePage.this, Login.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent going back
    }
}
