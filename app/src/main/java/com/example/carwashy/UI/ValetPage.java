package com.example.carwashy.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.carwashy.Adapter.PremiseAdapter;
import com.example.carwashy.Adapter.ValetAdapter;
import com.example.carwashy.Model.Premise;
import com.example.carwashy.Model.Valet;
import com.example.carwashy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ValetPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ValetAdapter valetAdapter;
    private List<Valet> valetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.valet);

        recyclerView = findViewById(R.id.rv); // Replace with your actual RecyclerView ID
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        valetList = new ArrayList<>();
        valetAdapter = new ValetAdapter(valetList);
        recyclerView.setAdapter(valetAdapter);

        // Set click listener for items in the RecyclerView
        valetAdapter.setOnItemClickListener(valet -> {

            // Create Intent to move to the next activity (PremisePage)
            Intent intent = new Intent(ValetPage.this, ValetPage2.class);
            // Add relevant data to the intent
            intent.putExtra("name", valet.getName());
            // Save the data to ServicePage in the background (you need to implement this method)
            saveDataToBookingPage(valet);
            // Start the next activity
            startActivity(intent);
        });

        retrievePremiseData();
    }
    private void retrievePremiseData() {
        DatabaseReference premiseReference = FirebaseDatabase.getInstance().getReference("Valet");
        premiseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                valetList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Valet valet = snapshot.getValue(Valet.class);
                    if (valet != null) {
                        valetList.add(valet);
                    }
                }
                valetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if any
                Log.e("PremisePage", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }
    private void saveDataToBookingPage(Valet valet) {
        // Assuming you want to use SharedPreferences to temporarily store the data
        SharedPreferences preferencesD = getSharedPreferences("ServicePageDataD", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesD.edit();

        // Save the data to SharedPreferences
        editor.putString("name", valet.getName());

        // Apply the changes
        editor.apply();
    }

}