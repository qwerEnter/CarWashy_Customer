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

import com.example.carwashy.Model.CarWashRecord;
import com.example.carwashy.R;
import com.example.carwashy.UI.RecordDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CarWashRecordAdapter extends RecyclerView.Adapter<CarWashRecordAdapter.ViewHolder> {

    private List<CarWashRecord> carwashrecordList;
    private Context context;

    public CarWashRecordAdapter(List<CarWashRecord> carwashrecordList, Context context) {
        this.carwashrecordList = carwashrecordList;
        this.context = context;
    }

    @NonNull
    @Override
    public CarWashRecordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carwashrecord_item, parent, false);
        return new ViewHolder(view, parent.getContext());

    }

    @Override
    public void onBindViewHolder(@NonNull CarWashRecordAdapter.ViewHolder holder, int position) {

        CarWashRecord status = carwashrecordList.get(position);

        holder.noplate.setText(status.getNoPlate());
        holder.status.setText(status.getStatus());
        holder.bookingdate.setText(status.getDate());
        holder.timestart.setText(status.getTimeStart());
        holder.timefinish.setText(status.getTimeFinish());


         if ("Paid".equals(status.getStatus())) {
            holder.buttonView.setVisibility(View.VISIBLE);
        }

        CarWashRecord currentItem = carwashrecordList.get(position);

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
        return carwashrecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noplate, status, bookingdate,timestart,timefinish;
        private Context context;
        CardView cardViewWaze,cardViewValet;
        Button buttonView;
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

            databaseReference = FirebaseDatabase.getInstance().getReference("CarWashRecord");

            buttonView = itemView.findViewById(R.id.buttonview);
            buttonView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, RecordDetails.class);
                    context.startActivity(intent);

            });

        }



    }

    private boolean isValetNone(CarWashRecord carwashrecord) {
        return carwashrecord != null && "-".equals(carwashrecord.getValet());
    }
}
