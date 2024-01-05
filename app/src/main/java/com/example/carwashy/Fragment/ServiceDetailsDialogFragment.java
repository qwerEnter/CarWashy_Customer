package com.example.carwashy.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Adapter.ServiceAdapter;
import com.example.carwashy.Model.Service;
import com.example.carwashy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceDetailsDialogFragment extends DialogFragment {

    private Service service;
    private ServiceAddedListener serviceAddedListener;
    private TextView totalCostTextView,servicetimeTextView, emptyView;
    private RecyclerView addedServicesRecyclerView;
    private ServiceAdapter addedServicesAdapter;

    // Interface to communicate the added service to the parent
    public interface ServiceAddedListener {
        void onServiceAdded(Service service);
    }

    // Constructor with the added ServiceAddedListener parameter
    public ServiceDetailsDialogFragment(Service service, ServiceAddedListener listener) {
        this.service = service;
        this.serviceAddedListener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_service_details, null);

        TextView nameTextView = view.findViewById(R.id.nameTextView);
        TextView costTextView = view.findViewById(R.id.costTextView);
        TextView timetakenTextView = view.findViewById(R.id.timetakenTextView);
        TextView pointTextView = view.findViewById(R.id.rewardpointTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        ImageView imageView = view.findViewById(R.id.imageView);
        Button addButton = view.findViewById(R.id.addServiceButton);

        totalCostTextView = requireActivity().findViewById(R.id.totalcost);
        emptyView = requireActivity().findViewById(R.id.emptyView);

        addedServicesRecyclerView = requireActivity().findViewById(R.id.rvcarset);
        addedServicesAdapter = new ServiceAdapter(new ArrayList<>(), requireActivity().getSupportFragmentManager());
        addedServicesRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        addedServicesRecyclerView.setAdapter(addedServicesAdapter);

        SharedPreferences preferencesA = requireContext().getSharedPreferences("ServicePageDataA", Context.MODE_PRIVATE);
        String vehicleType = preferencesA.getString("vehicleType", "");



        nameTextView.setText("SERVICE TYPE   : " + service.getName());

        if ("SMALL SEDAN/PROTON".equals(vehicleType)) {
            // Display cost2 and servicetime2
            costTextView.setText("SERVICE COST   : RM " + service.getCost2());
            timetakenTextView.setText("Estimation Service Time  : " + service.getServicetime2() + "0 hours/min");
        } else {
            // Display cost and servicetime
            costTextView.setText("SERVICE COST   : RM " + service.getCost());
            timetakenTextView.setText("Estimation Service Time  : " + service.getServicetime() + "0 hours/min");
        }

        descriptionTextView.setText("DESCRIPTION   : " + service.getDescription());
        pointTextView.setText("REWARD POINTS   : " + service.getPoints()+" RP");
        Picasso.get().load(service.getImageUri()).into(imageView);

        addButton.setOnClickListener(v -> {
            addServiceToSharedPreferences(service);
            saveDataToBookingPage(); // Call the new method to update data
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }

    private void addServiceToSharedPreferences(Service service) {
        // Service data SharedPreferences
        SharedPreferences serviceSharedPreferences = requireContext().getSharedPreferences("ServicePageDataC", Context.MODE_PRIVATE);
        SharedPreferences.Editor serviceEditor = serviceSharedPreferences.edit();

        Set<String> existingServices = serviceSharedPreferences.getStringSet("services", new HashSet<>());

        if (existingServices.add(service.getName())) {
            Toast.makeText(requireContext(), "Service added successfully", Toast.LENGTH_SHORT).show();
            serviceEditor.putStringSet("services", existingServices);

            // Fetch vehicleType from ServicePageDataA
            SharedPreferences preferencesA = requireContext().getSharedPreferences("ServicePageDataA", Context.MODE_PRIVATE);
            String vehicleType = preferencesA.getString("vehicleType", "");

            if ("SMALL SEDAN/PROTON".equals(vehicleType)) {
                // Store cost2 instead of cost
                serviceEditor.putString(service.getName(), String.valueOf(service.getCost2()));
            } else {
                // Store cost instead of cost2
                serviceEditor.putString(service.getName(), String.valueOf(service.getCost()));
            }

            // Apply changes to ServicePageDataC SharedPreferences
            serviceEditor.apply();

            emptyView.setVisibility(View.GONE);
            serviceAddedListener.onServiceAdded(service);

        } else {
            emptyView.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Service already added", Toast.LENGTH_SHORT).show();
        }
    }


    private void addServiceToSharedPreferences2(double points) {
        // Reward points SharedPreferences
        SharedPreferences rewardSharedPreferences = requireContext().getSharedPreferences("ServicePageDataReward", Context.MODE_PRIVATE);
        SharedPreferences.Editor rewardEditor = rewardSharedPreferences.edit();

        // Retrieve the current total points
        int currentTotalPoints = rewardSharedPreferences.getInt("totalPoints", 0);

        // Add the points of the newly added service
        int newTotalPoints = currentTotalPoints + (int) points;

        rewardEditor.putInt("totalPoints", newTotalPoints);

        // Apply changes to ServicePageDataReward SharedPreferences
        rewardEditor.apply();
    }

    private int calculateTotalPoints(SharedPreferences rewardSharedPreferences) {
        int totalPoints = 0;

        // Use the provided sharedPreferences for calculation
        for (String serviceName : rewardSharedPreferences.getStringSet("services", new HashSet<>())) {
            int servicePoints = getServicePoints(rewardSharedPreferences, serviceName);
            totalPoints += servicePoints;
        }
        return totalPoints;
    }

    private int getServicePoints(SharedPreferences sharedPreferences, String serviceName) {
        return sharedPreferences.getInt(serviceName + "_points", 0); // Change "_points" accordingly
    }




    private void updateAddedServicesList() {
        if (addedServicesAdapter != null && serviceAddedListener != null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ServicePageDataC", Context.MODE_PRIVATE);

            List<Service> addedServices = new ArrayList<>();
            for (String serviceName : sharedPreferences.getStringSet("services", new HashSet<>())) {
                String carsets = sharedPreferences.getString(serviceName, "");

                // Check if the string is not empty before parsing
                if (!carsets.isEmpty()) {
                    double parsedCarsets = Double.parseDouble(carsets);
                    Service service = new Service(serviceName, "Default Description", parsedCarsets, parsedCarsets, parsedCarsets, parsedCarsets, parsedCarsets, "Default Image Uri");
                    addedServices.add(service);
                } else {
                    // Handle empty string case, e.g., provide default values or skip adding the service
                    // For now, I'm skipping the service
                    continue;
                }
            }

            addedServicesAdapter.setServiceList(addedServices);
            addedServicesAdapter.notifyDataSetChanged();

            for (Service service : addedServices) {
                serviceAddedListener.onServiceAdded(service);
            }
        }
    }


    private void updateTotalCost() {
        double totalCost = 0;
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ServicePageDataC", Context.MODE_PRIVATE);
        for (String serviceName : sharedPreferences.getStringSet("services", new HashSet<>())) {
            String serviceCostString = sharedPreferences.getString(serviceName, "0");
            double serviceCost = 0.0; // Default value if the string is empty
            if (!serviceCostString.isEmpty()) {
                serviceCost = Double.parseDouble(serviceCostString);
            }
            totalCost += serviceCost;
        }

        totalCostTextView.setText("RM " + totalCost);
    }





    private void saveDataToBookingPage() {



        updateAddedServicesList();
        updateTotalCost();
        // Call addServiceToSharedPreferences2 with the appropriate points value
        addServiceToSharedPreferences2(service.getPoints());
    }
}
