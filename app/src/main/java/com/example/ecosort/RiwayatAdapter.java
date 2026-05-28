package com.example.ecosort;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<Riwayat> listRiwayat;

    public RiwayatAdapter(List<Riwayat> listRiwayat) {
        this.listRiwayat = listRiwayat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menyambungkan dengan desain XML yang sudah kamu buat tadi
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Riwayat riwayat = listRiwayat.get(position);

        holder.txtJudulRiwayat.setText(riwayat.getJudul());
        holder.txtTanggalRiwayat.setText(riwayat.getTanggal());

        // LOGIKA PEWARNAAN OTOMATIS
        if (riwayat.getJenis().equalsIgnoreCase("Masuk")) {
            holder.txtPoinRiwayat.setText("+" + riwayat.getPoin() + " pts");
            holder.txtPoinRiwayat.setTextColor(Color.parseColor("#10B981")); // Hijau
            holder.imgIkonRiwayat.setImageResource(android.R.drawable.ic_input_add); // Ikon Plus bawaan Android
        } else {
            holder.txtPoinRiwayat.setText("-" + riwayat.getPoin() + " pts");
            holder.txtPoinRiwayat.setTextColor(Color.parseColor("#F59E0B")); // Oranye
            holder.imgIkonRiwayat.setImageResource(android.R.drawable.ic_menu_send); // Ikon Panah Keluar
        }
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtJudulRiwayat, txtTanggalRiwayat, txtPoinRiwayat;
        ImageView imgIkonRiwayat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJudulRiwayat = itemView.findViewById(R.id.txtJudulRiwayat);
            txtTanggalRiwayat = itemView.findViewById(R.id.txtTanggalRiwayat);
            txtPoinRiwayat = itemView.findViewById(R.id.txtPoinRiwayat);
            imgIkonRiwayat = itemView.findViewById(R.id.imgIkonRiwayat);
        }
    }
}