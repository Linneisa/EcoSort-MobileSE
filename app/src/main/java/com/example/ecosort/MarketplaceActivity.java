package com.example.ecosort;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private TextView btnSemua, btnOrganik, btnPlastik, btnLogam;
    private RecyclerView rvMarketplace;
    private List<Produk> semuaProdukList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        // Inisialisasi Filter (Komponen Navigasi Sudah Dihapus)
        btnSemua = findViewById(R.id.btnFilterSemua);
        btnOrganik = findViewById(R.id.btnFilterOrganik);
        btnPlastik = findViewById(R.id.btnFilterPlastik);
        btnLogam = findViewById(R.id.btnFilterLogam);

        // Setup RecyclerView Grid 2 Kolom
        rvMarketplace = findViewById(R.id.rvMarketplace);
        rvMarketplace.setLayoutManager(new GridLayoutManager(this, 2));

        // Isi Data Barang Tiruan
        semuaProdukList = new ArrayList<>();
        semuaProdukList.add(new Produk("Botol Plastik PET", "500 pts", "/1 kg", "Plastik"));
        semuaProdukList.add(new Produk("Kardus Bekas Tebal", "350 pts", "/1 kg", "Organik"));
        semuaProdukList.add(new Produk("Kaleng Soda Aluminium", "800 pts", "/1 kg", "Logam"));
        semuaProdukList.add(new Produk("Minyak Jelantah Jernih", "1.200 pts", "/1 ltr", "Organik"));
        semuaProdukList.add(new Produk("Gelas Plastik Air Mineral", "400 pts", "/1 kg", "Plastik"));
        semuaProdukList.add(new Produk("Besi Siku Bekas", "1.500 pts", "/1 kg", "Logam"));

        // Tampilkan barang awal
        tampilkanDataKeGrid(semuaProdukList);

        // Filter klik
        btnSemua.setOnClickListener(v -> {
            setKategoriAktif(btnSemua);
            tampilkanDataKeGrid(semuaProdukList);
        });
        btnOrganik.setOnClickListener(v -> {
            setKategoriAktif(btnOrganik);
            filterData("Organik");
        });
        btnPlastik.setOnClickListener(v -> {
            setKategoriAktif(btnPlastik);
            filterData("Plastik");
        });
        btnLogam.setOnClickListener(v -> {
            setKategoriAktif(btnLogam);
            filterData("Logam");
        });
    }

    private void filterData(String kategori) {
        List<Produk> filteredList = new ArrayList<>();
        for (Produk p : semuaProdukList) {
            if (p.getKategori().equalsIgnoreCase(kategori)) {
                filteredList.add(p);
            }
        }
        tampilkanDataKeGrid(filteredList);
    }

    private void tampilkanDataKeGrid(List<Produk> list) {
        ProdukAdapter adapter = new ProdukAdapter(list);
        rvMarketplace.setAdapter(adapter);
    }

    private void setKategoriAktif(TextView tombolTerpilih) {
        TextView[] daftarTombol = {btnSemua, btnOrganik, btnPlastik, btnLogam};
        for (TextView btn : daftarTombol) {
            btn.setBackgroundResource(R.drawable.bg_tab_inactive);
            btn.setTextColor(Color.parseColor("#475569"));
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        tombolTerpilih.setBackgroundResource(R.drawable.bg_tab_active);
        tombolTerpilih.setTextColor(Color.parseColor("#050F0B"));
        tombolTerpilih.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}