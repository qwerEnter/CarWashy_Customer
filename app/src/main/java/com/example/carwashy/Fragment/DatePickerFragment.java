package com.example.carwashy.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            // Check if the selected date is a Sunday
            if (isSunday(year1, monthOfYear, dayOfMonth)) {
                Toast.makeText(requireContext(), "Premise closed on Sundays. Please reselect your booking date!", Toast.LENGTH_SHORT).show();
            } else {
                // Pass the selected date to the activity's onDateSet method
                ((DatePickerDialog.OnDateSetListener) getActivity()).onDateSet(view, year1, monthOfYear, dayOfMonth);
            }
        }, year, month, day);

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // 1000 milliseconds = 1 second

        // Set the maximum date to one week (30 days) from the current date
        c.add(Calendar.DAY_OF_MONTH, 30);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        return datePickerDialog;
    }

    // Helper method to check if a given date is a Sunday
    private boolean isSunday(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
}
