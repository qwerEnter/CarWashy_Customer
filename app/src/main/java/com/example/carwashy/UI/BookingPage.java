
package com.example.carwashy.UI;

import android.app.AlertDialog;
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

import androidx.activity.OnBackPressedCallback;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class BookingPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, SessionDialogFragment.SessionDialogListener {

    private RecyclerView addedServicesRecyclerView;
    private DatabaseReference sessionReference;
    private ServiceAdapter addedServicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
        setContentView(R.layout.booking);

        sessionReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");

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

            String merchantId = retrieveMerchantIdFromSharedPreferencesB();
            BookingInfo bookingInfo = new BookingInfo();
            bookingInfo.setNoPlate(((TextView) findViewById(R.id.noplate)).getText().toString());
            bookingInfo.setCarName(((TextView) findViewById(R.id.carname)).getText().toString());
            bookingInfo.setTotalcost(((TextView) findViewById(R.id.cost)).getText().toString());
            bookingInfo.setValet(((TextView) findViewById(R.id.valet)).getText().toString());
            bookingInfo.setValetPhone(((TextView) findViewById(R.id.valetphonenum)).getText().toString());
            bookingInfo.setAddress(((TextView) findViewById(R.id.address)).getText().toString());
            bookingInfo.setDate(((TextView) findViewById(R.id.textdate)).getText().toString());
            bookingInfo.setSession(((TextView) findViewById(R.id.textsession)).getText().toString());
            bookingInfo.setPhone(((TextView) findViewById(R.id.customerphonenum)).getText().toString());
            bookingInfo.setCurrentaddress(((TextView) findViewById(R.id.customeraddress)).getText().toString());
            bookingInfo.setMerchant_id(merchantId);

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
            clearSharedPreferences("retrieveMerchantIdFromSharedPreferencesB");
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

        Button buttonviewsessions1 = findViewById(R.id.buttonviewsession1);
        buttonviewsessions1.setOnClickListener(v -> {
            String selectedDate = ((TextView) findViewById(R.id.textdate)).getText().toString();
            getAllSessionsFromFirebase(selectedDate, "Session 1 ( 10AM - 2PM )");
        });

        Button buttonviewsessions2 = findViewById(R.id.buttonviewsession2);
        buttonviewsessions2.setOnClickListener(v -> {
            String selectedDate = ((TextView) findViewById(R.id.textdate)).getText().toString();
            getAllSessionsFromFirebase(selectedDate, "Session 2 ( 2PM - 6PM )");
        });

        Button buttonviewsessions3 = findViewById(R.id.buttonviewsession3);
        buttonviewsessions3.setOnClickListener(v -> {
            String selectedDate = ((TextView) findViewById(R.id.textdate)).getText().toString();
            getAllSessionsFromFirebase(selectedDate, "Session 3 ( 6PM - 10PM )");
        });
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
    private String retrieveMerchantIdFromSharedPreferencesB() {
        SharedPreferences preferencesB = getSharedPreferences("ServicePageDataB", Context.MODE_PRIVATE);
        return preferencesB.getString("merchant_id", "");
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
        TextView phonenumber = findViewById(R.id.valetphonenum);
        String nameTextView = preferencesD.getString("name", "-");
        String phonenumTextView = preferencesD.getString("phonenumber", "-");

        name.setText(nameTextView);
        phonenumber.setText(phonenumTextView);
    }
    private void retrieveDataFromSharedPreferencesE() {
        SharedPreferences preferencesE = getSharedPreferences("ServicePageDataE", Context.MODE_PRIVATE);

        TextView userPhone = findViewById(R.id.customerphonenum);
        TextView userAddress = findViewById(R.id.customeraddress);

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

    private void showSessionsDialog(String selectedDate, List<BookingInfo> sessions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sessions for " + selectedDate);

        List<BookingInfo> filteredSessions = filterSessionsByDate(sessions, selectedDate);

        StringBuilder sessionInfo = new StringBuilder();
        for (BookingInfo session : filteredSessions) {
            sessionInfo.append("").append(session.getSession()).append("\n");
            sessionInfo.append("No. Plate: ").append(session.getNoPlate()).append("\n");
            sessionInfo.append("Time Start: ").append(session.getTimeStart()).append("\n");
            sessionInfo.append("Time Finish: ").append(session.getTimeFinish()).append("\n\n\n");
        }

        if (sessionInfo.length() > 0) {
            builder.setMessage(sessionInfo.toString());
        } else {
            builder.setMessage("No sessions found for the selected date.");
        }

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private List<BookingInfo> filterSessionsByDate(List<BookingInfo> sessions, String selectedDate) {
        List<BookingInfo> filteredSessions = new ArrayList<>();

        for (BookingInfo session : sessions) {
            if (session.getDate().equals(selectedDate)) {
                filteredSessions.add(session);
            }
        }

        return filteredSessions;
    }
    private void getAllSessionsFromFirebase(String selectedDate, String targetSession) {
        List<BookingInfo> sessions = new ArrayList<>();

        sessionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingInfo session = snapshot.getValue(BookingInfo.class);
                    if (session != null && session.getDate().equals(selectedDate) && session.getSession().equals(targetSession)) {
                        sessions.add(session);
                    }
                }

                showSessionsDialog(selectedDate, sessions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void saveBookingInfoToFirebase(BookingInfo bookingInfo) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the unique ID of the current user
            String userId = currentUser.getUid();

            // Reference to the Customer node in your database
            DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(userId);

            // Reference to the BookingInfo node under the BookingInfo node (not under the Customer node)
            DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("BookingInfo");

            // Generate a unique ID for the booking
            String bookingId = bookingRef.push().getKey();

            // Set the customer_id field in the BookingInfo
            bookingInfo.setCustomer_id(userId);

            // Save the booking information under the BookingInfo node
            bookingRef.child(bookingId).setValue(bookingInfo);
        }
    }
    private void clearSharedPreferences(String sharedPreferencesName) {
        SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}