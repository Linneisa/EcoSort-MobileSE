package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder> {

    private List<Jadwal> listJadwal;

    public JadwalAdapter(List<Jadwal> listJadwal) {
        this.listJadwal = listJadwal;
    }

    public void setData(List<Jadwal> data) {
        this.listJadwal = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_jadwal, parent, false);
        return new JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwal jadwal = listJadwal.get(position);
        holder.txtTanggal.setText(jadwal.getTanggal());
        holder.txtHari.setText(jadwal.getHari());
        holder.txtJenis.setText(jadwal.getJenisSampah());
        holder.txtJam.setText(jadwal.getJam());
        holder.txtStatus.setText(jadwal.getStatus());
    }

    @Override
    public int getItemCount() {
        return listJadwal.size();
    }

    public static class JadwalViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggal, txtHari, txtJenis, txtJam, txtStatus;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTanggal = itemView.findViewById(R.id.txtTanggalAngka);
            txtHari = itemView.findViewById(R.id.txtHariNama);
            txtJenis = itemView.findViewById(R.id.txtJenisSampah);
            txtJam = itemView.findViewById(R.id.txtJamPengambilan);
            txtStatus = itemView.findViewById(R.id.txtStatusJadwal);
        }
    }
}