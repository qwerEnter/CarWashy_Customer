package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.UserAdapter;
import com.example.carwashy.Model.Reward;
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

public class RewardPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Reward> rewardList;
    private TextView pointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reward);

        pointsTextView = findViewById(R.id.points);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rewardList = new ArrayList<>();
        userAdapter = new UserAdapter(rewardList,pointsTextView);
        recyclerView.setAdapter(userAdapter);

        displayTotalPoints();

        // Set total points in the adapter
        userAdapter.setTotalPoints(retrieveTotalPointsFromSharedPreferences());

        // Retrieve data from Firebase and update the RecyclerView
        retrieveRewardData();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Your existing code for handling item selection

            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(RewardPage.this, HomePage.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(RewardPage.this, ProfilePage.class);
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


    private int defaultPoints() {
        int currentPoints = 0;
        return currentPoints ;
    }
    private int retrieveTotalPointsFromSharedPreferences() {
        // Retrieve total points from reward SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ServicePageDataReward", MODE_PRIVATE);
        return sharedPreferences.getInt("totalPoints", 0);
    }

    private int sumPoints() {
        int sumPoints =  defaultPoints() + retrieveTotalPointsFromSharedPreferences();
        return sumPoints;
    }
    private void displayTotalPoints() {
        int finalPoints = sumPoints();
        pointsTextView.setText(String.valueOf(finalPoints));
    }


    private void retrieveRewardData() {
        DatabaseReference rewardReference = FirebaseDatabase.getInstance().getReference("Reward");
        rewardReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rewardList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reward reward = snapshot.getValue(Reward.class);
                    if (reward != null) {
                        rewardList.add(reward);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
            }
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
        Intent intent = new Intent(RewardPage.this, Login.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent going back
    }
}