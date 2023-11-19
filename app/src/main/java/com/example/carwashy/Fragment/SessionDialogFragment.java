package com.example.carwashy.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.carwashy.R;

public class SessionDialogFragment extends DialogFragment {

    public interface SessionDialogListener {
        void onSessionSelected(String session);
    }

    private SessionDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_session_details, null);
        builder.setTitle("                        Select Booking Session");

        // Add radio buttons
        final String[] sessions = {"Session 1 ( 10AM - 2PM )", "Session 2 ( 2PM - 6PM )", "Session 3 ( 6PM - 10PM )"};
        final RadioGroup sessionGroup = view.findViewById(R.id.sessionGroup);
        for (String session : sessions) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(session);
            sessionGroup.addView(radioButton);
        }

        Button addServiceButton = view.findViewById(R.id.addServiceButton);
        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = sessionGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = sessionGroup.findViewById(selectedId);
                    String selectedSession = selectedRadioButton.getText().toString();
                    listener.onSessionSelected(selectedSession);
                    dismiss(); // Close the dialog after selecting a session
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SessionDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SessionDialogListener");
        }
    }
}
