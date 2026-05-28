package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Tombol Back
        findViewById(R.id.btnBackProfil).setOnClickListener(v -> finish());

        // Tombol Edit Profil
        findViewById(R.id.btnEditProfilHeader).setOnClickListener(v ->
                startActivity(new Intent(ProfilActivity.this, UbahProfilActivity.class)));

        // Tombol Bantuan & Laporan (Menghubungkan ke WhatsApp CS)
// Ubah bagian setOnClickListener untuk btnBantuan menjadi seperti ini:
        findViewById(R.id.btnBantuan).setOnClickListener(v -> {
            // Ganti 'CsActivity.class' dengan nama file Activity yang menampilkan halaman Customer Service kamu
            Intent intent = new Intent(ProfilActivity.this, CsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mengambil data profil yang tersimpan
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView tvNama = findViewById(R.id.tvNamaProfil);
        TextView tvEmail = findViewById(R.id.tvEmailProfil);
        ImageView ivFoto = findViewById(R.id.ivFotoProfilHeader);

        // Menampilkan data ke UI
        tvNama.setText(prefs.getString("nama", "Bintang"));
        tvEmail.setText(prefs.getString("email", "bintang@mahasiswa.binus.ac.id"));

        String uriString = prefs.getString("foto_uri", null);
        if (uriString != null && ivFoto != null) {
            ivFoto.setImageURI(Uri.parse(uriString));
        }
    }
}