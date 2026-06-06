package com.example.ecosort;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JualRongsokActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual_rongsok);

        // Tombol Kembali
        findViewById(R.id.btnBackJual).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Inisialisasi Input dan Tombol
        Button btnSimpan = findViewById(R.id.btnSimpanJualan);
        EditText etNamaPengirim = findViewById(R.id.etNamaPengirim);
        EditText etNamaBarang = findViewById(R.id.etNamaBarang);
        EditText etLokasi = findViewById(R.id.etLokasiBarang);

        btnSimpan.setOnClickListener(v -> {
            String pengirim = etNamaPengirim.getText().toString().trim();
            String barang = etNamaBarang.getText().toString().trim();
            String lokasi = etLokasi.getText().toString().trim();

            if (pengirim.isEmpty() || barang.isEmpty() || lokasi.isEmpty()) {
                Toast.makeText(JualRongsokActivity.this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show();
            } else {
                // 1. Tentukan nomor WhatsApp tujuan
                // PENTING: Ganti dengan nomormu sendiri (gunakan 62, tanpa 0 di depan)
                String nomorAdmin = "6281234567890";

                // 2. Rangkai pesan otomatis
                String pesan = "Halo Admin EcoSort, saya *" + pengirim + "* ingin bernegosiasi untuk menjual rongsokan.\n\n" +
                        "📦 *Nama Barang:* " + barang + "\n" +
                        "📍 *Lokasi Jemput:* " + lokasi + "\n\n" +
                        "_(Foto barang akan saya kirimkan di sini)._\n\n" +
                        "Apakah tim penjemput bisa datang ke lokasi saya?";

                // 3. Eksekusi Intent ke WhatsApp
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(android.net.Uri.parse("https://api.whatsapp.com/send?phone=" + nomorAdmin + "&text=" + android.net.Uri.encode(pesan)));

                try {
                    startActivity(intent);
                    finish(); // Menutup formulir setelah membuka WA
                } catch (Exception e) {
                    Toast.makeText(JualRongsokActivity.this, "Gagal membuka WhatsApp. Pastikan aplikasi terinstal.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}