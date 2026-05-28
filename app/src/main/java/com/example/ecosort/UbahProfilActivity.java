package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UbahProfilActivity extends AppCompatActivity {
    private ImageView ivFotoProfil;
    private Uri imageUri;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivFotoProfil.setImageURI(uri);
                    // Ambil izin akses agar URI tetap valid setelah restart
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);

        ivFotoProfil = findViewById(R.id.ivFotoProfil);
        findViewById(R.id.cardEditFoto).setOnClickListener(v -> pickImage.launch("image/*"));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        EditText etNama = findViewById(R.id.etNama);
        EditText etNomorHP = findViewById(R.id.etNomorHP);
        EditText etEmail = findViewById(R.id.etEmail);

        // Load data lama
        etNama.setText(prefs.getString("nama", ""));
        etNomorHP.setText(prefs.getString("hp", ""));
        etEmail.setText(prefs.getString("email", ""));

        // Load foto dengan kunci yang seragam: "foto_profil_path"
        String savedUri = prefs.getString("foto_profil_path", null);
        if (savedUri != null) {
            ivFotoProfil.setImageURI(Uri.parse(savedUri));
        }

        // Tombol Simpan
        findViewById(R.id.btnSimpan).setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nama", etNama.getText().toString());
            editor.putString("hp", etNomorHP.getText().toString());
            editor.putString("email", etEmail.getText().toString());

            // Simpan dengan kunci yang seragam: "foto_profil_path"
            if (imageUri != null) {
                editor.putString("foto_profil_path", imageUri.toString());
            }

            editor.apply();
            Toast.makeText(this, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke halaman ProfilActivity
        });
    }
}