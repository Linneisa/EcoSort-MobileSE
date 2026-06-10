package com.example.ecosort;

import android.graphics.Color;
import android.view.LayoutInflater;
import java.text.NumberFormat;
import java.util.Locale;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    public interface OnLihatKodeListener {
        void onLihatKode(Riwayat riwayat);
    }

    public interface OnBatalkanListener {
        void onBatalkan(Riwayat riwayat);
    }

    private final List<Riwayat> listRiwayat;
    private OnLihatKodeListener lihatKodeListener;
    private OnBatalkanListener  batalkanListener;

    public RiwayatAdapter(List<Riwayat> listRiwayat) {
        this.listRiwayat = listRiwayat;
    }

    public void setOnLihatKodeListener(OnLihatKodeListener listener) {
        this.lihatKodeListener = listener;
    }

    public void setOnBatalkanListener(OnBatalkanListener listener) {
        this.batalkanListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Riwayat riwayat = listRiwayat.get(position);

        holder.txtJudulRiwayat.setText(riwayat.getJudul());
        holder.txtTanggalRiwayat.setText(riwayat.getTanggal());

        if (riwayat.isRupiah()) {
            // Entri jual rongsok — tampilkan Rupiah langsung, bukan poin
            String poin = riwayat.getPoin();
            boolean selesai = poin != null && !poin.equals("Menunggu");
            if (selesai) {
                try {
                    long angka = Long.parseLong(poin);
                    String formatted = "Rp " + NumberFormat.getNumberInstance(
                            new Locale("in", "ID")).format(angka);
                    holder.txtPoinRiwayat.setText(formatted);
                } catch (NumberFormatException e) {
                    holder.txtPoinRiwayat.setText("Rp " + poin);
                }
                holder.txtPoinRiwayat.setTextColor(Color.parseColor("#10B981"));
            } else {
                holder.txtPoinRiwayat.setText("Menunggu");
                holder.txtPoinRiwayat.setTextColor(Color.parseColor("#94A3B8"));
            }
            holder.imgIkonRiwayat.setImageResource(android.R.drawable.ic_input_add);
        } else if (riwayat.getJenis().equalsIgnoreCase("Masuk")) {
            holder.txtPoinRiwayat.setText("+" + riwayat.getPoin() + " pts");
            holder.txtPoinRiwayat.setTextColor(Color.parseColor("#10B981"));
            holder.imgIkonRiwayat.setImageResource(android.R.drawable.ic_input_add);
        } else {
            holder.txtPoinRiwayat.setText("-" + riwayat.getPoin() + " pts");
            holder.txtPoinRiwayat.setTextColor(Color.parseColor("#F59E0B"));
            holder.imgIkonRiwayat.setImageResource(android.R.drawable.ic_menu_send);
        }

        // Tampilkan tombol hanya untuk transaksi sampah yang masih menunggu verifikasi
        boolean isPending = "menunggu".equals(riwayat.getStatus())
                && riwayat.getTransactionId() != null;

        holder.btnLihatKode.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.btnBatalkan.setVisibility(isPending ? View.VISIBLE : View.GONE);

        holder.btnLihatKode.setOnClickListener(
                isPending && lihatKodeListener != null
                        ? v -> lihatKodeListener.onLihatKode(riwayat)
                        : null);
        holder.btnBatalkan.setOnClickListener(
                isPending && batalkanListener != null
                        ? v -> batalkanListener.onBatalkan(riwayat)
                        : null);
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  txtJudulRiwayat, txtTanggalRiwayat, txtPoinRiwayat;
        ImageView imgIkonRiwayat;
        Button    btnLihatKode, btnBatalkan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtJudulRiwayat   = itemView.findViewById(R.id.txtJudulRiwayat);
            txtTanggalRiwayat = itemView.findViewById(R.id.txtTanggalRiwayat);
            txtPoinRiwayat    = itemView.findViewById(R.id.txtPoinRiwayat);
            imgIkonRiwayat    = itemView.findViewById(R.id.imgIkonRiwayat);
            btnLihatKode      = itemView.findViewById(R.id.btnLihatKode);
            btnBatalkan       = itemView.findViewById(R.id.btnBatalkan);
        }
    }
}
