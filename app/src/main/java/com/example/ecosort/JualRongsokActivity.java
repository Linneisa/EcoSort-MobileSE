package com.example.ecosort;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class JualRongsokActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual_rongsok);

        ImageView btnBack = findViewById(R.id.btnBackJual);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        androidx.cardview.widget.CardView btnUnggahFoto = findViewById(R.id.btnUnggahFoto);
        btnUnggahFoto.setOnClickListener(v -> {
            Toast.makeText(JualRongsokActivity.this, "Membuka Galeri Kamera...", Toast.LENGTH_SHORT).show();
        });

        // Ubah teks tombol menjadi lebih relevan
        Button btnSimpan = findViewById(R.id.btnSimpanJualan);
        btnSimpan.setText("Ajukan & Mulai Chat Admin");

        EditText etNama = findViewById(R.id.etNamaBarang);
        EditText etLokasi = findViewById(R.id.etLokasiBarang);

        btnSimpan.setOnClickListener(v -> {
            String nama = etNama.getText().toString();
            String lokasi = etLokasi.getText().toString();

            // Validasi hanya nama dan lokasi
            if (nama.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(JualRongsokActivity.this, "Mohon isi nama barang dan lokasi penjemputan!", Toast.LENGTH_SHORT).show();
            } else {
                // 1. Tentukan nomor WhatsApp Admin (Gunakan kodifikasi negara 62 tanpa angka 0 di depan)
                // Ganti dengan nomor aslimu agar saat demo presentasi, chat-nya masuk ke HP-mu sendiri
                String nomorAdmin = "6281273103919";

                // 2. Rangkai pesan otomatis yang akan dikirim
                String pesan = "Halo Admin EcoSort, saya ingin menjual barang rongsok.\n\n" +
                        "📦 Nama Barang: " + nama + "\n" +
                        "📍 Lokasi: " + lokasi + "\n\n" +
                        "Apakah bisa dilakukan penjemputan ke lokasi saya di Bandung? Kira-kira bagaimana estimasi harganya?";

                // 3. Buat Intent untuk melompat ke WhatsApp
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse("https://api.whatsapp.com/send?phone=" + nomorAdmin + "&text=" + android.net.Uri.encode(pesan)));

                // 4. Jalankan Intent dengan Try-Catch berjaga-jaga jika HP belum pasang WhatsApp
                try {
                    startActivity(intent);
                    finish(); // Menutup layar formulir setelah melompat ke WA
                } catch (Exception e) {
                    Toast.makeText(JualRongsokActivity.this, "Gagal membuka WhatsApp. Pastikan aplikasi terinstal.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}