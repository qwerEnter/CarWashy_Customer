package com.example.carwashy.UI;

import static com.example.carwashy.Model.BookingInfo.convertJsonToServicesList;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.ServiceAdapter;
import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.Model.CarWashRecord;
import com.example.carwashy.Model.Service;
import com.example.carwashy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ReceiptPage extends AppCompatActivity {

    private DatabaseReference receiptReference;
    private List<Service> passServices = new ArrayList<>();
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private ImageView imagereceipt;
    private StorageReference storageRef;
    private CarWashRecord carwashrecInfo;
    private String downloadUrl;
    private ServiceAdapter serviceAdapter;
    Uri fileUri;


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
                                    imagereceipt.setImageURI(selectedImageUri);
                                    imagereceipt.setTag(selectedImageUri.toString());
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        imagereceipt = findViewById(R.id.imagereceipt);

        // Retrieve the noPlate value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("dataNoplate", MODE_PRIVATE);
        String noPlate = preferences.getString("noPlate", "");

        // Set the noPlate value to the searchlicense TextView
        TextView searchlicense = findViewById(R.id.searchlicense);
        searchlicense.setText(noPlate);
        receiptReference = FirebaseDatabase.getInstance().getReference("BookingInfo");

        if (!noPlate.isEmpty()) {
            // Perform the search operation using noPlate
            retrieveBookingInfoData(noPlate);
        }

        receiptReference = FirebaseDatabase.getInstance().getReference("BookingInfo");



        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(v -> {
            String licensePlate = searchlicense.getText().toString().trim();
            if (!licensePlate.isEmpty()) {
                retrieveBookingInfoData(licensePlate);
            } else {
                Toast.makeText(this, "Please enter a license plate number", Toast.LENGTH_SHORT).show();
            }
        });


        Button buttonsave = findViewById(R.id.buttonsave);
        buttonsave.setOnClickListener(view -> {

            CarWashRecord carwashrecInfo = new CarWashRecord();
            carwashrecInfo.setDate(((TextView) findViewById(R.id.textdate)).getText().toString());
            carwashrecInfo.setSession(((TextView) findViewById(R.id.textsession)).getText().toString());
            carwashrecInfo.setNoPlate(((TextView) findViewById(R.id.noplate)).getText().toString());
            carwashrecInfo.setCarName(((TextView) findViewById(R.id.carname)).getText().toString());
            carwashrecInfo.setTotalcost(((TextView) findViewById(R.id.cost)).getText().toString());
            carwashrecInfo.setAddress(((TextView) findViewById(R.id.address)).getText().toString());
            carwashrecInfo.setValet(((TextView) findViewById(R.id.valet)).getText().toString());
            carwashrecInfo.setCurrentaddress(((TextView) findViewById(R.id.useraddress)).getText().toString());
            carwashrecInfo.setPhone(((TextView) findViewById(R.id.usercurrentphone)).getText().toString());
            carwashrecInfo.setPhone(((TextView) findViewById(R.id.usercurrentphone)).getText().toString());

            // Retrieve the list of services from SharedPreferences
            List<Service> services = retrieveServicesFromSharedPreferences();
            // Convert the list of services to a JSON string
            String carsetJson = CarWashRecord.convertServicesListToJson(services);
            // Set the carsetJson in carwashrecInfo
            carwashrecInfo.setCarsetJson(carsetJson);

            // Check if an image has been selected
            if (imagereceipt.getTag() != null) {
                Uri receipt = Uri.parse(imagereceipt.getTag().toString());

                // Save the CarWashRecord along with the image
                saveCarWashRecordToFirebase(carwashrecInfo, receipt);
                // Show a Toast message indicating successful save
                Toast.makeText(ReceiptPage.this, "Receipt Successfully Saved", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    // Navigate to BookingStatusPage
                    Intent intent = new Intent(ReceiptPage.this, BookingStatusPage.class);
                    startActivity(intent);
                }, 2000); // 3000 milliseconds (3 seconds) delay, adjust as needed

            }
            else {
                // Handle the case where no image has been selected
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }


        });



        carwashrecInfo = new CarWashRecord();

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

    private void retrieveBookingInfoData(String licensePlate) {
        // Query Firebase for the receipt data
        receiptReference.orderByChild("noPlate").equalTo(licensePlate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the first matching receipt
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        BookingInfo receipt = snapshot.getValue(BookingInfo.class);
                        if (receipt != null) {
                            // Display the data on the UI
                            displayBookingInfo(receipt);
                            // Save all data in SharedPreferences named "bookingServiceData"

                        }
                    }
                } else {
                    Toast.makeText(ReceiptPage.this, "No data found for the entered license plate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ReceiptPage.this, "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateRecyclerView(List<Service> services) {
        // Find the RecyclerView
        RecyclerView rvcarset = findViewById(R.id.rvcarset);

        // Set up the RecyclerView with the ServiceAdapter
        ServiceAdapter serviceAdapter = new ServiceAdapter(new ArrayList<>(), getSupportFragmentManager());
        rvcarset.setLayoutManager(new LinearLayoutManager(ReceiptPage.this));
        rvcarset.setAdapter(serviceAdapter);

        // Update the serviceList in the adapter
        serviceAdapter.setServiceList(services);
        // Notify the adapter that the data has changed
        serviceAdapter.notifyDataSetChanged();
    }
    private void displayBookingInfo(BookingInfo receipt) {
        // Display the data on the UI
        final TextView textdate = findViewById(R.id.textdate);
        textdate.setText(receipt.getDate());

        final TextView textsession = findViewById(R.id.textsession);
        textsession.setText(receipt.getSession());

        final TextView noplate = findViewById(R.id.noplate);
        noplate.setText(receipt.getNoPlate());

        final TextView carname = findViewById(R.id.carname);
        carname.setText(receipt.getCarName());

        final TextView cost = findViewById(R.id.cost);
        cost.setText(receipt.getTotalcost());

        final TextView address = findViewById(R.id.address);
        address.setText(receipt.getAddress());

        final TextView valet = findViewById(R.id.valet);
        valet.setText(receipt.getValet());

        final TextView useraddress = findViewById(R.id.useraddress);
        useraddress.setText(receipt.getCurrentaddress());

        final TextView usercurrentphone = findViewById(R.id.usercurrentphone);
        usercurrentphone.setText(receipt.getPhone());

        // Retrieve the JSON string from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("bookingServiceData", MODE_PRIVATE);
        String carsetJson = preferences.getString("carsetJson", "");

        // Convert the JSON string to a list
        List<Service> services = convertJsonToServicesList(carsetJson);

        // Update the RecyclerView
        updateRecyclerView(services);
        saveBookingInfoToSharedPreferences(receipt);
    }
    private void saveBookingInfoToSharedPreferences(BookingInfo receipt) {
        SharedPreferences preferences = getSharedPreferences("bookingServiceData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Retrieve the JSON string from BookingInfo object
        String carsetJson = receipt.getCarsetJson();

        // Save the JSON string in SharedPreferences
        editor.putString("carsetJson", carsetJson);

        editor.apply();
    }


    private List<Service> retrieveServicesFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("bookingServiceData", MODE_PRIVATE);
        String carsetJson = preferences.getString("carsetJson", "");

        // Convert the JSON string to a list of services
        return convertJsonToServicesList(carsetJson);
    }
    private void saveCarWashRecordToFirebase(CarWashRecord carwashrecInfo, Uri receipt) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference carwashrecRef = database.getReference("CarWashRecord");

        String carwashrecId = carwashrecRef.push().getKey();

        // Get a reference to the Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("receipts").child(carwashrecId + ".jpg");

        // Upload the file to Firebase Storage
        storageRef.putFile(receipt)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // You can retrieve the download URL if needed: taskSnapshot.getDownloadUrl()
                    // Get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the URL to Realtime Database
                        carwashrecRef.child(carwashrecId).child("receipt").setValue(uri.toString());
                    }).addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(exception -> {
                    // Handle failures
                    Toast.makeText(this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
        carwashrecRef.child(carwashrecId).setValue(carwashrecInfo)
                .addOnSuccessListener(aVoid -> {
                    // Success
                    Log.d("ReceiptPage", "CarWashRecord saved successfully");
                })
                .addOnFailureListener(e -> {
                    // Failure
                    Log.e("ReceiptPage", "Failed to save CarWashRecord: " + e.getMessage());
                });
    }



}