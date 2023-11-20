package com.example.carwashy.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.carwashy.UI.ReceiptPage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        holder.timestart.setText(status.getTimeStart());
        holder.timefinish.setText(status.getTimeFinish());

        // Show/hide buttons based on the status
        if ("Accept".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.VISIBLE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.GONE);
        }
        else if ("Pending".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.VISIBLE);
            holder.buttonView.setVisibility(View.GONE);
        }
        else if ("Paid".equals(status.getStatus())) {
            holder.buttonPay.setVisibility(View.GONE);
            holder.buttonCancel.setVisibility(View.GONE);
            holder.buttonView.setVisibility(View.VISIBLE);
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

        TextView noplate, status, bookingdate,timestart,timefinish;
        private Context context;
        CardView cardViewWaze,cardViewValet;

        Button buttonPay,buttonCancel,buttonView;

        DatabaseReference databaseReference; // Add this reference


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            noplate = itemView.findViewById(R.id.noplate);
            status = itemView.findViewById(R.id.status);
            bookingdate = itemView.findViewById(R.id.bookingdate);
            timestart = itemView.findViewById(R.id.timestart);
            timefinish = itemView.findViewById(R.id.timefinish);
            cardViewWaze = itemView.findViewById(R.id.cardViewWaze);
            cardViewValet = itemView.findViewById(R.id.cardViewValet);

            databaseReference = FirebaseDatabase.getInstance().getReference("BookingInfo"); // Replace with your actual database path


            // Initialize the button reference
            buttonPay = itemView.findViewById(R.id.buttonpay);
            buttonView = itemView.findViewById(R.id.buttonview);
            buttonCancel = itemView.findViewById(R.id.buttoncancel);
            // Set OnClickListener for the "Pay" button
            buttonPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the "Pay" button click event here
                    // You can start the ReceiptPage activity or perform any other action
                    // For example, start the ReceiptPage activity:
                    Intent intent = new Intent(context, ReceiptPage.class);
                    context.startActivity(intent);
                }
            });

            // Set OnClickListener for the "Cancel" button
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Handle the "Cancel" button click event here
                    // You can perform any action you want

                    // Get the booking information
                    BookingInfo currentItem = bookingstatusList.get(getAdapterPosition());

                    // Remove the entry from the database
                    if (currentItem != null) {
                        String date = currentItem.getDate();
                        String noPlate = currentItem.getNoPlate();

                        // Build the key based on date and noPlate
                        String key = date + "_" + noPlate;

                        // Remove the entry from the database
                        databaseReference.child(key).removeValue();

                        // Notify the adapter about the removal
                        bookingstatusList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }
                }
            });


        }
    }


    private boolean isValetNone(BookingInfo bookingInfo) {
        return bookingInfo != null && "-".equals(bookingInfo.getValet());
    }
}
