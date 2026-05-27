package com.example.ecosort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Menambahkan library untuk memunculkan pop-up
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

        holder.imgBarang.setImageResource(barang.getFotoBarang());
        holder.txtNamaBarang.setText(barang.getNamaBarang());
        holder.txtHargaBarang.setText(barang.getHargaBarang());

        // LOGIKA SAAT TOMBOL "BELI LANGSUNG" DIKLIK
        holder.btnBeliLangsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Mengarahkan ke pembayaran " + barang.getNamaBarang(), Toast.LENGTH_SHORT).show();
            }
        });

        // LOGIKA SAAT TOMBOL "+ KERANJANG" DIKLIK
        holder.btnKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, barang.getNamaBarang() + " ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    // Kelas khusus untuk mengenali semua komponen di XML
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBarang;
        TextView txtNamaBarang;
        TextView txtHargaBarang;
        TextView btnBeliLangsung; // Tambahan untuk tombol Beli
        TextView btnKeranjang;    // Tambahan untuk tombol Keranjang

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBarang = itemView.findViewById(R.id.imgBarang);
            txtNamaBarang = itemView.findViewById(R.id.txtNamaBarang);
            txtHargaBarang = itemView.findViewById(R.id.txtHargaBarang);
            btnBeliLangsung = itemView.findViewById(R.id.btnBeliLangsung); // Sambungkan ID
            btnKeranjang = itemView.findViewById(R.id.btnKeranjang);       // Sambungkan ID
        }
    }
}