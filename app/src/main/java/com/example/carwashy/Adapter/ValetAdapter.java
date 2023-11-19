package com.example.carwashy.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carwashy.Model.Premise;
import com.example.carwashy.Model.Valet;
import com.example.carwashy.R;

import java.util.List;

public class ValetAdapter extends RecyclerView.Adapter<ValetAdapter.ViewHolder> {

    private List<Valet> valetList;
    private ValetAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Valet valet);
    }

    public void setOnItemClickListener(ValetAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ValetAdapter(List<Valet> valetList) {
        this.valetList = valetList;
    }

    @NonNull
    @Override
    public ValetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.valet_item, parent, false);
        return new ValetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Valet valet = valetList.get(position);
        holder.location.setText("Location : " + valet.getLocation());
        holder.name.setText("Name : " + valet.getName());
        holder.phonenumber.setText("Phone Number: " + valet.getPhonenumber());

        // Load the image using Glide
        Glide.with(holder.itemView.getContext())
                .load(valet.getImageUri())  // Make sure to have getImageURL() in your Vehicle class
                .placeholder(R.drawable.imageicon)  // Placeholder image while loading
                .error(R.drawable.imageicon)  // Image to show if loading fails
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(valet);
            }
        });
    }


    @Override
    public int getItemCount() {
        return valetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView location, name,phonenumber ;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.location);
            name = itemView.findViewById(R.id.name);
            phonenumber = itemView.findViewById(R.id.phonenumber);
            imageView = itemView.findViewById(R.id.imagevalet);
        }

    }
}