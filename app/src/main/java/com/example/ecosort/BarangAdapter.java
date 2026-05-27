package com.example.ecosort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
        // Ini perintah untuk "meniup" (inflate) desain XML kotak kecilmu tadi
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_barang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Ini perintah untuk mengisi kotak dengan data asli (foto, nama, harga)
        Barang barang = listBarang.get(position);

        holder.imgBarang.setImageResource(barang.getFotoBarang());
        holder.txtNamaBarang.setText(barang.getNamaBarang());
        holder.txtHargaBarang.setText(barang.getHargaBarang());
    }

    @Override
    public int getItemCount() {
        // Menghitung ada berapa banyak barang yang mau ditampilkan
        return listBarang.size();
    }

    // Kelas khusus untuk mengenali elemen di dalam item_grid_barang.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView txtNamaBarang;
        TextView txtHargaBarang;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            txtNamaBarang = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaBarang = itemView.findViewById(R.id.txtHargaBarang);
        }
    }
}