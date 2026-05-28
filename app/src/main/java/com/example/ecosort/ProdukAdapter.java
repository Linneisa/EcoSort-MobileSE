package com.example.ecosort;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent; // 👈 DISINI KITA TAMBAHKAN IMPORT INTENT NYA
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private Context context;
    private List<Produk> listProduk;

    public ProdukAdapter(Context context, List<Produk> listProduk) {
        this.context = context;
        this.listProduk = listProduk;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproduct, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = listProduk.get(position);

        holder.txtNama.setText(produk.getNama());
        holder.txtHarga.setText(produk.getHarga());
        holder.txtBerat.setText(produk.getBerat());
        holder.imgProduk.setImageResource(produk.getGambarResId());

        // Logika saat salah satu kartu barang diklik -> Buka Halaman Checkout Baru
// Logika klik -> Sekarang lari ke Halaman Input Berat Detail Barang
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailProdukActivity.class);

                // Mengirim data ke halaman detail
                intent.putExtra("PRODUK_NAMA", produk.getNama());
                intent.putExtra("PRODUK_HARGA", produk.getHarga());
                intent.putExtra("PRODUK_GAMBAR", produk.getGambarResId());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduk.size();
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHarga, txtBerat;
        ImageView imgProduk;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaProduk);
            txtHarga = itemView.findViewById(R.id.txtHargaProduk);
            txtBerat = itemView.findViewById(R.id.txtBeratProduk);
            imgProduk = itemView.findViewById(R.id.imgProduk);
        }
    }
}