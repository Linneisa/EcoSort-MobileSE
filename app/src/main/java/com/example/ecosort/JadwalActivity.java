package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.JadwalModel;
import com.example.ecosort.network.SupabaseClient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JadwalActivity extends AppCompatActivity {

    private RecyclerView rvJadwal;
    private ImageView btnBackJadwal;
    private JadwalAdapter jadwalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        // 👇 PERBAIKAN: Menggunakan OnBackPressedDispatcher terbaru agar seirama dengan halaman lain
        btnBackJadwal = findViewById(R.id.btnBackJadwal);
        btnBackJadwal.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Setup RecyclerView Vertikal biasa (LinearLayoutManager)
        rvJadwal = findViewById(R.id.rvJadwal);
        rvJadwal.setLayoutManager(new LinearLayoutManager(this));
        rvJadwal.setHasFixedSize(true);

        // Inisialisasi adapter kosong, data akan diisi dari Supabase
        jadwalAdapter = new JadwalAdapter(new ArrayList<>());
        rvJadwal.setAdapter(jadwalAdapter);

        // Ambil data jadwal dari Supabase
        fetchJadwalDariSupabase();

        // =======================================================
        // TAMBAHAN: Logika Klik Foto Profil ke ProfilActivity
        // =======================================================
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v -> {
                Intent intent = new Intent(JadwalActivity.this, ProfilActivity.class);
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
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    // Ambil data jadwal dari tabel "jadwal" di Supabase
    private void fetchJadwalDariSupabase() {
        Call<List<JadwalModel>> call = SupabaseClient.getApiService()
                .getJadwal("*", "tanggal.asc");

        call.enqueue(new Callback<List<JadwalModel>>() {
            @Override
            public void onResponse(Call<List<JadwalModel>> call, Response<List<JadwalModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Jadwal> listJadwal = new ArrayList<>();
                    for (JadwalModel item : response.body()) {
                        listJadwal.add(new Jadwal(
                                formatHariAngka(item.getTanggal()),
                                formatBulanTahun(item.getTanggal()),
                                item.getJenisSampah(),
                                item.getJam(),
                                item.getStatus()
                        ));
                    }
                    jadwalAdapter.setData(listJadwal);
                } else {
                    // Tabel masih kosong di Supabase, tampilkan data contoh
                    loadDataDummy();
                }
            }

            @Override
            public void onFailure(Call<List<JadwalModel>> call, Throwable t) {
                Toast.makeText(JadwalActivity.this,
                        "Gagal terhubung ke server, menampilkan data contoh",
                        Toast.LENGTH_SHORT).show();
                loadDataDummy();
            }
        });
    }

    // "2026-06-09" → "09"  (untuk txtTanggalAngka)
    private String formatHariAngka(String rawDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(rawDate);
            return new SimpleDateFormat("dd", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return rawDate;
        }
    }

    // "2026-06-09" → "Jun 2026"  (untuk txtHariNama)
    private String formatBulanTahun(String rawDate) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(rawDate);
            return new SimpleDateFormat("MMM yyyy", new Locale("in", "ID")).format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    // Fallback: data statis jika Supabase belum berisi data atau tidak ada koneksi
    private void loadDataDummy() {
        List<Jadwal> listData = new ArrayList<>();
        listData.add(new Jadwal("28", "Mei 2026", "Sampah Organik (Sisa Makanan)",       "09:00 - 11:00 WIB", "Akan Datang"));
        listData.add(new Jadwal("30", "Mei 2026", "Sampah Anorganik (Plastik & Kertas)", "13:00 - 15:00 WIB", "Akan Datang"));
        listData.add(new Jadwal("02", "Jun 2026", "Sampah B3 & Elektronik",              "10:00 - 12:00 WIB", "Akan Datang"));
        jadwalAdapter.setData(listData);
    }
}