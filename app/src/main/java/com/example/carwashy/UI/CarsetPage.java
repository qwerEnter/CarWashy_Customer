package com.example.carwashy.UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.CarsetAdapter;
import com.example.carwashy.Adapter.ServiceAdapter;
import com.example.carwashy.Fragment.ServiceDetailsDialogFragment;
import com.example.carwashy.Model.Service;
import com.example.carwashy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarsetPage extends AppCompatActivity {

    private RecyclerView recyclerView,recyclerViewSet;
    private Service service;
    private TextView totalCostTextView;
    private static final String CLAIMED_REWARDS_PREF = "ClaimedRewards";
    private RecyclerView addedServicesRecyclerView;
    private ServiceAdapter addedServicesAdapter,serviceAdapter;
    private List<Service> serviceList;
    private CarsetAdapter carsetAdapter;
    private Button applyRewardButton;
    private double claimedDiscount;
    private ServiceDetailsDialogFragment.ServiceAddedListener serviceAddedListener;

    private TextView emptyView;

    @SuppressLint("NonConstantResourceId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carset);

        // Initialize addedServicesAdapter here
        addedServicesAdapter = new ServiceAdapter(new ArrayList<>(), getSupportFragmentManager());
        addedServicesRecyclerView = findViewById(R.id.rvcarset);  // initialize addedServicesRecyclerView
        addedServicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addedServicesRecyclerView.setAdapter(addedServicesAdapter);

        emptyView = findViewById(R.id.emptyView);
        totalCostTextView = findViewById(R.id.totalcost);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        serviceList = new ArrayList<>();
        carsetAdapter = new CarsetAdapter(serviceList, getSupportFragmentManager());
        recyclerView.setAdapter(carsetAdapter);
        retrieveServiceData();

        applyRewardButton = findViewById(R.id.applyreward);
        // Check if there is a claimed discount
        SharedPreferences claimedSharedPreferences = getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
        claimedDiscount = claimedSharedPreferences.getFloat("claimed_discount", 0.0f);
        // If there is a claimed discount, make the "APPLY REWARD" button visible
        if (claimedDiscount > 0) {
            applyRewardButton.setVisibility(View.VISIBLE);
        }
        applyRewardButton.setOnClickListener(v -> {
            // Subtract the claimed discount from the total cost
            subtractClaimedDiscount();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.menu_service).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_service) {
                return true;
            }
            if (item.getItemId() == R.id.menu_booking) {
                showValetConfirmationDialog();
                return true;
            }
            return false;
        });
    }

    private void retrieveServiceData() {
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service");
        serviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Service service = snapshot.getValue(Service.class);
                    if (service != null) {
                        serviceList.add(service);
                    }
                }
                carsetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("CarsetPage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }
    private void subtractClaimedDiscount() {
        // Get the current total cost
        String totalCostString = totalCostTextView.getText().toString().replace("RM ", "");
        double totalCost = Double.parseDouble(totalCostString);

        // Subtract the claimed discount
        totalCost -= claimedDiscount;

        // Update the total cost TextView
        totalCostTextView.setText("RM " + totalCost);

        // Optionally, you can reset the claimed discount in SharedPreferences
        SharedPreferences claimedSharedPreferences = getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = claimedSharedPreferences.edit();
        editor.putFloat("claimed_discount", 0.0f);
        editor.clear(); // Clear all entries in the SharedPreferences
        editor.apply();

        // Optionally, hide the "APPLY REWARD" button
        applyRewardButton.setVisibility(View.GONE);

        // Add any other logic you need after applying the reward
    }
    private void showValetConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Booking Confirmation");
        builder.setMessage("Do you want to add Valet Service to your booking?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // User clicked Yes, start ValetPage
            startActivity(new Intent(CarsetPage.this, ValetPage.class));
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // User clicked No, start BookingPage
            startActivity(new Intent(CarsetPage.this, BookingPage.class));
        });
        builder.show();
    }
}
