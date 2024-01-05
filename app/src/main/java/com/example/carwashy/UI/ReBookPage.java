package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.carwashy.Fragment.DatePickerFragment;
import com.example.carwashy.Fragment.SessionDialogFragment;
import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReBookPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, SessionDialogFragment.SessionDialogListener {

    private DatabaseReference bookingInfoReference;
    private DatabaseReference sessionReference;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_book_page);
        sessionReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");
        // Initialize Firebase reference
        bookingInfoReference = FirebaseDatabase.getInstance().getReference("BookingInfo");

        // Retrieve the noPlate and date values from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataBookingStatus", MODE_PRIVATE);
        String noPlate = preferences.getString("noPlate", "");
        String date = preferences.getString("date", "");

        // Set the noPlate value to the searchlicense TextView
        TextView textdate = findViewById(R.id.textdate);
        textdate.setText(date);

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
        // Button to open date picker
        Button bookingdate = findViewById(R.id.bookingdate);
        bookingdate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        // Button to open session dialog
        Button bookingsession = findViewById(R.id.bookingsession);
        bookingsession.setOnClickListener(v -> showSessionDialog());

        // Button to save updates
        Button buttonsave = findViewById(R.id.buttonbook);
        buttonsave.setOnClickListener(view -> {
            // Get the updated date and session values
            String updatedDate = ((TextView) findViewById(R.id.textdate)).getText().toString();
            String updatedSession = ((TextView) findViewById(R.id.textsession)).getText().toString();

            // Show ProgressDialog
            showProgressDialog();

            // Perform the update in Firebase
            updateBookingInfo(noPlate, date, updatedDate, updatedSession);

            dismissProgressDialog();
            Intent intent = new Intent(ReBookPage.this, BookingStatusPage.class);
            startActivity(intent);
        });
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

    @Override
    public void onSessionSelected(String session) {
        TextView textsession = findViewById(R.id.textsession);
        textsession.setText(session);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false); // Optional: Prevent the user from canceling the dialog
        AlertDialog progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        // Dismiss the ProgressDialog
        // Make sure that progressDialog is declared as a class variable
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void updateBookingInfo(String noPlate, String currentDate, String updatedDate, String updatedSession) {
        // Search for the BookingInfo node with the specified noPlate and currentDate
        bookingInfoReference.orderByChild("noPlate").equalTo(noPlate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingInfo bookingInfo = snapshot.getValue(BookingInfo.class);
                    if (bookingInfo != null && bookingInfo.getDate().equals(currentDate)) {
                        // Update the date and session
                        snapshot.getRef().child("date").setValue(updatedDate);
                        snapshot.getRef().child("session").setValue(updatedSession);
                        snapshot.getRef().child("status").setValue("Pending");

                        // Dismiss the ProgressDialog
                        dismissProgressDialog();

                        return;
                    }
                }
                // No matching data found
                dismissProgressDialog(); // Dismiss ProgressDialog in case of no match
                Toast.makeText(ReBookPage.this, "Data not found for update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                dismissProgressDialog(); // Dismiss ProgressDialog in case of an error
                Toast.makeText(ReBookPage.this, "Error updating data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
}
