package com.example.carwashy.UI;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.carwashy.Fragment.DatePickerFragment;
import com.example.carwashy.Fragment.SessionDialogFragment;
import com.example.carwashy.R;

import java.text.DateFormat;
import java.util.Calendar;

public class ReBookPage extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener, SessionDialogFragment.SessionDialogListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.re_book_page);

        Button bookingdate = findViewById(R.id.bookingdate);
        bookingdate.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        });

        Button bookingsession = findViewById(R.id.bookingsession);
        bookingsession.setOnClickListener(v -> showSessionDialog());

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

}