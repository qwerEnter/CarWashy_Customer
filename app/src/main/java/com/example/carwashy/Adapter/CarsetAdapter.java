package com.example.carwashy.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.Service;
import com.example.carwashy.R;
import com.example.carwashy.Fragment.ServiceDetailsDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarsetAdapter extends RecyclerView.Adapter<CarsetAdapter.ViewHolder> implements ServiceDetailsDialogFragment.ServiceAddedListener {

    private List<Service> serviceList;

    private OnItemClickListener onItemClickListener;
    private FragmentManager fragmentManager;

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public interface OnItemClickListener {
        void onItemClick(Service service);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public CarsetAdapter(List<Service> serviceList, FragmentManager fragmentManager) {
        this.serviceList = serviceList;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carset_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = serviceList.get(position);

        // Set car set naame
        holder.carsetName.setText(service.getName());

        // Load image using Picasso (or any other image loading library)
        Picasso.get().load(service.getImageUri()).into(holder.imageCarSet);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            showDetailsDialog(service);
        });
    }
    private void showDetailsDialog(Service service) {
        ServiceDetailsDialogFragment dialogFragment = new ServiceDetailsDialogFragment(service, this);
        dialogFragment.show(fragmentManager, "ServiceDetailsDialogFragment");
    }
    @Override
    public int getItemCount() {
        return serviceList.size();
    }
    @Override
    public void onServiceAdded(Service service) {
        // Handle service added event here if needed
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(service);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView carsetName;
        ImageView imageCarSet;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carsetName = itemView.findViewById(R.id.carsetname);
            imageCarSet = itemView.findViewById(R.id.imagecarset);
        }
    }
    public List<Service> getServiceList() {
        return serviceList;
    }

}
