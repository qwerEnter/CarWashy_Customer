package com.example.carwashy.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    ProgressBar progressBarLogin;
    EditText email,password;
    TextView textView;
    Button btnregister,btnlogin;
    FirebaseAuth mAuth;

//     stay in home page if still online
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {}
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        setContentView(R.layout.login);

        //login with email and password
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnregister = findViewById(R.id.btnregister);
        btnlogin = findViewById(R.id.btnlogin);
        textView = findViewById(R.id.textViewreset);

        //button register
        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordPage.class);
            startActivity(intent);
            finish();

        });

        //button register
        btnregister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterPage.class);
            startActivity(intent);
            finish();

        });

        //button login
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
}

    //login with email n password
    private void loginUser() {

        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if(TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userPassword))
        {
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userPassword.length()<6)
        {
            Toast.makeText(this, "Password Must Be More Than 6", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an AlertDialog with a custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(false); // Optional: Prevent user from canceling the dialog
        AlertDialog progressDialog = builder.create();
        progressDialog.show();

        //login user db
        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Dismiss the AlertDialog
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    Intent splashIntent = new Intent(Login.this, SplashScreenActivity.class);
                    startActivity(splashIntent);
                    finish();
                }
                else
                {
                    Toast.makeText(Login.this, "This Account Does Not Exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}