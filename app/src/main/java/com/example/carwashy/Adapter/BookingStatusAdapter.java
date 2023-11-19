package com.example.carwashy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.BookingInfo;
import com.example.carwashy.R;

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

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            noplate = itemView.findViewById(R.id.noplate);
            status = itemView.findViewById(R.id.status);
            bookingdate = itemView.findViewById(R.id.bookingdate);
            timestart = itemView.findViewById(R.id.timestart);
            timefinish = itemView.findViewById(R.id.timefinish);
            cardViewWaze = itemView.findViewById(R.id.cardViewWaze);
            cardViewValet = itemView.findViewById(R.id.cardViewValet);
        }
    }


    private boolean isValetNone(BookingInfo bookingInfo) {
        return bookingInfo != null && "-".equals(bookingInfo.getValet());
    }
}
