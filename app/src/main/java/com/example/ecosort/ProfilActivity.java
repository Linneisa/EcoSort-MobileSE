package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        findViewById(R.id.btnBackProfil).setOnClickListener(v -> finish());
        findViewById(R.id.btnEditProfilHeader).setOnClickListener(v ->
                startActivity(new Intent(ProfilActivity.this, UbahProfilActivity.class)));
        findViewById(R.id.btnBantuan).setOnClickListener(v -> {
            startActivity(new Intent(ProfilActivity.this, CsActivity.class));
        });
        // Logika Tombol Keluar Akun
        androidx.cardview.widget.CardView btnLogout = findViewById(R.id.menuLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // 1. Bersihkan memori sesi (SharedPreferences)
                android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();

                // Menghapus hanya Role agar nama dan foto tetap ada (atau gunakan editor.clear() untuk menghapus semua)
                editor.remove("ROLE");
                editor.apply();

                Toast.makeText(ProfilActivity.this, "Berhasil Keluar Akun", Toast.LENGTH_SHORT).show();

                // 2. Lempar kembali ke LoginActivity
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);

                // 3. FLAG PENTING: Bersihkan riwayat halaman agar tidak bisa di-back
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Membuka memori (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView tvNama = findViewById(R.id.tvNamaProfil);
        TextView tvEmail = findViewById(R.id.tvEmailProfil);
        ImageView ivFoto = findViewById(R.id.ivFotoProfilHeader);
        View btnMenuAdmin = findViewById(R.id.btnMenuAdmin);

        // Ambil role pengguna saat ini
        String rolePengguna = prefs.getString("ROLE", "User");

        if (rolePengguna.equals("Admin")) {
            // ==========================================
            // 🛡️ TAMPILAN KHUSUS ADMIN
            // ==========================================
            tvNama.setText("Admin EcoSort");
            tvEmail.setText("admin@ecosort.com");

            // Ganti foto profil bawaan menjadi ikon gembok/admin
            if (ivFoto != null) {
                ivFoto.setImageResource(android.R.drawable.ic_secure);
            }

            // Munculkan tombol Scanner Admin
            if (btnMenuAdmin != null) {
                btnMenuAdmin.setVisibility(View.VISIBLE);
                btnMenuAdmin.setOnClickListener(v -> {
                    startActivity(new Intent(ProfilActivity.this, AdminScanActivity.class));
                });
            }

        } else {
            // ==========================================
            // 👤 TAMPILAN USER BIASA
            // ==========================================
            tvNama.setText(prefs.getString("nama", "Christoffer Bintang"));
            tvEmail.setText(prefs.getString("email", "bintang@mahasiswa.binus.ac.id"));

            // Load foto profil user jika ada
            String uriString = prefs.getString("foto_profil_path", null);
            if (uriString != null && ivFoto != null) {
                ivFoto.setImageURI(Uri.parse(uriString));
            } else if (ivFoto != null) {
                ivFoto.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // Sembunyikan tombol Scanner Admin
            if (btnMenuAdmin != null) {
                btnMenuAdmin.setVisibility(View.GONE);
            }
        }
    }
}