package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView tvNama = findViewById(R.id.tvNamaProfil);
        TextView tvEmail = findViewById(R.id.tvEmailProfil);
        ImageView ivFoto = findViewById(R.id.ivFotoProfilHeader);

        tvNama.setText(prefs.getString("nama", "Bintang"));
        tvEmail.setText(prefs.getString("email", "bintang@mahasiswa.binus.ac.id"));

        // KUNCI HARUS SAMA: foto_profil_path
        String uriString = prefs.getString("foto_profil_path", null);
        if (uriString != null && ivFoto != null) {
            ivFoto.setImageURI(Uri.parse(uriString));
        } else if (ivFoto != null) {
            ivFoto.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }
}