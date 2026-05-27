package com.example.ecosort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ProdukViewHolder> {

    private List<Produk> listProduk;

    public ProdukAdapter(List<Produk> listProduk) {
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
    }

    @Override
    public int getItemCount() {
        return listProduk.size();
    }

    public static class ProdukViewHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHarga, txtBerat;

        public ProdukViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNamaProduk);
            txtHarga = itemView.findViewById(R.id.txtHargaProduk);
            txtBerat = itemView.findViewById(R.id.txtBeratProduk);
        }
    }
}