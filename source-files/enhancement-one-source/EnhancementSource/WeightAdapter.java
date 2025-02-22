package com.example.project2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    private List<DatabaseHelper.WeightEntry> weightEntries;
    private final OnItemEditListener onItemEditListener;
    private final OnItemDeleteListener onItemDeleteListener;

    // Constructor
    public WeightAdapter(List<DatabaseHelper.WeightEntry> weightEntries,
                         OnItemEditListener onItemEditListener,
                         OnItemDeleteListener onItemDeleteListener) {
        this.weightEntries = weightEntries;
        this.onItemEditListener = onItemEditListener;
        this.onItemDeleteListener = onItemDeleteListener;
    }

    // Interfaces for edit and delete actions
    public interface OnItemEditListener {
        void onEdit(DatabaseHelper.WeightEntry entry);
    }

    public interface OnItemDeleteListener {
        void onDelete(DatabaseHelper.WeightEntry entry);
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight_entry, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        DatabaseHelper.WeightEntry entry = weightEntries.get(position);
        holder.dateTextView.setText(entry.getDate());
        holder.weightTextView.setText(String.format("%.1f lbs", entry.getWeight()));

        // Handle edit icon click
        holder.editIcon.setOnClickListener(v -> {
            if (onItemEditListener != null) {
                onItemEditListener.onEdit(entry);
            }
        });

        // Handle delete icon click
        holder.deleteIcon.setOnClickListener(v -> {
            if (onItemDeleteListener != null) {
                onItemDeleteListener.onDelete(entry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weightEntries.size();
    }

    public void updateData(List<DatabaseHelper.WeightEntry> newEntries) {
        weightEntries.clear();
        weightEntries.addAll(newEntries);
        notifyDataSetChanged();
    }

    static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, weightTextView;
        ImageView editIcon, deleteIcon;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.item_date);
            weightTextView = itemView.findViewById(R.id.item_weight);
            editIcon = itemView.findViewById(R.id.item_edit);
            deleteIcon = itemView.findViewById(R.id.item_delete);
        }
    }
}
