package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marketplace);

        // Mengatur jarak layar (Bawaan Android)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ================= 1. KODE GRID MARKETPLACE (KOTAK-KOTAK) =================
        RecyclerView rvMarketplace = findViewById(R.id.rvMarketplace);
        rvMarketplace.setLayoutManager(new GridLayoutManager(this, 2));

        List<Barang> daftarBarang = new ArrayList<>();

        // Memasukkan data barang (Nanti ganti tulisan ic_launcher_background dengan foto aslimu)
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Botol Plastik (PET)", "Rp 3.000 / Kg"));
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Kardus Bekas", "Rp 1.500 / Kg"));
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Kertas HVS & Buku", "Rp 2.000 / Kg"));
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Minyak Jelantah", "Rp 5.000 / Liter"));
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Kaleng Alumunium", "Rp 9.000 / Kg"));
        daftarBarang.add(new Barang(R.drawable.ic_launcher_background, "Botol Kaca", "Rp 1.000 / Kg"));

        BarangAdapter adapterMarket = new BarangAdapter(this, daftarBarang);
        rvMarketplace.setAdapter(adapterMarket);


        // ================= 2. KODE FUNGSI TOMBOL & NAVIGASI BAWAH =================
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navMap = findViewById(R.id.navMap);
        ImageView navMarket = findViewById(R.id.navMarket);
        ImageView navProfile = findViewById(R.id.navProfile);
        CardView cardSearch = findViewById(R.id.cardSearch);

        // Fungsi Tombol Pencarian
        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarketplaceActivity.this, "Fitur pencarian sedang dikembangkan...", Toast.LENGTH_SHORT).show();
            }
        });

        // Fungsi Tombol Home (Kembali)
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Menutup halaman dan kembali ke Dashboard
            }
        });

        // Fungsi Tombol Map (Pindah ke TpsActivity)
        navMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarketplaceActivity.this, TpsActivity.class);
                startActivity(intent);
            }
        });

        // Fungsi Tombol Profile
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarketplaceActivity.this, "Membuka halaman profil...", Toast.LENGTH_SHORT).show();
            }
        });

        // Fungsi Tombol Market (Memberitahu posisi saat ini)
        navMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarketplaceActivity.this, "Kamu sudah berada di Marketplace", Toast.LENGTH_SHORT).show();
            }
        });
    }
}