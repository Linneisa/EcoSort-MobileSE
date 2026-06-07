package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- PROTEKSI SESI ---
        // Cek apakah sudah pernah login sebelumnya
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        if (sharedPref.contains("ROLE")) {
            // Kalau sudah ada data, langsung lempar ke MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return; // Hentikan agar tidak load layout login
        }

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 1. Pindah ke Register
        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            });
        }

        // 2. Logika Tombol Masuk
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedPref.edit();

                if (email.equals("admin@ecosort.com") && password.equals("admin123")) {
                    editor.putString("ROLE", "Admin");
                } else {
                    editor.putString("ROLE", "User");
                }

                // Tambahkan email agar bisa dipakai di profil
                editor.putString("email", email);

                // GUNAKAN COMMIT agar sinkron dan langsung tersimpan
                boolean isSaved = editor.commit();

                if (isSaved) {
                    Toast.makeText(LoginActivity.this, "Login Sukses!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Gagal menyimpan sesi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}