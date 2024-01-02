package com.example.carwashy.UI;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.Model.CarWashRecord;
import com.example.carwashy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ReceiptPage extends AppCompatActivity {

    private DatabaseReference receiptReference;
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ImageView imagereceipt;
    private StorageReference storageRef;
    private CarWashRecord carwashrecInfo;
    private String downloadUrl;
    Uri fileUri;
    private Uri selectedImageUri;
    private String noPlate;
    private String date;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                // The user has selected an image from the gallery
                                selectedImageUri = result.getData().getData();
                                imagereceipt.setImageURI(selectedImageUri);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        receiptReference = FirebaseDatabase.getInstance().getReference("BookingInfo");

        imagereceipt = findViewById(R.id.imagereceipt);

        // Retrieve the noPlate value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataBookingStatus", MODE_PRIVATE);
        noPlate = preferences.getString("noPlate", "");
        date = preferences.getString("date", "");

        Button buttonsave = findViewById(R.id.buttonsave);
        buttonsave.setOnClickListener(view -> {
            if (selectedImageUri != null) {
                // Upload the receipt image to Firebase Storage and get the download URL
                uploadReceiptImage(selectedImageUri);
            } else {
                Toast.makeText(ReceiptPage.this, "Please select a receipt image", Toast.LENGTH_SHORT).show();
            }
        });

        // Remove the local declaration of storageRef
        storageRef = FirebaseStorage.getInstance().getReference("receipts");

        // Set OnClickListener for the imagecar
        imagereceipt.setOnClickListener(v -> {
            // Check for permission before launching the gallery
            if (ContextCompat.checkSelfPermission(ReceiptPage.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if it's not granted
                ActivityCompat.requestPermissions(ReceiptPage.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // Permission already granted, launch the gallery
                launchGallery();
            }
        });
    }

    private void uploadReceiptImage(Uri imageUri) {
        // Create a unique filename for the image
        String fileName = "receipt_" + System.currentTimeMillis();

        // Create a reference to the file in Firebase Storage
        StorageReference imageRef = storageRef.child(fileName);

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the download URL in Firebase Database
                        updateBookingInfo(noPlate, date, uri.toString());

                    });
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Toast.makeText(ReceiptPage.this, "Error uploading receipt image", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch the gallery
                launchGallery();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void updateBookingInfo(String noPlate, String currentDate, String updatedReceipt) {
        // Search for the BookingInfo node with the specified noPlate and currentDate
        receiptReference.orderByChild("noPlate").equalTo(noPlate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BookingInfo bookingInfo = snapshot.getValue(BookingInfo.class);
                    if (bookingInfo != null && bookingInfo.getDate().equals(currentDate)) {
                        // Update the date and session
                        snapshot.getRef().child("receipt").setValue(updatedReceipt);
                        snapshot.getRef().child("status").setValue("Paid");
                        // Show a success message
                        Toast.makeText(ReceiptPage.this, "Receipt successfully uploaded", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            // Navigate to BookingStatusPage
                            Intent intent = new Intent(ReceiptPage.this, BookingStatusPage.class);
                            startActivity(intent);
                        }, 1000); // 3000 milliseconds (3 seconds) delay, adjust as needed

                        return;
                    }
                }
                // No matching data found
                Toast.makeText(ReceiptPage.this, "Data not found for update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(ReceiptPage.this, "Error updating data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
