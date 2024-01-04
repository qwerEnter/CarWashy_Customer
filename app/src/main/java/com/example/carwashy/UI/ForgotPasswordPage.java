package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordPage extends AppCompatActivity {

    Button btnreset,home;
    EditText email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        setContentView(R.layout.forgotpassword);
        btnreset = findViewById(R.id.btnreset);
        home = findViewById(R.id.home);
        email = findViewById(R.id.email);
        firebaseAuth = FirebaseAuth.getInstance();

        home.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordPage.this, Login.class);
            startActivity(intent);
            finish();

        });

        btnreset.setOnClickListener(view -> {
            String userEmail = email.getText().toString();

            // Create an AlertDialog with a custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
            builder.setView(dialogView);
            builder.setCancelable(false); // Optional: Prevent user from canceling the dialog
            AlertDialog progressDialog = builder.create();
            progressDialog.show();

            firebaseAuth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordPage.this,
                                    "Check Your Email to Restore Password",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPasswordPage.this,
                                    "This Email Does Not Exist",
                                    Toast.LENGTH_LONG).show();
                        }

                        // Dismiss the AlertDialog
                        progressDialog.dismiss();
                    });
        });

    }
}