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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        setContentView(R.layout.carset);

        Intent intent = getIntent();
        String selectedPremiseId = intent.getStringExtra("merchant_id");
        Log.d("CarsetPage", "merhcant_id for CarsetPage: " + selectedPremiseId);

        applyRewardButton = findViewById(R.id.applyreward);
        addedServicesAdapter = new ServiceAdapter(new ArrayList<>(), getSupportFragmentManager());
        addedServicesRecyclerView = findViewById(R.id.rvcarset);
        addedServicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addedServicesRecyclerView.setAdapter(addedServicesAdapter);

        emptyView = findViewById(R.id.emptyView);
        totalCostTextView = findViewById(R.id.totalcost);
        Log.d("TotalCostTextView", "Value: " + totalCostTextView.getText().toString());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        serviceList = new ArrayList<>();
        carsetAdapter = new CarsetAdapter(serviceList, getSupportFragmentManager());
        recyclerView.setAdapter(carsetAdapter);

        retrieveServiceData(selectedPremiseId);

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

    private void retrieveServiceData(String selectedMerchantId) {
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service");

        // Order the query by child "merchant_id" and filter by selected merchant ID
        serviceReference.orderByChild("merchant_id").equalTo(selectedMerchantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                serviceList.clear();
                for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each service
                    Service service = serviceSnapshot.getValue(Service.class);
                    if (service != null) {
                        // Add the service to your list
                        serviceList.add(service);
                    }
                }
                // Notify your adapter that the data has changed
                carsetAdapter.notifyDataSetChanged();

                SharedPreferences claimedSharedPreferences = getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
                claimedDiscount = claimedSharedPreferences.getFloat("claimed_discount", 0.0f);
                Log.d("claimedDiscount", "Value: " + claimedDiscount);
                if (claimedDiscount > 0.0) {
                        applyRewardButton.setVisibility(View.VISIBLE);
                        applyRewardButton.setOnClickListener(v -> {
                            if ("Calculating ..".equals(totalCostTextView.getText().toString()))
                            {
                                    Toast.makeText(getApplicationContext(), "Please select your services first", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    subtractClaimedDiscount();
                                    SharedPreferences.Editor editor = claimedSharedPreferences.edit();
                                    editor.apply();
                                    }
                        });
                }
                else if (claimedDiscount <= 0.0)
                {
                    applyRewardButton.setVisibility(View.VISIBLE);
                    applyRewardButton.setOnClickListener(v -> {
                    if ("Calculating ..".equals(totalCostTextView.getText().toString()))
                    {
                        Toast.makeText(getApplicationContext(), "Please select your services first", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Voucher cannot be found", Toast.LENGTH_SHORT).show();
                    }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CarsetPage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }



    private void subtractClaimedDiscount() {
        // Get the current total cost
        String totalCostString = totalCostTextView.getText().toString().replace("RM ", "");
        double totalCost = Double.parseDouble(totalCostString);
        totalCost -= claimedDiscount;
        totalCostTextView.setText("RM " + totalCost);

        SharedPreferences claimedSharedPreferences = getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = claimedSharedPreferences.edit();
        editor.putFloat("claimed_discount", 0.0f);
        editor.clear(); // Clear all entries in the SharedPreferences
        editor.apply();
        applyRewardButton.setVisibility(View.VISIBLE);
        applyRewardButton.setOnClickListener(v -> {
            {
                Toast.makeText(getApplicationContext(), "you claim ady", Toast.LENGTH_SHORT).show();
            }});
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
