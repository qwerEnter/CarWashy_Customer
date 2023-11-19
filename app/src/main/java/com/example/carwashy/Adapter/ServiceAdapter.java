package com.example.carwashy.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.Service;
import com.example.carwashy.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> serviceList;
    private CarsetAdapter.OnItemClickListener onItemClickListener;
    private FragmentManager fragmentManager;

    public ServiceAdapter(List<Service> serviceList, FragmentManager fragmentManager) {
        this.serviceList = (serviceList != null) ? serviceList : new ArrayList<>();
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceNameTextView.setText(service.getName());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = (serviceList != null) ? serviceList : new ArrayList<>();
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceName);
        }
    }

    public List<Service> getServiceList() {
        return serviceList;
    }
}

