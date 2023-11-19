// EditProfilePage.java

package com.example.carwashy.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carwashy.Model.Customer;
import com.example.carwashy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfilePage extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editprofile);

        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phonenum);
        addressEditText = findViewById(R.id.address);

        loadUserProfile();

        Button saveButton = findViewById(R.id.buttoneditprofile);
        saveButton.setOnClickListener(view -> saveUserProfileChanges());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Customer")
                    .child(currentUser.getUid());

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Customer customer = dataSnapshot.getValue(Customer.class);

                        // Set existing values to EditTexts
                        if (customer != null) {
                            nameEditText.setText(customer.getName());
                            phoneEditText.setText(customer.getPhonenumber());
                            addressEditText.setText(customer.getAddress());
                            // Password is not recommended to be displayed or edited

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void saveUserProfileChanges() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Customer")
                    .child(currentUser.getUid());



            // Update values in Firebase
            userReference.child("name").setValue(nameEditText.getText().toString());
            userReference.child("phonenumber").setValue(phoneEditText.getText().toString());
            userReference.child("address").setValue(addressEditText.getText().toString());

            // Password is not recommended to be changed programmatically

            // Optionally, you might want to show a success message
            // or navigate back to the profile page.
            // For simplicity, we finish the activity here.
            finish();
        }
    }
}
