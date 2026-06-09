package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.LokasiTpsModel;
import java.util.List;

public class TpsAdapter extends RecyclerView.Adapter<TpsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LokasiTpsModel tps);
    }

    private final List<LokasiTpsModel> listTps;
    private final OnItemClickListener listener;

    public TpsAdapter(List<LokasiTpsModel> listTps, OnItemClickListener listener) {
        this.listTps = listTps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_layanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LokasiTpsModel tps = listTps.get(position);
        holder.textTpsItem.setText(tps.getNama());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(tps);
        });
    }

    @Override
    public int getItemCount() {
        return listTps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTpsItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTpsItem = itemView.findViewById(R.id.textTpsItem);
        }
    }
}
