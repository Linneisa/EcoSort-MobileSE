package com.example.ecosort;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView btnBackRegister = findViewById(R.id.btnBackRegister);
        EditText etRegisterName = findViewById(R.id.etRegisterName);
        EditText etRegisterEmail = findViewById(R.id.etRegisterEmail);
        EditText etRegisterPassword = findViewById(R.id.etRegisterPassword);
        AppCompatButton btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);

        // Aksi klik tombol kembali
        btnBackRegister.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Aksi klik tombol daftar
        btnRegisterSubmit.setOnClickListener(v -> {
            String name = etRegisterName.getText().toString().trim();
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
            } else {
                // Sisi login database/Firebase bisa ditaruh di sini nantinya
                Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();
                finish(); // Menutup halaman dan kembali ke Login
            }
        });
    }
}