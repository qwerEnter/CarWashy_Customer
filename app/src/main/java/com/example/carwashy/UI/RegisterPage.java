package com.example.carwashy.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carwashy.Login;
import com.example.carwashy.Model.Customer;
import com.example.carwashy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class RegisterPage extends AppCompatActivity {

    EditText email,password;
    TextView contactus;
    Button btnsignup;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnsignup = findViewById(R.id.btnsignup);
        contactus = findViewById(R.id.contactus);

        //contactus
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = "imannajmi156@gmail.com";
                String subject = "Feedback or Support";
                String body = "Please provide your feedback or describe your issue here.";

                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                PackageManager packageManager = getPackageManager();
                // Query for activities that can handle the email intent
                List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
                if (!activities.isEmpty()) {
                    // If there are activities that can handle the intent, show the chooser dialog
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } else {
                    // Display a dialog or show a Toast message indicating no email app found
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPage.this);
                    builder.setTitle("No Email App Found")
                            .setMessage("Please contact us at imannajmi156@gmail.com for any feedback or support.")
                            .setPositiveButton("Copy Email", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("email", emailAddress);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(RegisterPage.this, "Email copied to clipboard", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }

            }
        });

        //nak register
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createUser();


            }
        });
    }

    //create user
    private void createUser() {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(RegisterPage.this, "Enter Your Email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(RegisterPage.this, "Enter Your Password", Toast.LENGTH_LONG).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(RegisterPage.this, "Password Must Be More Than 6", Toast.LENGTH_LONG).show();
            return;
        }

        //create user
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Use the correct constructor here
                    Customer userModel = new Customer(userEmail, userPassword, "", "", "");
                    String id = task.getResult().getUser().getUid();
                    database.getReference().child("Customer").child(id).setValue(userModel);

                    Toast.makeText(RegisterPage.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterPage.this, "Welcome", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterPage.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

