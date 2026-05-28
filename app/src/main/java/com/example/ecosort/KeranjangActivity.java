package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class KeranjangActivity extends AppCompatActivity {

    private ImageView btnBackKeranjang;
    private TextView txtNamaItemKeranjang, txtDetailBeratKeranjang, txtSubtotalPoin;
    private RadioGroup rgMetodePenyerahan;
    private Button btnTukarSekarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        btnBackKeranjang = findViewById(R.id.btnBackKeranjang);
        txtNamaItemKeranjang = findViewById(R.id.txtNamaItemKeranjang);
        txtDetailBeratKeranjang = findViewById(R.id.txtDetailBeratKeranjang);
        txtSubtotalPoin = findViewById(R.id.txtSubtotalPoin);
        rgMetodePenyerahan = findViewById(R.id.rgMetodePenyerahan);
        btnTukarSekarang = findViewById(R.id.btnTukarSekarang);

        // Menangkap data kiriman dari DetailProdukActivity
        String nama = getIntent().getStringExtra("KARTU_NAMA");
        String berat = getIntent().getStringExtra("KARTU_BERAT");
        String poin = getIntent().getStringExtra("KARTU_POIN");

        if (nama != null) txtNamaItemKeranjang.setText(nama);
        if (berat != null) txtDetailBeratKeranjang.setText("Estimasi: " + berat + " kg");
        if (poin != null) txtSubtotalPoin.setText(poin);

        // Tombol Kembali
        btnBackKeranjang.setOnClickListener(v -> finish());

        // Tombol Konfirmasi Tukar Sekarang -> Lanjut ke Halaman Tiket QR Code (Tahap 3)
        btnTukarSekarang.setOnClickListener(v -> {
            int checkedId = rgMetodePenyerahan.getCheckedRadioButtonId();
            String metodePilihan = "Drop-off";

            if (checkedId == R.id.rbPickUp) {
                metodePilihan = "Pick-up";
            }

            // Pindah ke Halaman Tiket QR Code (Akan kita buat di langkah berikutnya)
            Intent intentKeTiket = new Intent(KeranjangActivity.this, TiketQrActivity.class);
            intentKeTiket.putExtra("TIKET_NAMA", nama);
            intentKeTiket.putExtra("TIKET_BERAT", berat);
            intentKeTiket.putExtra("TIKET_POIN", poin);
            intentKeTiket.putExtra("TIKET_METODE", metodePilihan);
            startActivity(intentKeTiket);
        });
    }
}