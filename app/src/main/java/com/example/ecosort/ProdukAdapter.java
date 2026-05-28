package com.example.ecosort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private Context context; // Tambahan Context untuk pop-up/pindah halaman
    private List<Produk> listProduk;

    // Tambahkan Context ke dalam konstruktor
    public ProdukAdapter(Context context, List<Produk> listProduk) {
        this.context = context;
        this.listProduk = listProduk;
    }

    @NonNull
    @Override
    public ProdukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan nama file XML aslimu: itemproduct
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproduct, parent, false);
        return new ProdukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukViewHolder holder, int position) {
        Produk produk = listProduk.get(position);

        holder.txtNama.setText(produk.getNama());
        holder.txtHarga.setText(produk.getHarga());
        holder.txtBerat.setText(produk.getBerat());

        // Logika saat salah satu kartu barang diklik
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Membuka detail " + produk.getNama(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listProduk.size();
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHarga, txtBerat;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            // ID komponen persis seperti desain aslimu
            txtNama = itemView.findViewById(R.id.txtNamaProduk);
            txtHarga = itemView.findViewById(R.id.txtHargaProduk);
            txtBerat = itemView.findViewById(R.id.txtBeratProduk);
        }
    }
}