package com.example.carwashy.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReceiptPage2 extends AppCompatActivity {

    private ImageView imagereceipt;
    private DatabaseReference receiptReference;
    private String noPlate;
    private String date;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt2);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();
        imagereceipt = findViewById(R.id.imagereceipt);

        receiptReference = FirebaseDatabase.getInstance().getReference("BookingInfo");

        // Retrieve the noPlate value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataBookingStatus", MODE_PRIVATE);
        noPlate = preferences.getString("noPlate", "");
        date = preferences.getString("date", "");

        TextView textdate = findViewById(R.id.date);
        TextView textnoplate = findViewById(R.id.noplate);
        textdate.setText(date);
        textnoplate.setText(noPlate);

        retrievereceipt();
    }


    private void retrievereceipt() {
        DatabaseReference userReceiptReference = FirebaseDatabase.getInstance().getReference("Customer")
                .child(userId)
                .child("BookingInfo");

        userReceiptReference.orderByChild("noPlate").equalTo(noPlate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingInfo bookingInfo = snapshot.getValue(BookingInfo.class);
                    if (bookingInfo != null && bookingInfo.getDate().equals(date)) {
                        String receipt = bookingInfo.getReceipt();

                        // Check if the receipt URL is not empty or "-"
                        if (!"".equals(receipt)) {
                            Log.d("ReceiptPage2", "Receipt URL: " + receipt);
                            Picasso.get().load(receipt).into(imagereceipt);
                            Toast.makeText(ReceiptPage2.this, "Receipt found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReceiptPage2.this, "Receipt not found", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                }
                // No matching data found
                Toast.makeText(ReceiptPage2.this, "Data not found for retrieval", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ReceiptPage2.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
