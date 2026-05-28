package com.example.ecosort;

import android.content.Context;
import android.content.Intent; // 👇 Tambahan import Intent untuk pindah halaman
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class JadwalActivity extends AppCompatActivity {

    private RecyclerView rvJadwal;
    private ImageView btnBackJadwal;

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

        // Data dummy jadwal pengambilan sampah (Tepat 3 Jadwal)
        List<Jadwal> listData = new ArrayList<>();
        listData.add(new Jadwal("28", "MEI", "Sampah Organik (Sisa Makanan)", "09:00 - 11:00 WIB", "Akan Datang"));
        listData.add(new Jadwal("30", "MEI", "Sampah Anorganik (Plastik & Kertas)", "13:00 - 15:00 WIB", "Akan Datang"));
        listData.add(new Jadwal("02", "JUN", "Sampah B3 & Elektronik", "10:00 - 12:00 WIB", "Akan Datang"));

        // Pasang ke adapter
        JadwalAdapter adapter = new JadwalAdapter(listData);
        rvJadwal.setAdapter(adapter);

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
}