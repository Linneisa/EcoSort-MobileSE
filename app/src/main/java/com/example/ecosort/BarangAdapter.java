package com.example.ecosort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    private Context context;
    private List<Barang> listBarang;

    // Konstruktor untuk menerima data dari MarketplaceActivity
    public BarangAdapter(Context context, List<Barang> listBarang) {
        this.context = context;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_barang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = listBarang.get(position);

        // Set data dasar barang
        holder.imgBarang.setImageResource(barang.getFotoBarang());
        holder.txtNamaBarang.setText(barang.getNamaBarang());
        holder.txtHargaBarang.setText(barang.getHargaBarang());

        // KUNCI PERBAIKAN 1: Set data untuk komponen baru (Satuan & Lokasi)
        // Catatan: Pastikan di kelas 'Barang.java' kamu sudah punya method getSatuanBarang() dan getLokasiBarang().
        // Jika belum ada di model Barang, kamu bisa ganti teks sementara atau mengosongkannya dulu.
        holder.txtSatuanBarang.setText("/kg");
        holder.txtLokasiBarang.setText("Pengepul · Bandung");

        // KUNCI PERBAIKAN 2: Logika klik dialihkan ke SATU KARTU PENUH (itemView)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop-up indikator premium saat kartu barang diklik
                Toast.makeText(context, "Membuka detail untuk " + barang.getNamaBarang(), Toast.LENGTH_SHORT).show();

                // Di sini nanti tempat kamu menaruh Intent untuk pindah ke halaman DetailBarangActivity
                /*
                Intent intent = new Intent(context, DetailBarangActivity.class);
                intent.putExtra("nama_barang", barang.getNamaBarang());
                context.startActivity(intent);
                */
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    // Kelas khusus untuk mengenali semua komponen di XML yang baru
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView txtNamaBarang, txtHargaBarang, txtSatuanBarang, txtLokasiBarang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Hubungkan komponen sesuai dengan ID baru di item_grid_barang.xml
            imgBarang = itemView.findViewById(R.id.imgBarang);
            txtNamaBarang = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaBarang = itemView.findViewById(R.id.txtHargaBarang);
            txtSatuanBarang = itemView.findViewById(R.id.txtSatuanBarang);
            txtLokasiBarang = itemView.findViewById(R.id.txtLokasiBarang);
        }
    }
}