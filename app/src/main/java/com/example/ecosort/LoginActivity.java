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
        setContentView(R.layout.activity_login);

        // Menyembunyikan ActionBar bawaan biar fullscreen kustom
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Inisialisasi komponen XML
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 1. Logika Klik untuk pindah ke halaman Register
        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        // 2. Logika Klik untuk tombol Masuk (DITAMBAH SENSOR ADMIN)
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show();
            } else {

                // --- MEMBUKA MEMORI SESI (SharedPreferences) ---
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Cek apakah yang login adalah Admin Ecosort
                if (email.equals("admin@ecosort.com") && password.equals("admin123")) {
                    editor.putString("ROLE", "Admin");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Selamat datang di Panel Admin!", Toast.LENGTH_SHORT).show();
                } else {
                    // Jika user biasa
                    editor.putString("ROLE", "User");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Login Sukses!", Toast.LENGTH_SHORT).show();
                }

                // Tetap mengarah ke MainActivity sesuai alur aslimu
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}