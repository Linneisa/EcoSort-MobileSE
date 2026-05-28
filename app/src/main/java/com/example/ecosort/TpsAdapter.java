package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TpsAdapter extends RecyclerView.Adapter<TpsAdapter.ViewHolder> {

    private String[] listTps;
    private OnItemClickListener listener;

    // Interface untuk menangani klik kartu
    public interface OnItemClickListener {
        void onItemClick(String tpsText);
    }

    public TpsAdapter(String[] listTps, OnItemClickListener listener) {
        this.listTps = listTps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_layanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tpsName = listTps[position];
        holder.textTpsItem.setText(tpsName);

        // Pasang sensor klik pada satu kartu penuh
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(tpsName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTps.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTpsItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTpsItem = itemView.findViewById(R.id.textTpsItem);
        }
    }
}