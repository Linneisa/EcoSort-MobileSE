package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.TransaksiCancelRequest;
import com.example.ecosort.network.SupabaseClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private Button btnSelesaiTiket, btnBatalkanTiket;

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
        btnBatalkanTiket = findViewById(R.id.btnBatalkanTiket);

        String nama          = getIntent().getStringExtra("TIKET_NAMA");
        String berat         = getIntent().getStringExtra("TIKET_BERAT");
        String poin          = getIntent().getStringExtra("TIKET_POIN");
        String metode        = getIntent().getStringExtra("TIKET_METODE");
        String transactionId = getIntent().getStringExtra("TIKET_ID");
        boolean isReview     = getIntent().getBooleanExtra("IS_REVIEW", false);

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

        if (!isReview) {
            // Toast hanya muncul saat transaksi baru dibuat, bukan saat ditinjau ulang
            Toast.makeText(this,
                    "Transaksi diajukan! Tunjukkan QR ini ke admin untuk verifikasi.",
                    Toast.LENGTH_LONG).show();
        }

        btnBackTiket.setOnClickListener(v -> finish());

        final String finalTransactionId = transactionId;

        if (isReview) {
            // Dibuka dari Riwayat — tampilkan tombol Batalkan dan Tutup
            btnSelesaiTiket.setText("Tutup");
            btnSelesaiTiket.setOnClickListener(v -> finish());
            btnBatalkanTiket.setVisibility(View.VISIBLE);
            btnBatalkanTiket.setOnClickListener(v -> konfirmasiBatalkan(finalTransactionId));
        } else {
            btnSelesaiTiket.setOnClickListener(v -> {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void konfirmasiBatalkan(String txId) {
        if (txId == null || txId.isEmpty()) return;
        new AlertDialog.Builder(this)
                .setTitle("Batalkan Transaksi")
                .setMessage("Apakah kamu yakin ingin membatalkan transaksi ini?")
                .setPositiveButton("Ya, Batalkan", (d, w) -> kirimBatalkan(txId))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void kirimBatalkan(String txId) {
        btnBatalkanTiket.setEnabled(false);
        btnBatalkanTiket.setText("Membatalkan...");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("auth_access_token", "");

        SupabaseClient.getApiService()
                .cancelTransaksi(
                        "Bearer " + accessToken,
                        "eq." + txId,
                        new TransaksiCancelRequest())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(TiketQrActivity.this,
                                    "Transaksi berhasil dibatalkan.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            btnBatalkanTiket.setEnabled(true);
                            btnBatalkanTiket.setText("Batalkan Transaksi");
                            Toast.makeText(TiketQrActivity.this,
                                    "Gagal membatalkan transaksi (kode " + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        btnBatalkanTiket.setEnabled(true);
                        btnBatalkanTiket.setText("Batalkan Transaksi");
                        Toast.makeText(TiketQrActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
