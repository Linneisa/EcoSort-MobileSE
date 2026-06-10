package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.JualRongsokModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RongsokanAdminAdapter
        extends RecyclerView.Adapter<RongsokanAdminAdapter.ViewHolder> {

    public interface OnKonfirmasiListener {
        void onKonfirmasi(JualRongsokModel item, int position, long hargaDeal);
    }

    private final List<JualRongsokModel> list;
    private final OnKonfirmasiListener   listener;

    public RongsokanAdminAdapter(List<JualRongsokModel> list, OnKonfirmasiListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    /** Hapus item setelah konfirmasi berhasil. */
    public void hapusItem(int position) {
        if (position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    /** Tandai tombol loading/selesai pada satu item tanpa refresh seluruh list. */
    public void setBtnLoading(ViewHolder holder, boolean isLoading) {
        holder.btnKonfirmasi.setEnabled(!isLoading);
        holder.btnKonfirmasi.setText(isLoading ? "Memproses..." : "Konfirmasi Selesai");
        holder.btnKonfirmasi.setAlpha(isLoading ? 0.6f : 1.0f);
        holder.etHargaDeal.setEnabled(!isLoading);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rongsok_admin, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JualRongsokModel item = list.get(position);

        // Short ID (8 char pertama UUID)
        String id = item.getId();
        String shortId = (id != null && id.length() >= 8)
                ? "#" + id.substring(0, 8).toUpperCase()
                : (id != null ? "#" + id : "#-");

        holder.tvNamaPenjual.setText(item.getNamaPenjual());
        holder.tvShortId.setText(shortId);
        holder.tvNamaBarang.setText(item.getNamaBarang());
        holder.tvLokasi.setText("📍 " + item.getLokasi());
        holder.tvTanggal.setText("Diajukan: " + formatTanggal(item.getCreatedAt()));

        // Reset state tombol setiap kali item di-bind (cegah state residual saat scroll)
        holder.btnKonfirmasi.setEnabled(true);
        holder.btnKonfirmasi.setText("Konfirmasi Selesai");
        holder.btnKonfirmasi.setAlpha(1.0f);
        holder.etHargaDeal.setEnabled(true);
        holder.etHargaDeal.setText("");

        holder.btnKonfirmasi.setOnClickListener(v -> {
            String input = holder.etHargaDeal.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(v.getContext(),
                        "Masukkan harga deal terlebih dahulu.", Toast.LENGTH_SHORT).show();
                return;
            }

            long hargaDeal;
            try {
                hargaDeal = Long.parseLong(input);
            } catch (NumberFormatException e) {
                Toast.makeText(v.getContext(),
                        "Format harga tidak valid.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Adapter posisi bisa berubah saat scroll; ambil adapterPosition saat klik
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;

            listener.onKonfirmasi(item, pos, hargaDeal);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static String formatTanggal(String iso) {
        if (iso == null || iso.isEmpty()) return "-";
        try {
            String s = iso.replaceAll("(\\.[0-9]+)?(\\+.*|Z).*$", "");
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(s);
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, HH:mm",
                    new Locale("in", "ID"));
            fmt.setTimeZone(TimeZone.getDefault());
            return fmt.format(date);
        } catch (Exception e) {
            return iso;
        }
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvNamaPenjual, tvShortId, tvNamaBarang, tvLokasi, tvTanggal;
        final EditText etHargaDeal;
        final Button   btnKonfirmasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaPenjual = itemView.findViewById(R.id.tvNamaPenjual);
            tvShortId     = itemView.findViewById(R.id.tvShortId);
            tvNamaBarang  = itemView.findViewById(R.id.tvNamaBarang);
            tvLokasi      = itemView.findViewById(R.id.tvLokasiRongsok);
            tvTanggal     = itemView.findViewById(R.id.tvTanggalRongsok);
            etHargaDeal   = itemView.findViewById(R.id.etHargaDeal);
            btnKonfirmasi = itemView.findViewById(R.id.btnKonfirmasiRongsok);
        }
    }
}
