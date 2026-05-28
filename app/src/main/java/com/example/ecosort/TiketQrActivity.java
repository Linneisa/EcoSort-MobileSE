package com.example.ecosort;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class TiketQrActivity extends AppCompatActivity {

    private ImageView btnBackTiket, imgQrCode;
    private TextView txtTiketNama, txtTiketBerat, txtTiketMetode, txtTiketPoin;
    private Button btnSelesaiTiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiket_qr);

        btnBackTiket = findViewById(R.id.btnBackTiket);
        imgQrCode = findViewById(R.id.imgQrCode); // Inisialisasi ImageView QR
        txtTiketNama = findViewById(R.id.txtTiketNama);
        txtTiketBerat = findViewById(R.id.txtTiketBerat);
        txtTiketMetode = findViewById(R.id.txtTiketMetode);
        txtTiketPoin = findViewById(R.id.txtTiketPoin);
        btnSelesaiTiket = findViewById(R.id.btnSelesaiTiket);

        String nama = getIntent().getStringExtra("TIKET_NAMA");
        String berat = getIntent().getStringExtra("TIKET_BERAT");
        String poin = getIntent().getStringExtra("TIKET_POIN");
        String metode = getIntent().getStringExtra("TIKET_METODE");

        if (nama != null) txtTiketNama.setText(nama);
        if (berat != null) txtTiketBerat.setText(berat + " kg");
        if (metode != null) txtTiketMetode.setText(metode);
        if (poin != null) txtTiketPoin.setText(poin);

        // --- PROSES GENERATE QR CODE OTOMATIS ---
        String idTransaksi = "ECS-98421"; // Kamu bisa bikin dinamis nanti jika perlu
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // Membuat matriks bit untuk QR Code berukuran 500x500 pixel
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(idTransaksi, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // Pasang bitmap hasil generate ke ImageView kamu
            imgQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        // ----------------------------------------

        btnBackTiket.setOnClickListener(v -> finish());
        btnSelesaiTiket.setOnClickListener(v -> finish());
    }
}