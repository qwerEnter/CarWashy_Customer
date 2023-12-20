package com.example.carwashy.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.PremiseAdapter;
import com.example.carwashy.Model.Premise;
import com.example.carwashy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PremisePage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PremiseAdapter premiseAdapter;
    private List<Premise> premiseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premise);

        recyclerView = findViewById(R.id.rv); // Replace with your actual RecyclerView ID
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        premiseList = new ArrayList<>();
        premiseAdapter = new PremiseAdapter(premiseList);
        recyclerView.setAdapter(premiseAdapter);

        // Set click listener for items in the RecyclerView
        premiseAdapter.setOnItemClickListener(premise -> {

            // Create Intent to move to the next activity (PremisePage)
            Intent intent = new Intent(PremisePage.this, CarsetPage.class);
            // Add relevant data to the intent
            intent.putExtra("merchant_id", premise.getMerchant_id());
            // Save the data to ServicePage in the background (you need to implement this method)
            saveDataToBookingPage(premise);
            // Start the next activity
            startActivity(intent);
        });
        // Retrieve data from Firebase and update the RecyclerView
        retrievePremiseData();
    }
    private void saveDataToBookingPage(Premise premise) {
        // Assuming you want to use SharedPreferences to temporarily store the data
        SharedPreferences preferencesB = getSharedPreferences("ServicePageDataB", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesB.edit();

        // Save the data to SharedPreferences
        editor.putString("address", premise.getAddress());

        // Apply the changes
        editor.apply();
    }
    private void retrievePremiseData() {
        DatabaseReference premiseReference = FirebaseDatabase.getInstance().getReference("Premise");
        premiseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                premiseList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Premise premise = snapshot.getValue(Premise.class);
                    if (premise != null) {
                        premiseList.add(premise);
                    }
                }
                premiseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("PremisePage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }
}
