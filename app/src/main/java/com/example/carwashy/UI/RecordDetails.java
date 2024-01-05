package com.example.carwashy.UI;

import static com.example.carwashy.Model.CarWashRecord.convertJsonToServicesList;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.ServiceAdapter;
import com.example.carwashy.Model.CarWashRecord;
import com.example.carwashy.Model.Merchant;
import com.example.carwashy.Model.Service;
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

public class RecordDetails extends AppCompatActivity {

    private DatabaseReference bookingInfoReference;
    private ServiceAdapter serviceAdapter;
    private String noPlate;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_details);

        bookingInfoReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");

        // Retrieve the noPlate value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataBookingRecord", MODE_PRIVATE);
        noPlate = preferences.getString("noPlate", "");
        date = preferences.getString("date", "");

        TextView textdate = findViewById(R.id.date);
        TextView textnoplate = findViewById(R.id.noplate);
        textdate.setText(date);
        textnoplate.setText(noPlate);
        serviceAdapter = new ServiceAdapter(new ArrayList<>(), getSupportFragmentManager());
        RecyclerView rvcarset = findViewById(R.id.rvcarset);
        rvcarset.setLayoutManager(new LinearLayoutManager(this));
        rvcarset.setAdapter(serviceAdapter);

        retrieveBookingInfoData(noPlate);

        Button googleMaps = findViewById(R.id.buttonmap);
        googleMaps.setOnClickListener(view -> {

            String currentlocation = "";
            TextView premiseaddress = findViewById(R.id.address);
            String destination = premiseaddress.getText().toString();

            Uri uri = Uri.parse("https://www.google.com/maps/dir/" +currentlocation+ "/" +destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });



        Button contactCenter = findViewById(R.id.buttoncenter);
        contactCenter.setOnClickListener(view -> {
            DatabaseReference bookingInfoReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");
            bookingInfoReference.orderByChild("noPlate").equalTo(noPlate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CarWashRecord record = snapshot.getValue(CarWashRecord.class);
                            if (record != null) {
                                String merchantId = record.getMerchant_id();
                                fetchMerchantPhoneNumber(merchantId);
                            }
                        }
                    } else {
                        Toast.makeText(RecordDetails.this, "No data found for the entered license plate", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RecordDetails.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Your existing code for handling item selection

            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(RecordDetails.this, HomePage.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_profile) {

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


    private void fetchMerchantPhoneNumber(String merchantId) {

        DatabaseReference merchantReference = FirebaseDatabase.getInstance().getReference("Merchant").child(merchantId);
        merchantReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Merchant merchant = dataSnapshot.getValue(Merchant.class);
                    if (merchant != null) {
                        String cwcPhone = merchant.getPhonenumber();
                        if (!cwcPhone.isEmpty()) {
                            showCallConfirmationDialog(cwcPhone);
                        } else {
                            Toast.makeText(RecordDetails.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(RecordDetails.this, "Merchant information not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecordDetails.this, "Error fetching merchant information: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCallConfirmationDialog(String cwcPhone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecordDetails.this);
        builder.setTitle("Call Center");
        builder.setMessage("Do you want to call the Premise?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + cwcPhone));
            if (dialIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(dialIntent);
            } else {
                Toast.makeText(RecordDetails.this, "No app to handle phone dialer", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void retrieveBookingInfoData(String noPlate) {
        bookingInfoReference.orderByChild("noPlate").equalTo(noPlate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CarWashRecord record = snapshot.getValue(CarWashRecord.class);
                        if (record != null) {
                            displayCarWashRecord(record);
                        }
                    }
                } else {
                    Toast.makeText(RecordDetails.this, "No data found for the entered license plate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecordDetails.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void displayCarWashRecord(CarWashRecord record) {

        final TextView textdate = findViewById(R.id.date);
        textdate.setText(record.getDate());

        final TextView textsession = findViewById(R.id.session);
        textsession.setText(record.getSession());

        final TextView timestart = findViewById(R.id.timestart);
        timestart.setText(record.getTimeStart());

        final TextView timefinish = findViewById(R.id.timefinish);
        timefinish.setText(record.getTimeFinish());

        final TextView noplate = findViewById(R.id.noplate);
        noplate.setText(record.getNoPlate());

        final TextView carname = findViewById(R.id.carname);
        carname.setText(record.getCarName());

        final TextView cost = findViewById(R.id.cost);
        cost.setText(record.getTotalcost());

        final TextView address = findViewById(R.id.address);
        address.setText(record.getAddress());

        final TextView valet = findViewById(R.id.valet);
        valet.setText(record.getValet());

        final TextView valetnum = findViewById(R.id.valetphonenum);
        valetnum.setText(record.getValetPhone());

        final TextView custaddress = findViewById(R.id.customeraddress);
        custaddress.setText(record.getCurrentaddress());

        String carsetJson = record.getCarsetJson();
        List<Service> services = convertJsonToServicesList(carsetJson);
        updateRecyclerView(services);
    }



    private void updateRecyclerView(List<Service> services) {
        serviceAdapter.setServiceList(services);
        serviceAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(RecordDetails.this, Login.class);
        startActivity(intent);
        finish();  // Optional: Finish the current activity to prevent going back
    }
}