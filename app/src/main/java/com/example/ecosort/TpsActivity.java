package com.example.ecosort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity; // Import EdgeToEdge dihapus karena tidak dipakai lagi
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TpsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BARIS EdgeToEdge.enable(this); DI SINI SUDAH DIHAPUS BIAR TIDAK FULLSCREEN NABRAK ATAS

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
    }
}