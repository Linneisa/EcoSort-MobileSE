package com.example.ecosort;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView; // 👇 Tambahan import ImageView
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
    private ImageView btnBackHistory; // 👇 Tambahan variabel untuk tombol back manual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        // 👇 PERBAIKAN: Hubungkan tombol back manual dari XML dan beri logika klik
        btnBackHistory = findViewById(R.id.btnBackHistory);
        btnBackHistory.setOnClickListener(v -> {
            onBackPressed(); // Menutup halaman ini dan otomatis kembali ke halaman sebelumnya
        });

        // 1. Inisialisasi Komponen
        btnSemua = findViewById(R.id.btnFilterSemua);
        btnMasuk = findViewById(R.id.btnFilterMasuk);
        btnKeluar = findViewById(R.id.btnFilterKeluar);
        rvRiwayat = findViewById(R.id.rvRiwayat);

        // 2. Setup RecyclerView (Bentuk List Vertikal, bukan Grid)
        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        // 3. Masukkan Data Tiruan (Dummy Data)
        semuaRiwayatList = new ArrayList<>();
        semuaRiwayatList.add(new Riwayat("Setor Botol Plastik PET", "28 Mei 2026 • 14:30", "500", "Masuk"));
        semuaRiwayatList.add(new Riwayat("Tukar Saldo OVO Rp25.000", "25 Mei 2026 • 09:15", "2500", "Keluar"));
        semuaRiwayatList.add(new Riwayat("Setor Minyak Jelantah", "20 Mei 2026 • 16:45", "1200", "Masuk"));
        semuaRiwayatList.add(new Riwayat("Tukar Pulsa Telkomsel", "15 Mei 2026 • 11:20", "1000", "Keluar"));
        semuaRiwayatList.add(new Riwayat("Setor Kardus Bekas", "10 Mei 2026 • 10:00", "350", "Masuk"));

        // 4. Tampilkan semua data saat aplikasi pertama kali dibuka
        filterData("Semua");

        // 5. Logika Tombol Filter
        btnSemua.setOnClickListener(v -> {
            setKategoriAktif(btnSemua);
            filterData("Semua");
        });
        btnMasuk.setOnClickListener(v -> {
            setKategoriAktif(btnMasuk);
            filterData("Masuk");
        });
        btnKeluar.setOnClickListener(v -> {
            setKategoriAktif(btnKeluar);
            filterData("Keluar");
        });
    }

    // Method untuk Menyaring Data
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

    // Method untuk Merubah Visual Tombol Filter (Seperti di Marketplace)
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