package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // =======================================================
        // 1. KLIK KARTU POIN -> MENUJU RIWAYAT
        // =======================================================
        CardView kartuPoin = findViewById(R.id.kartuPoin);
        kartuPoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRiwayat = new Intent(DashboardActivity.this, RiwayatActivity.class);
                startActivity(intentRiwayat);
            }
        });

        // =======================================================
        // 2. KLIK MENU GRID (Fitur Utama)
        // =======================================================
        CardView menuPeta = findViewById(R.id.menuPeta);
        menuPeta.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, TpsActivity.class)));

        CardView menuMarketplace = findViewById(R.id.menuMarketplace);
        menuMarketplace.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, MarketplaceActivity.class)));

        CardView menuJadwal = findViewById(R.id.menuJadwal);
        menuJadwal.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, JadwalActivity.class)));

        CardView menuReward = findViewById(R.id.menuReward);
        menuReward.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, RewardActivity.class)));

        // =======================================================
        // 3. LOGIKA BOTTOM NAVIGATION
        // =======================================================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Atur agar ikon "Home" menyala saat berada di halaman ini
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true; // Sudah di halaman Home, tidak perlu pindah
                } else if (itemId == R.id.nav_riwayat) {
                    startActivity(new Intent(DashboardActivity.this, RiwayatActivity.class));
                    // Supaya animasinya mulus
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profil) {
                    Toast.makeText(DashboardActivity.this, "Halaman Profil segera hadir!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                return false;
            }
        });
    }
}