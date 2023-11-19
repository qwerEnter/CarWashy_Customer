package com.example.carwashy.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.Premise;
import com.example.carwashy.Model.Vehicle;
import com.example.carwashy.R;

import java.util.List;

public class PremiseAdapter extends RecyclerView.Adapter<PremiseAdapter.ViewHolder> {

    private List<Premise> premiseList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Premise premise);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PremiseAdapter(List<Premise> premiseList) {
        this.premiseList = premiseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premise_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Premise premise = premiseList.get(position);
        holder.address.setText("Address : " + premise.getAddress());
        holder.state.setText("State: " + premise.getState());
        holder.imageView.setImageResource(R.drawable.icon);
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(premise);
            }
        });


    }

    @Override
    public int getItemCount() {
        return premiseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView address, state;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            state = itemView.findViewById(R.id.state);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
