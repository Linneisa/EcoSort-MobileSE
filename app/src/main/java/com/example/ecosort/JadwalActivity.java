package com.example.ecosort;

import android.os.Bundle;
import android.widget.ImageView; // 👇 Tambahan import ImageView
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class JadwalActivity extends AppCompatActivity {

    private RecyclerView rvJadwal;
    private ImageView btnBackJadwal; // 👇 Tambahan variabel untuk tombol back manual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);

        // 👇 PERBAIKAN: Hubungkan tombol back manual dari XML dan beri logika klik
        btnBackJadwal = findViewById(R.id.btnBackJadwal);
        btnBackJadwal.setOnClickListener(v -> {
            onBackPressed(); // Menutup halaman ini dan otomatis kembali ke Home
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
    }
}