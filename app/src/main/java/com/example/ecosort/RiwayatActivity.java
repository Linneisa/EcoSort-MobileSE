package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RiwayatActivity extends AppCompatActivity {

    private TextView btnSemua, btnMasuk, btnKeluar;
    private RecyclerView rvRiwayat;
    private List<Riwayat> semuaRiwayatList;
    private ImageView btnBackHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        // 👇 PERBAIKAN: Menggunakan OnBackPressedDispatcher terbaru
        btnBackHistory = findViewById(R.id.btnBackHistory);
        btnBackHistory.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // 1. Inisialisasi Komponen
        btnSemua = findViewById(R.id.btnFilterSemua);
        btnMasuk = findViewById(R.id.btnFilterMasuk);
        btnKeluar = findViewById(R.id.btnFilterKeluar);
        rvRiwayat = findViewById(R.id.rvRiwayat);

        // 2. Setup RecyclerView
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        // 3. Masukkan Data Tiruan
        semuaRiwayatList = new ArrayList<>();
        semuaRiwayatList.add(new Riwayat("Setor Botol Plastik PET", "28 Mei 2026 • 14:30", "500", "Masuk"));
        semuaRiwayatList.add(new Riwayat("Tukar Saldo OVO Rp10.000", "25 Mei 2026 • 09:15", "5000", "Keluar"));
        semuaRiwayatList.add(new Riwayat("Setor Minyak Jelantah", "20 Mei 2026 • 16:45", "1200", "Masuk"));
        semuaRiwayatList.add(new Riwayat("Tukar Pulsa Telkomsel", "15 Mei 2026 • 11:20", "9500", "Keluar"));
        semuaRiwayatList.add(new Riwayat("Setor Kardus Bekas", "10 Mei 2026 • 10:00", "350", "Masuk"));

        filterData("Semua");

        // 5. Logika Tombol Filter
        btnSemua.setOnClickListener(v -> { setKategoriAktif(btnSemua); filterData("Semua"); });
        btnMasuk.setOnClickListener(v -> { setKategoriAktif(btnMasuk); filterData("Masuk"); });
        btnKeluar.setOnClickListener(v -> { setKategoriAktif(btnKeluar); filterData("Keluar"); });

        // =======================================================
        // TAMBAHAN: Logika Klik Foto Profil
        // =======================================================
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v -> {
                Intent intent = new Intent(RiwayatActivity.this, ProfilActivity.class);
                startActivity(intent);
            });
        }

        // =======================================================
        // TAMBAHAN DINAMIS: Ambil Foto Profil
        // =======================================================
        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String fotoPath = sharedPref.getString("foto_profil_path", null);

            if (fotoPath != null) {
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    private void filterData(String kategori) {
        List<Riwayat> filteredList = new ArrayList<>();
        if (kategori.equals("Semua")) {
            filteredList.addAll(semuaRiwayatList);
        } else {
            for (Riwayat r : semuaRiwayatList) {
                if (r.getJenis().equalsIgnoreCase(kategori)) {
                    filteredList.add(r);
                }
            }
        }
        RiwayatAdapter adapter = new RiwayatAdapter(filteredList);
        rvRiwayat.setAdapter(adapter);
    }

    private void setKategoriAktif(TextView tombolTerpilih) {
        TextView[] daftarTombol = {btnSemua, btnMasuk, btnKeluar};
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