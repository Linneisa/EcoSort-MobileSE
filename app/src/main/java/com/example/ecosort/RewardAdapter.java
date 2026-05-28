package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // 👇 Tambahan import ImageView
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private List<Reward> listReward;

    public RewardAdapter(List<Reward> listReward) {
        this.listReward = listReward;
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        Reward reward = listReward.get(position);
        holder.txtNama.setText(reward.getNama());
        holder.txtPoin.setText(reward.getPoin());
        holder.txtStok.setText(reward.getStok());

        // 👇 BARIS WAJIB: Biar gambar asli hadiahnya kepasang di ImageView kartu reward
        holder.imgReward.setImageResource(reward.getGambarResId());
    }

    @Override
    public int getItemCount() { return listReward.size(); }

    public static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtPoin, txtStok;
        ImageView imgReward; // 👇 Tambahan variabel ImageView

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaReward);
            txtPoin = itemView.findViewById(R.id.txtBiayaPoin);
            txtStok = itemView.findViewById(R.id.txtStokReward);

            // 👇 Hubungkan variabel dengan ID ImageView yang ada di item_reward.xml kamu
            imgReward = itemView.findViewById(R.id.imgReward);
        }
    }
}