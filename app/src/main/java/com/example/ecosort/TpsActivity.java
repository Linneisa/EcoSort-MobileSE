package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TpsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tps);

        // Menyembunyikan ActionBar bawaan
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 1. Inisialisasi RecyclerView & Atur Jadi Grid 2 Kolom
        RecyclerView rvTps = findViewById(R.id.rvTps);
        rvTps.setLayoutManager(new GridLayoutManager(this, 2));

        String[] dataTps = {
                "TPS Terpadu Babakan Siliwangi - Jl. Siliwangi",
                "TPS SPA Tegallega - Jl. Moch. Toha No.58",
                "TPS TAMANSARI (BONBIN) - Jl. Setapak",
                "TPS Pahlawan - Jl. Cikutra Barat"
        };

        TpsAdapter adapter = new TpsAdapter(dataTps, new TpsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String itemTerpilih) {
                String namaLokasi = itemTerpilih.split(" - ")[0];

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(namaLokasi + " Bandung"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(TpsActivity.this, "Aplikasi Google Maps tidak ditemukan!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 3. Hubungkan Adapter ke Layar
        rvTps.setAdapter(adapter);

        // Hubungkan ImageView XML ke Java dan beri fungsi klik back
        ImageView btnBackTps = findViewById(R.id.btnBackTps);
        btnBackTps.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // =======================================================
        // TAMBAHAN: Logika Klik Foto Profil ke ProfilActivity
        // =======================================================
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v -> {
                Intent intent = new Intent(TpsActivity.this, ProfilActivity.class);
                startActivity(intent);
            });
        }

        // =======================================================
        // TAMBAHAN DINAMIS: Ambil Foto Profil dari SharedPreferences
        // =======================================================
        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String fotoPath = sharedPref.getString("foto_profil_path", null);

            if (fotoPath != null) {
                // Jika user sudah ganti foto, pasang fotonya
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                // Jika belum, pakai icon default sistem (atau ganti ke avatar lokal jika ada)
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }
}