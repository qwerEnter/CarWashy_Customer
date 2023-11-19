package com.example.carwashy.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carwashy.Model.Vehicle;
import com.example.carwashy.R;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {

    private List<Vehicle> vehicleList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Vehicle vehicle);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public VehicleAdapter(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);

        holder.textViewCarName.setText("Car : " + vehicle.getCarName());
        holder.textViewNoPlate.setText("NO Plate: " + vehicle.getNoPlate());
        holder.textViewModel.setText("Model: " + vehicle.getModel());
        holder.textViewVehicleType.setText("Type: " + vehicle.getVehicleType());

        // Load the image using Glide
        Glide.with(holder.itemView.getContext())
                .load(vehicle.getImageUri())  // Make sure to have getImageURL() in your Vehicle class
                .placeholder(R.drawable.imageicon)  // Placeholder image while loading
                .error(R.drawable.imageicon)  // Image to show if loading fails
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(vehicleList.get(adapterPosition));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCarName, textViewNoPlate, textViewModel, textViewVehicleType;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCarName = itemView.findViewById(R.id.textViewCarName);
            textViewNoPlate = itemView.findViewById(R.id.textViewNoPlate);
            textViewModel = itemView.findViewById(R.id.textViewModel);
            textViewVehicleType = itemView.findViewById(R.id.textViewVehicleType);
            imageView = itemView.findViewById(R.id.imagecar);  // Assuming you have an ImageView with the id imagecar in your item layout
        }
    }
}
