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

import com.example.carwashy.Adapter.ValetAdapter;
import com.example.carwashy.Model.Valet;
import com.example.carwashy.R;
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

        SharedPreferences preferences = getSharedPreferences("ServicePageDataB", Context.MODE_PRIVATE);
        String selectedPremiseAddress = preferences.getString("address", "");
        Log.d("ValetPage", "Address for ValetPage: " + selectedPremiseAddress);


        recyclerView = findViewById(R.id.rv); // Replace with your actual RecyclerView ID
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        valetList = new ArrayList<>();
        valetAdapter = new ValetAdapter(valetList);
        recyclerView.setAdapter(valetAdapter);
        valetAdapter.setOnItemClickListener(valet -> {
            // Create Intent to move to the next activity (PremisePage)
            Intent intent2 = new Intent(ValetPage.this, ValetPage2.class);
            // Add relevant data to the intent
            intent2.putExtra("name", valet.getName());
            // Save the data to ServicePage in the background (you need to implement this method)
            saveDataToBookingPage(valet);
            // Start the next activity
            startActivity(intent2);
        });

        retrieveValetData(selectedPremiseAddress);
    }
    private void retrieveValetData(String selectedPremiseAddress) {
        DatabaseReference valetReference = FirebaseDatabase.getInstance().getReference("Valet");

        // Query valet nodes where the "address" field matches selectedPremiseAddress
        valetReference.orderByChild("location").equalTo(selectedPremiseAddress).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                valetList.clear();

                for (DataSnapshot valetSnapshot : dataSnapshot.getChildren()) {
                    Valet valet = valetSnapshot.getValue(Valet.class);
                    if (valet != null) {
                        valetList.add(valet);
                        Log.d("ValetPage", "Valet data: " + valet.toString());
                    }
                }

                valetAdapter.notifyDataSetChanged();
                Log.e("Valet", "Valet data successfully retrieved");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Valet", "Valet data retrieval failed: " + error.getMessage());
            }
        });
    }






    private void saveDataToBookingPage(Valet valet) {
        // Assuming you want to use SharedPreferences to temporarily store the data
        SharedPreferences preferencesD = getSharedPreferences("ServicePageDataD", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesD.edit();

        // Save the data to SharedPreferences
        editor.putString("name", valet.getName());
        editor.putString("phonenumber", valet.getPhonenumber());

        // Apply the changes
        editor.apply();
    }

}