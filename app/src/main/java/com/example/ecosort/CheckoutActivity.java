package com.example.ecosort;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView btnBackCheckout, imgCheckout;
    private TextView txtNamaCheckout, txtHargaCheckout, txtBeratCheckout;
    private Button btnKonfirmasiCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        btnBackCheckout = findViewById(R.id.btnBackCheckout);
        imgCheckout = findViewById(R.id.imgCheckout);
        txtNamaCheckout = findViewById(R.id.txtNamaCheckout);
        txtHargaCheckout = findViewById(R.id.txtHargaCheckout);
        txtBeratCheckout = findViewById(R.id.txtBeratCheckout);
        btnKonfirmasiCheckout = findViewById(R.id.btnKonfirmasiCheckout);

        // Ambil data kiriman dari ProdukAdapter
        String nama = getIntent().getStringExtra("PRODUK_NAMA");
        String harga = getIntent().getStringExtra("PRODUK_HARGA");
        String berat = getIntent().getStringExtra("PRODUK_BERAT");
        int gambarResId = getIntent().getIntExtra("PRODUK_GAMBAR", android.R.drawable.ic_menu_gallery);

        // Set ke tampilan halaman checkout
        if (nama != null) txtNamaCheckout.setText(nama);
        if (harga != null) txtHargaCheckout.setText(harga);
        if (berat != null) txtBeratCheckout.setText(berat);
        imgCheckout.setImageResource(gambarResId);

        // Tombol Kembali
        btnBackCheckout.setOnClickListener(v -> finish());

        // Tombol Eksekusi Transaksi Sukses
        btnKonfirmasiCheckout.setOnClickListener(v -> {
            Toast.makeText(this, "Transaksi Berhasil! Poin diproses.", Toast.LENGTH_LONG).show();
            finish(); // Tutup halaman setelah sukses
        });
    }
}