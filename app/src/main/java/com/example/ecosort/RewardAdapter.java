package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.RewardModel;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    public interface OnTukarClickListener {
        void onTukar(RewardModel reward, int position);
    }

    private List<RewardModel> listReward = new ArrayList<>();
    private final OnTukarClickListener listener;

    public RewardAdapter(OnTukarClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<RewardModel> data) {
        this.listReward = data;
        notifyDataSetChanged();
    }

    public void updateStokAtPosition(int position, int newStok) {
        if (position >= 0 && position < listReward.size()) {
            listReward.get(position).setStok(newStok);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        RewardModel reward = listReward.get(position);

        holder.txtNama.setText(reward.getNama() != null ? reward.getNama() : "-");
        holder.txtPoin.setText(formatPoin(reward.getHargaPoin()));
        holder.txtStok.setText("Stok: " + reward.getStok());
        holder.imgReward.setImageResource(resolveDrawable(reward.getKategori(), reward.getNama()));

        boolean habis = reward.getStok() <= 0;
        holder.btnTukar.setEnabled(!habis);
        holder.btnTukar.setAlpha(habis ? 0.4f : 1.0f);
        holder.btnTukar.setText(habis ? "Habis" : "Tukar");

        holder.btnTukar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID && listener != null) {
                listener.onTukar(listReward.get(pos), pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listReward.size();
    }

    private String formatPoin(int poin) {
        if (poin >= 1000) {
            return String.format("%,d pts", poin).replace(',', '.');
        }
        return poin + " pts";
    }

    private int resolveDrawable(String kategori, String nama) {
        if (kategori == null) return R.drawable.img_voucher;
        switch (kategori.toLowerCase().trim()) {
            case "ewallet":
            case "e-wallet":
            case "saldo":
                return R.drawable.img_wallet;
            case "voucher":
                return R.drawable.img_voucher;
            case "pulsa":
                return R.drawable.img_pulsa;
            case "listrik":
            case "token":
                return R.drawable.img_token;
            case "barang": {
                String namaLower = (nama != null) ? nama.toLowerCase() : "";
                if (namaLower.contains("tumbler"))    return R.drawable.img_tumbler;
                if (namaLower.contains("alat makan")) return R.drawable.img_alatmakan;
                return R.drawable.img_voucher;
            }
            case "tumbler":
            case "merchandise":
                return R.drawable.img_tumbler;
            case "alat_makan":
            case "peralatan":
                return R.drawable.img_alatmakan;
            default:
                return R.drawable.img_voucher;
        }
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView      txtNama, txtPoin, txtStok;
        ImageView     imgReward;
        MaterialButton btnTukar;

        RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama   = itemView.findViewById(R.id.txtNamaReward);
            txtPoin   = itemView.findViewById(R.id.txtBiayaPoin);
            txtStok   = itemView.findViewById(R.id.txtStokReward);
            imgReward = itemView.findViewById(R.id.imgReward);
            btnTukar  = itemView.findViewById(R.id.btnTukar);
        }
    }
}
