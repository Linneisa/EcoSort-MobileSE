package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
// BARIS UTAMA YANG WAJIB ADA AGAR CARDVIEW DIKENALI:
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // 1. Hubungkan Menu Grid Peta TPS
        CardView menuPeta = findViewById(R.id.menuPeta);
        menuPeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTps = new Intent(DashboardActivity.this, TpsActivity.class);
                startActivity(intentTps);
            }
        });

        // 2. Hubungkan Menu Grid Marketplace
        CardView menuMarketplace = findViewById(R.id.menuMarketplace);
        menuMarketplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMarket = new Intent(DashboardActivity.this, MarketplaceActivity.class);
                startActivity(intentMarket);
            }
        });

        // 3. Hubungkan Menu Grid Jadwal Pengambilan
        CardView menuJadwal = findViewById(R.id.menuJadwal);
        menuJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Munculkan toast pesan (opsional, biar keren)
               // Toast.makeText(DashboardActivity.this, "Membuka Jadwal Pengambilan...", Toast.LENGTH_SHORT).show();

                // 2. Tambahkan baris Intent ini untuk MEMAKSA PINDAH HALAMAN:
                Intent intentJadwal = new Intent(DashboardActivity.this, JadwalActivity.class);
                startActivity(intentJadwal);
            }
        });

        // 4. Hubungkan Menu Grid Reward
        CardView menuReward = findViewById(R.id.menuReward);
        menuReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Baris Toast lama sudah dihapus agar tidak memunculkan notifikasi di bawah layar

                // Tambahkan baris Intent ini untuk memaksa pindah halaman:
                Intent intentReward = new Intent(DashboardActivity.this, RewardActivity.class);
                startActivity(intentReward);
            }
        });
    }
}