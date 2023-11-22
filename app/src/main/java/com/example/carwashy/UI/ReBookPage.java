package com.example.carwashy.UI;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.carwashy.Fragment.DatePickerFragment;
import com.example.carwashy.Fragment.SessionDialogFragment;
import com.example.carwashy.R;
import com.example.carwashy.Model.BookingInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class ReBookPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, SessionDialogFragment.SessionDialogListener {

    private DatabaseReference bookingInfoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_book_page);

        // Initialize Firebase reference
        bookingInfoReference = FirebaseDatabase.getInstance().getReference("BookingInfo");

        // Retrieve the noPlate and date values from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataRebook", MODE_PRIVATE);
        String noPlate = preferences.getString("noPlate", "");
        String date = preferences.getString("date", "");

        // Set the noPlate value to the searchlicense TextView
        TextView textdate = findViewById(R.id.textdate);
        textdate.setText(date);

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

            // Perform the update in Firebase
            updateBookingInfo(noPlate, date, updatedDate, updatedSession);
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
                        // Show a success message
                        Toast.makeText(ReBookPage.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // No matching data found
                Toast.makeText(ReBookPage.this, "Data not found for update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ReBookPage.this, "Error updating data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
