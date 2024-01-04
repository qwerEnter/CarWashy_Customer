package com.example.carwashy.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.carwashy.Model.Vehicle;
import com.example.carwashy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class NewVehiclePage extends AppCompatActivity {

    private EditText carNameEditText, noPlateEditText, modelEditText;
    private RadioGroup radioGroup;
    private ImageView imagecar;

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private final int PICK_IMAGE_REQUEST = 1;

    // ActivityResultLauncher for picking an image
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK) {
                                // The user has selected an image from the gallery
                                Intent data = result.getData();
                                if (data != null) {
                                    Uri selectedImageUri = data.getData();
                                    imagecar.setImageURI(selectedImageUri);
                                    imagecar.setTag(selectedImageUri.toString());
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newvehicle);

        // Initialize UI components
        carNameEditText = findViewById(R.id.carname);
        noPlateEditText = findViewById(R.id.noplate);
        modelEditText = findViewById(R.id.model);
        radioGroup = findViewById(R.id.radioGroup);
        imagecar = findViewById(R.id.imagecar);

        // Save button click listener
        Button saveButton = findViewById(R.id.buttonaddvechicle);
        saveButton.setOnClickListener(view ->

                saveVehicleInfo());

        // Set OnClickListener for the imagecar
        imagecar.setOnClickListener(v -> {
            // Check for permission before launching the gallery
            if (ContextCompat.checkSelfPermission(NewVehiclePage.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission if it's not granted
                ActivityCompat.requestPermissions(NewVehiclePage.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                // Permission already granted, launch the gallery
                launchGallery();
            }
        });
    }

    private void saveVehicleInfo() {

        AlertDialog.Builder progressDialogBuilder = new AlertDialog.Builder(this);
        View progressDialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog, null);
        progressDialogBuilder.setView(progressDialogView);
        progressDialogBuilder.setCancelable(false); // Prevent user from canceling the dialog
        AlertDialog progressDialog = progressDialogBuilder.create();
        progressDialog.show();

        String carName = carNameEditText.getText().toString().trim();
        String noPlate = noPlateEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String imageUri = imagecar.getTag() != null ? imagecar.getTag().toString() : null;

        // Get selected radio button text
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String vehicleType = selectedRadioButton.getText().toString().trim();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the unique ID of the logged-in user
            String userId = currentUser.getUid();

            // Create a new Vehicle object
            Vehicle vehicle = new Vehicle(userId, carName, noPlate, model, vehicleType, imageUri);

            // Save the vehicle information to Firebase with a new auto-generated ID
            DatabaseReference vehicleReference  = FirebaseDatabase.getInstance().getReference()
                    .child("Vehicle")
                    .push(); // Generates a new unique ID for the vehicle


            vehicleReference.setValue(vehicle);

            // Check if an image is selected
            if (imageUri != null) {
                // Generate a unique image name (you may use your own logic)
                String imageName = "vehicle_image_" + System.currentTimeMillis();

                // Upload the image to Firebase Storage and save URL to Realtime Database
                uploadImageToFirebaseStorage(Uri.parse(imageUri), imageName, vehicleReference);
            }
            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                // Optionally, show a success message or navigate to another page
                Toast.makeText(NewVehiclePage.this, "Vehicle information saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            }, 2000); // 2000 milliseconds delay
        }
    }

    // Handle the result of the permission request
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

    // Launch the gallery
    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri, String imageName, DatabaseReference vehicleReference) {
        // Get a reference to the Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("car").child(imageName);

        // Upload the file to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // You can retrieve the download URL if needed: taskSnapshot.getDownloadUrl()

                    // Get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the URL to Realtime Database
                        vehicleReference.child("imageUri").setValue(uri.toString());
                    }).addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle failures
                    Toast.makeText(this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
