package com.example.carwashy.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carwashy.Login;
import com.example.carwashy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordPage extends AppCompatActivity {

    Button btnreset,home;
    EditText email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword); btnreset = findViewById(R.id.btnreset);
        home = findViewById(R.id.home);
        email = findViewById(R.id.email);
        firebaseAuth = FirebaseAuth.getInstance();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordPage.this, Login.class);
                startActivity(intent);
                finish();

            }
        });
        btnreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ForgotPasswordPage.this,
                                            "Check Your Email to Restore Password",
                                            Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ForgotPasswordPage.this,
                                            "This Email Does Not Exist",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

    }
}