package com.example.ecosort;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.text.NumberFormat;
import java.util.Locale;

public class TiketQrActivity extends AppCompatActivity {

    private ImageView btnBackTiket, imgQrCode;
    private TextView  txtTiketNama, txtTiketBerat, txtTiketMetode,
                      txtTiketPoin, txtTotalPoinUser;
    private Button btnSelesaiTiket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiket_qr);

        btnBackTiket     = findViewById(R.id.btnBackTiket);
        imgQrCode        = findViewById(R.id.imgQrCode);
        txtTiketNama     = findViewById(R.id.txtTiketNama);
        txtTiketBerat    = findViewById(R.id.txtTiketBerat);
        txtTiketMetode   = findViewById(R.id.txtTiketMetode);
        txtTiketPoin     = findViewById(R.id.txtTiketPoin);
        txtTotalPoinUser = findViewById(R.id.txtTotalPoinUser);
        btnSelesaiTiket  = findViewById(R.id.btnSelesaiTiket);

        String nama          = getIntent().getStringExtra("TIKET_NAMA");
        String berat         = getIntent().getStringExtra("TIKET_BERAT");
        String poin          = getIntent().getStringExtra("TIKET_POIN");
        String metode        = getIntent().getStringExtra("TIKET_METODE");
        String transactionId = getIntent().getStringExtra("TIKET_ID");

        if (nama   != null) txtTiketNama.setText(nama);
        if (berat  != null) txtTiketBerat.setText(berat + " kg");
        if (metode != null) txtTiketMetode.setText(metode);
        if (poin   != null) txtTiketPoin.setText(poin + " (estimasi)");

        // Tampilkan poin saat ini (belum bertambah — poin baru masuk setelah admin verifikasi)
        if (txtTotalPoinUser != null) {
            int poinSekarang = UserPointsHelper.getCachedPoin(this);
            txtTotalPoinUser.setText(
                    NumberFormat.getNumberInstance(new Locale("in", "ID"))
                            .format(poinSekarang) + " pts");
        }

        // QR code berisi UUID transaksi dari Supabase agar admin bisa scan
        String qrContent = (transactionId != null && !transactionId.isEmpty())
                ? transactionId
                : "ECS-UNKNOWN";

        try {
            com.google.zxing.common.BitMatrix bitMatrix =
                    new MultiFormatWriter().encode(qrContent, BarcodeFormat.QR_CODE, 500, 500);
            imgQrCode.setImageBitmap(new BarcodeEncoder().createBitmap(bitMatrix));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // Beritahu user bahwa transaksi menunggu verifikasi
        Toast.makeText(this,
                "Transaksi diajukan! Tunjukkan QR ini ke admin untuk verifikasi.",
                Toast.LENGTH_LONG).show();

        btnBackTiket.setOnClickListener(v -> finish());

        btnSelesaiTiket.setOnClickListener(v -> {
            Intent intent = new Intent(this, MarketplaceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
