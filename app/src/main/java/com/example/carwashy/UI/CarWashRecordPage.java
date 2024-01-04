package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.CarWashRecordAdapter;
import com.example.carwashy.Model.CarWashRecord;
import com.example.carwashy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CarWashRecordPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarWashRecordAdapter carwashrecordAdapter;
    private List<CarWashRecord> carwashrecordList;
    private Context context;
    private ImageView cwbookingimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carwashrecord);

        recyclerView = findViewById(R.id.rv); // Replace with your actual RecyclerView ID
        cwbookingimage = findViewById(R.id.cwbookingimage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carwashrecordList = new ArrayList<>();
        carwashrecordAdapter = new CarWashRecordAdapter(carwashrecordList,context);
        recyclerView.setAdapter(carwashrecordAdapter);

        retrieveBookingStatusData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkRecyclerViewEmpty();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.menu_status).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Your existing code for handling item selection

            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(CarWashRecordPage.this, HomePage.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_status) {
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

    private void retrieveBookingStatusData() {
        DatabaseReference recReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");
        recReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                carwashrecordList.clear();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String currentUserId = currentUser.getUid();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CarWashRecord rec = snapshot.getValue(CarWashRecord.class);

                        // Check if customer_id exists and matches the current user's ID
                        if (rec != null && rec.getCustomer_id() != null && rec.getCustomer_id().equals(currentUserId)) {
                            carwashrecordList.add(rec);
                        }
                    }
                    carwashrecordAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("CarWashRecordPage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }
    private void checkRecyclerViewEmpty() {
        // Check if the bookingstatusList is empty
        if (carwashrecordList.isEmpty()) {
            cwbookingimage.setVisibility(View.VISIBLE);
        } else {
            cwbookingimage.setVisibility(View.GONE);
        }
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CarWashy App");
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
        Intent intent = new Intent(CarWashRecordPage.this, Login.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent going back
    }
}