package com.example.carwashy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.R;
import com.example.carwashy.UI.ReBookPage;
import com.example.carwashy.UI.ReceiptPage;
import com.example.carwashy.UI.ReceiptPage2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.ViewHolder> {

    private List<BookingInfo> bookingstatusList;
    private Context context;

    public BookingStatusAdapter(List<BookingInfo> bookingstatusList) {
        this.bookingstatusList = bookingstatusList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookingstatus_item, parent, false);
        return new ViewHolder(view, parent.getContext());
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookingInfo status = bookingstatusList.get(position);


        holder.noplate.setText(status.getNoPlate());
        holder.status.setText(status.getStatus());
        holder.bookingdate.setText(status.getDate());
        holder.session.setText(status.getSession());

        // Show/hide buttons based on the status
        if ("Available".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.VISIBLE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
            holder.buttonRebook.setVisibility(View.GONE);
            holder.buttonVerify.setVisibility(View.GONE);
        }
        else if ("Pending".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.VISIBLE);
            holder.buttonView.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
            holder.buttonRebook.setVisibility(View.GONE);
            holder.buttonVerify.setVisibility(View.GONE);
        }
        else if ("Paid".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
            holder.buttonRebook.setVisibility(View.GONE);
            holder.buttonVerify.setVisibility(View.VISIBLE);
        }

        else if ("Full Slot".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonRebook.setVisibility(View.VISIBLE);
            holder.buttonVerify.setVisibility(View.GONE);
        }

        else if ("Payment Verified".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.VISIBLE);
            holder.buttonDelete.setVisibility(View.GONE);
            holder.buttonRebook.setVisibility(View.GONE);
            holder.buttonVerify.setVisibility(View.GONE);
        }


        BookingInfo currentItem = bookingstatusList.get(position);

        // Show/hide Waze icon based on valet value
        if(isValetNone(currentItem)) {
            holder.cardViewWaze.setVisibility(View.VISIBLE);
            holder.cardViewValet.setVisibility(View.GONE);
        } else {
            holder.cardViewWaze.setVisibility(View.GONE);
            holder.cardViewValet.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return bookingstatusList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noplate, status, bookingdate,session;
        private Context context;
        CardView cardViewWaze,cardViewValet;
        Button buttonPay,buttonCancel,buttonView,buttonDelete, buttonRebook, buttonVerify;
        DatabaseReference databaseReference; // Add this reference


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            noplate = itemView.findViewById(R.id.noplate);
            status = itemView.findViewById(R.id.status);
            bookingdate = itemView.findViewById(R.id.bookingdate);
            session = itemView.findViewById(R.id.session);
            cardViewWaze = itemView.findViewById(R.id.cardViewWaze);
            cardViewValet = itemView.findViewById(R.id.cardViewValet);

            databaseReference = FirebaseDatabase.getInstance().getReference("BookingInfo"); // Replace with your actual database path

            // Initialize the button reference
            buttonPay = itemView.findViewById(R.id.buttonpay);
            buttonRebook = itemView.findViewById(R.id.buttonrebook);
            buttonView = itemView.findViewById(R.id.buttonview);
            buttonCancel = itemView.findViewById(R.id.buttoncancel);
            buttonDelete = itemView.findViewById(R.id.buttondelete);
            buttonVerify = itemView.findViewById(R.id.buttonverify);

            buttonVerify.setOnClickListener(view -> {
                // Get the current BookingInfo object
                BookingInfo currentItem = bookingstatusList.get(getAdapterPosition());

                // Check if currentItem is not null
                if (currentItem != null) {
                    // Get the noPlate value
                    String noPlate = currentItem.getNoPlate();
                    String date = currentItem.getDate();

                    // Save the noPlate value to SharedPreferences
                    saveNoPlateToSharedPreferences(context, noPlate, date);

                    // Start the ReceiptPage activity
                    Intent intent = new Intent(context, ReceiptPage2.class);
                    context.startActivity(intent);
                }
            });

            buttonPay.setOnClickListener(view -> {
                // Get the current BookingInfo object
                BookingInfo currentItem = bookingstatusList.get(getAdapterPosition());

                // Check if currentItem is not null
                if (currentItem != null) {
                    // Get the noPlate value
                    String noPlate = currentItem.getNoPlate();
                    String date = currentItem.getDate();

                    // Save the noPlate value to SharedPreferences
                    saveNoPlateToSharedPreferences(context, noPlate, date);

                    // Start the ReceiptPage activity
                    Intent intent = new Intent(context, ReceiptPage.class);
                    context.startActivity(intent);
                }
            });
            buttonRebook.setOnClickListener(view -> {
                BookingInfo currentItem = bookingstatusList.get(getAdapterPosition());

                // Check if currentItem is not null
                if (currentItem != null) {
                    // Get the noPlate value
                    String noPlate = currentItem.getNoPlate();
                    String date = currentItem.getDate();

                    // Save the noPlate value to SharedPreferences
                    saveNoPlateToSharedPreferences(context, noPlate, date);

                    // Start the ReceiptPage activity
                    Intent intent = new Intent(context, ReBookPage.class);
                    context.startActivity(intent);
                }
            });

            buttonCancel.setOnClickListener(view -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < bookingstatusList.size()) {
                    BookingInfo currentItem = bookingstatusList.get(adapterPosition);

                    if (currentItem != null) {
                        // Show a confirmation dialog
                        new AlertDialog.Builder(context)
                                .setTitle("Booking Cancellation")
                                .setMessage("Are you sure you want to cancel this booking?")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    // User clicked Yes, proceed with cancellation
                                    String date = currentItem.getDate();
                                    String noPlate = currentItem.getNoPlate();

                                    databaseReference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            List<DataSnapshot> snapshotsToRemove = new ArrayList<>();

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                BookingInfo bookingInfo = snapshot.getValue(BookingInfo.class);
                                                if (bookingInfo != null && noPlate.equals(bookingInfo.getNoPlate())) {
                                                    snapshotsToRemove.add(snapshot);
                                                }
                                            }

                                            // Remove the entries from the database
                                            for (DataSnapshot snapshotToRemove : snapshotsToRemove) {
                                                snapshotToRemove.getRef().removeValue().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        // Entry removed successfully
                                                        // Check if adapterPosition is still valid before updating the UI
                                                        if (adapterPosition < bookingstatusList.size()) {
                                                            // Notify the adapter about the removal
                                                            bookingstatusList.remove(adapterPosition);
                                                            notifyItemRemoved(adapterPosition);
                                                            Log.d("Firebase", "Successfully removed data");

                                                            // Redirect to the same page (refresh the current activity)
                                                            Intent intent = new Intent(context, context.getClass());
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(intent);
                                                        }
                                                    } else {
                                                        // Handle failure
                                                        if (task.getException() != null) {
                                                            Log.e("Firebase", "Error removing data: " + task.getException());
                                                        } else {
                                                            Log.e("Firebase", "Error removing data: Unknown error");
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle errors
                                            Log.e("Firebase", "Error querying data: " + databaseError.getMessage());
                                        }
                                    });
                                })
                                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                    // User clicked No, do nothing
                                    dialog.dismiss();
                                })
                                .show();
                    }
                }
            });
            buttonDelete.setOnClickListener(view -> {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < bookingstatusList.size()) {
                    BookingInfo currentItem = bookingstatusList.get(adapterPosition);

                    if (currentItem != null) {
                        // Show a confirmation dialog
                        new AlertDialog.Builder(context)
                                .setTitle("Booking Cancellation")
                                .setMessage("Are you sure you want to cancel this booking?")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    // User clicked Yes, proceed with cancellation
                                    String date = currentItem.getDate();
                                    String noPlate = currentItem.getNoPlate();

                                    databaseReference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            List<DataSnapshot> snapshotsToRemove = new ArrayList<>();

                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                BookingInfo bookingInfo = snapshot.getValue(BookingInfo.class);
                                                if (bookingInfo != null && noPlate.equals(bookingInfo.getNoPlate())) {
                                                    snapshotsToRemove.add(snapshot);
                                                }
                                            }

                                            // Remove the entries from the database
                                            for (DataSnapshot snapshotToRemove : snapshotsToRemove) {
                                                snapshotToRemove.getRef().removeValue().addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        // Entry removed successfully
                                                        // Check if adapterPosition is still valid before updating the UI
                                                        if (adapterPosition < bookingstatusList.size()) {
                                                            // Notify the adapter about the removal
                                                            bookingstatusList.remove(adapterPosition);
                                                            notifyItemRemoved(adapterPosition);
                                                            Log.d("Firebase", "Successfully removed data");

                                                            // Redirect to the same page (refresh the current activity)
                                                            Intent intent = new Intent(context, context.getClass());
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(intent);
                                                        }
                                                    } else {
                                                        // Handle failure
                                                        if (task.getException() != null) {
                                                            Log.e("Firebase", "Error removing data: " + task.getException());
                                                        } else {
                                                            Log.e("Firebase", "Error removing data: Unknown error");
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle errors
                                            Log.e("Firebase", "Error querying data: " + databaseError.getMessage());
                                        }
                                    });
                                })
                                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                    // User clicked No, do nothing
                                    dialog.dismiss();
                                })
                                .show();
                    }
                }
            });
        }
    }


    private void saveNoPlateToSharedPreferences(Context context,String noPlate,String date) {
        SharedPreferences preferences = context.getSharedPreferences("dataBookingStatus", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save the noPlate value to SharedPreferences
        editor.putString("noPlate", noPlate);
        editor.putString("date", date);
        editor.apply();
    }

    private boolean isValetNone(BookingInfo bookingInfo) {
        return bookingInfo != null && "-".equals(bookingInfo.getValet());
    }
}
