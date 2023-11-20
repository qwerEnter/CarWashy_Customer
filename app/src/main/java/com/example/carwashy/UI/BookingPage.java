
package com.example.carwashy.UI;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.ServiceAdapter;
import com.example.carwashy.Fragment.DatePickerFragment;
import com.example.carwashy.Fragment.SessionDialogFragment;
import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.Model.Service;
import com.example.carwashy.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class BookingPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, SessionDialogFragment.SessionDialogListener {

    private RecyclerView addedServicesRecyclerView;
    private ServiceAdapter addedServicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking);

        addedServicesRecyclerView = findViewById(R.id.rvcarset);
        addedServicesAdapter = new ServiceAdapter(new ArrayList<>(),getSupportFragmentManager());
        addedServicesRecyclerView.setLayoutManager (new LinearLayoutManager(this));
        addedServicesRecyclerView.setAdapter(addedServicesAdapter);

        retrieveDataFromSharedPreferencesA();
        retrieveDataFromSharedPreferencesB();
        retrieveDataFromSharedPreferencesC();
        retrieveDataFromSharedPreferencesD();
        retrieveDataFromSharedPreferencesE();

        Button buttonbook = findViewById(R.id.buttonbook);
        buttonbook.setOnClickListener(view -> {

            BookingInfo bookingInfo = new BookingInfo();
            bookingInfo.setNoPlate(((TextView) findViewById(R.id.noplate)).getText().toString());
            bookingInfo.setCarName(((TextView) findViewById(R.id.carname)).getText().toString());
            bookingInfo.setTotalcost(((TextView) findViewById(R.id.cost)).getText().toString());
            bookingInfo.setValet(((TextView) findViewById(R.id.valet)).getText().toString());
            bookingInfo.setAddress(((TextView) findViewById(R.id.address)).getText().toString());
            bookingInfo.setDate(((TextView) findViewById(R.id.textdate)).getText().toString());
            bookingInfo.setSession(((TextView) findViewById(R.id.textsession)).getText().toString());
            bookingInfo.setPhone(((TextView) findViewById(R.id.usercurrentphone)).getText().toString());
            bookingInfo.setCurrentaddress(((TextView) findViewById(R.id.useraddress)).getText().toString());

            // Retrieve and set the list of services from SharedPreferencesC
            List<Service> addedServices = new ArrayList<>();
            SharedPreferences sharedPreferencesC = getSharedPreferences("ServicePageDataC", Context.MODE_PRIVATE);
            for (String serviceName : sharedPreferencesC.getStringSet("services", new HashSet<>())) {
                String carsets = sharedPreferencesC.getString(serviceName, "");
                Service service = new Service(serviceName, "Default Description",  Double.parseDouble(carsets),Double.parseDouble(carsets), Double.parseDouble(carsets),Double.parseDouble(carsets),Double.parseDouble(carsets), "Default Image Uri");
                addedServices.add(service);
            }

            bookingInfo.setCarsetJson(BookingInfo.convertServicesListToJson(addedServices)); // Set the list of services to the BookingInfo
            saveBookingInfoToFirebase(bookingInfo);

            clearSharedPreferences("ServicePageDataA");
            clearSharedPreferences("ServicePageDataB");
            clearSharedPreferences("ServicePageDataC");
            clearSharedPreferences("ServicePageDataD");
            clearSharedPreferences("ServicePageDataE");

            Intent intent = new Intent(BookingPage.this, BookingStatusPage.class);
            startActivity(intent);
        });

        Button bookingdate = findViewById(R.id.bookingdate);
        bookingdate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        Button bookingsession = findViewById(R.id.bookingsession);
        bookingsession.setOnClickListener(v -> showSessionDialog());

    }


    private void retrieveDataFromSharedPreferencesA() {
        SharedPreferences preferencesA = getSharedPreferences("ServicePageDataA", Context.MODE_PRIVATE);

        TextView carname = findViewById(R.id.carname);
        TextView noplate = findViewById(R.id.noplate);
        ImageView imagecar = findViewById(R.id.imagecar);

        String carName = preferencesA.getString("carName", "");
        String noPlate = preferencesA.getString("noPlate", "");
        String imageUriString = preferencesA.getString("imageUri", "");

        carname.setText(carName);
        noplate.setText(noPlate);

        if (!imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            Picasso.get().load(imageUri).into(imagecar);
        }
    }
    private void retrieveDataFromSharedPreferencesB() {
        SharedPreferences preferencesB = getSharedPreferences("ServicePageDataB", Context.MODE_PRIVATE);

        TextView address = findViewById(R.id.address);
        String premiseAddress = preferencesB.getString("address", "");

        address.setText(premiseAddress);
    }
    private void retrieveDataFromSharedPreferencesC() {
        SharedPreferences sharedPreferencesC = getSharedPreferences("ServicePageDataC", Context.MODE_PRIVATE);

        // Retrieve totalCost
        double totalCost = 0;
        for (String serviceName : sharedPreferencesC.getStringSet("services", new HashSet<>())) {
            String serviceCostString = sharedPreferencesC.getString(serviceName, "0");

            // Check if the string is not empty before parsing
            if (!serviceCostString.isEmpty()) {
                double serviceCost = Double.parseDouble(serviceCostString);
                totalCost += serviceCost;
            }
        }

        TextView costTextView = findViewById(R.id.cost);
        costTextView.setText("RM " + totalCost);

        if (addedServicesAdapter != null) {
            List<Service> addedServices = new ArrayList<>();
            for (String serviceName : sharedPreferencesC.getStringSet("services", new HashSet<>())) {
                String carsets = sharedPreferencesC.getString(serviceName, "");

                // Check if the string is not empty before parsing
                if (!carsets.isEmpty()) {
                    Service service = new Service(serviceName, "Default Description", Double.parseDouble(carsets), Double.parseDouble(carsets), Double.parseDouble(carsets), Double.parseDouble(carsets), Double.parseDouble(carsets), "Default Image Uri");
                    addedServices.add(service);
                }
            }
            addedServicesAdapter.setServiceList(addedServices);
            addedServicesAdapter.notifyDataSetChanged();
        }
    }

    private void retrieveDataFromSharedPreferencesD() {
        SharedPreferences preferencesD = getSharedPreferences("ServicePageDataD", Context.MODE_PRIVATE);

        TextView name = findViewById(R.id.valet);
        String nameTextView = preferencesD.getString("name", "-");

        name.setText(nameTextView);
    }
    private void retrieveDataFromSharedPreferencesE() {
        SharedPreferences preferencesE = getSharedPreferences("ServicePageDataE", Context.MODE_PRIVATE);

        TextView userPhone = findViewById(R.id.usercurrentphone);
        TextView userAddress = findViewById(R.id.useraddress);

        String userphoneNumber = preferencesE.getString("phone", "-");
        String uservaletAddress = preferencesE.getString("currentaddress", "-");

        userPhone.setText(userphoneNumber);
        userAddress.setText(uservaletAddress);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView textdate = findViewById(R.id.textdate);
        textdate.setText(currentDateString);
    }
    private void showSessionDialog() {
        SessionDialogFragment dialogFragment = new SessionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "SessionDialogFragment");
    }
    public void onSessionSelected(String session) {
        TextView textsession = findViewById(R.id.textsession);
        textsession.setText(session);
    }


    private void saveBookingInfoToFirebase(BookingInfo bookingInfo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference bookingRef = database.getReference("BookingInfo");

        String bookingId = bookingRef.push().getKey();
        bookingRef.child(bookingId).setValue(bookingInfo);
    }


    private void clearSharedPreferences(String sharedPreferencesName) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}