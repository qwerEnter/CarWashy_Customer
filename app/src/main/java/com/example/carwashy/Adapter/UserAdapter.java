package com.example.carwashy.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carwashy.Model.Reward;
import com.example.carwashy.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.RewardViewHolder> {

    private List<Reward> rewardList;
    private int totalPoints;
    private TextView pointsTextView;
    private static final int SOME_THRESHOLD = 50;

    public UserAdapter(List<Reward> rewardList, TextView pointsTextView) {
        this.rewardList = rewardList;
        this.pointsTextView = pointsTextView;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_item, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = rewardList.get(position);
        holder.textRewardPoint.setText("Reward Point: " + reward.getRewardPoint());
        holder.textRewardDesc.setText("Reward Description: " + reward.getRewardDesc());

        // Check if totalPoints is equal to or more than reward.getRewardPoint()
        if (totalPoints >= reward.getRewardPoint()) {
            holder.buttonUnclaim.setVisibility(View.GONE);
            holder.buttonClaim.setVisibility(View.VISIBLE);
            holder.buttonClaim.setOnClickListener(v -> {
                // Implement your claim logic here
                claimReward(holder.itemView.getContext(), reward, pointsTextView, holder.buttonClaim);
            });
        } else {
            holder.buttonClaim.setVisibility(View.GONE);
            holder.buttonUnclaim.setVisibility(View.VISIBLE);
        }
    }

    private void claimReward(Context context, Reward reward, TextView pointsTextView, Button claimButton) {
        // Check if the user has already claimed a reward
        if (hasClaimedReward(context)) {
            // Display a toast indicating that the user has already claimed a reward
            Toast.makeText(context, "You have already claimed a reward", Toast.LENGTH_SHORT).show();
            return; // Do not proceed with claiming
        }

        // Save discount to a new SharedPreferences
        SharedPreferences claimSharedPreferences = context.getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
        SharedPreferences.Editor claimEditor = claimSharedPreferences.edit();

        // Retrieve the current total points
        int currentTotalPoints = retrieveTotalPointsFromSharedPreferences(context);

        // Subtract the reward points from the current total points
        int newTotalPoints = currentTotalPoints - reward.getRewardPoint();

        // Update the total points in the SharedPreferences and visibility of the claim button
        updateTotalPointsInSharedPreferences(context, newTotalPoints, pointsTextView, claimButton);

        // Save the claimed reward discount to the new SharedPreferences
        claimEditor.putFloat("claimed_discount", (float) reward.getDiscount());
        claimEditor.putBoolean("has_claimed_reward", true); // Set the flag to true
        claimEditor.apply();

        // Notify the user about the successful claim
        Toast.makeText(context, "Reward claimed successfully!", Toast.LENGTH_SHORT).show();
        // Refresh the RecyclerView to update the button visibility
        notifyDataSetChanged();
    }

    // Check if the user has already claimed a reward
    private boolean hasClaimedReward(Context context) {
        SharedPreferences claimSharedPreferences = context.getSharedPreferences("ClaimedRewards", Context.MODE_PRIVATE);
        return claimSharedPreferences.getBoolean("has_claimed_reward", false);
    }
    private int retrieveTotalPointsFromSharedPreferences(Context context) {
        // Retrieve total points from reward SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("ServicePageDataReward", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("totalPoints", 0);
    }
    private void updateTotalPointsInSharedPreferences(Context context, int newTotalPoints, TextView pointsTextView, Button claimButton) {
        // Update the total points in the original SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("ServicePageDataReward", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalPoints", newTotalPoints);
        editor.apply();

        // Update the total points displayed in the TextView
        pointsTextView.setText(String.valueOf(newTotalPoints));

        // Update the visibility of the claim button
        updateClaimButtonVisibility(newTotalPoints, claimButton);
    }


    private void updateClaimButtonVisibility(int newTotalPoints, Button claimButton) {
        // Assuming your logic for showing/hiding the claim button is based on the total points
        if (newTotalPoints >= SOME_THRESHOLD) {
            claimButton.setVisibility(View.VISIBLE);
        } else {
            claimButton.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return rewardList.size();
    }

    public static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView textRewardPoint;
        TextView textRewardDesc;
        Button buttonClaim,buttonUnclaim;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            textRewardPoint = itemView.findViewById(R.id.textRewardPoint);
            textRewardDesc = itemView.findViewById(R.id.textRewardDesc);
            buttonClaim = itemView.findViewById(R.id.buttonClaim);
            buttonUnclaim = itemView.findViewById(R.id.buttonUnclaim);
        }
    }
}
