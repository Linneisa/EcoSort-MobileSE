package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.TransaksiSampahModel;
import com.example.ecosort.model.TransaksiSampahRequest;
import com.example.ecosort.network.SupabaseClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeranjangActivity extends AppCompatActivity {

    private ImageView  btnBackKeranjang;
    private TextView   txtNamaItemKeranjang, txtDetailBeratKeranjang, txtSubtotalPoin;
    private RadioGroup rgMetodePenyerahan;
    private Button     btnTukarSekarang;

    private String namaSampah, beratStr, poinStr;
    private int    poinInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        btnBackKeranjang        = findViewById(R.id.btnBackKeranjang);
        txtNamaItemKeranjang    = findViewById(R.id.txtNamaItemKeranjang);
        txtDetailBeratKeranjang = findViewById(R.id.txtDetailBeratKeranjang);
        txtSubtotalPoin         = findViewById(R.id.txtSubtotalPoin);
        rgMetodePenyerahan      = findViewById(R.id.rgMetodePenyerahan);
        btnTukarSekarang        = findViewById(R.id.btnTukarSekarang);

        namaSampah = getIntent().getStringExtra("KARTU_NAMA");
        beratStr   = getIntent().getStringExtra("KARTU_BERAT");
        poinStr    = getIntent().getStringExtra("KARTU_POIN");

        if (poinStr != null) {
            String angkaSaja = poinStr.replaceAll("[^0-9]", "");
            poinInt = angkaSaja.isEmpty() ? 0 : Integer.parseInt(angkaSaja);
        }

        if (namaSampah != null) txtNamaItemKeranjang.setText(namaSampah);
        if (beratStr   != null) txtDetailBeratKeranjang.setText("Estimasi: " + beratStr + " kg");
        if (poinStr    != null) txtSubtotalPoin.setText(poinStr);

        btnBackKeranjang.setOnClickListener(v -> finish());
        btnTukarSekarang.setOnClickListener(v -> tampilkanDialogKonfirmasi());
    }

    private void tampilkanDialogKonfirmasi() {
        int checkedId = rgMetodePenyerahan.getCheckedRadioButtonId();
        String metode = (checkedId == R.id.rbPickUp) ? "Pick-up" : "Drop-off";

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Jual Sampah")
                .setMessage(
                        "Jenis sampah  : " + namaSampah + "\n" +
                        "Estimasi berat: " + beratStr + " kg\n" +
                        "Metode        : " + metode + "\n" +
                        "Estimasi poin : " + poinStr + "\n\n" +
                        "Poin aktual dihitung ulang oleh admin setelah timbang.\n" +
                        "Lanjutkan?"
                )
                .setPositiveButton("Ya, Ajukan", (dialog, which) -> prosesTransaksi(metode))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesTransaksi(String metode) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId      = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        double beratDouble = 0;
        try { beratDouble = Double.parseDouble(beratStr); }
        catch (NumberFormatException ignored) {}

        // status otomatis "menunggu" — poin baru ditambah setelah admin verifikasi
        TransaksiSampahRequest request =
                new TransaksiSampahRequest(userId, namaSampah, beratDouble, poinInt);

        SupabaseClient.getApiService()
                .insertTransaksiSampah("Bearer " + accessToken, request)
                .enqueue(new Callback<List<TransaksiSampahModel>>() {

                    @Override
                    public void onResponse(Call<List<TransaksiSampahModel>> call,
                                           Response<List<TransaksiSampahModel>> response) {
                        setLoading(false);
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {

                            // Ambil ID asli dari Supabase untuk QR
                            String transactionId = response.body().get(0).getId();
                            navigateToTiket(metode, transactionId);

                        } else if (response.code() == 401) {
                            // Token kedaluwarsa — hapus sesi dan minta login ulang
                            getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                    .edit().remove("ROLE").apply();
                            Toast.makeText(KeranjangActivity.this,
                                    "Sesi telah berakhir, silakan login kembali.",
                                    Toast.LENGTH_LONG).show();
                            startActivity(new Intent(KeranjangActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(KeranjangActivity.this,
                                    "Gagal menyimpan transaksi (kode " + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TransaksiSampahModel>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(KeranjangActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToTiket(String metode, String transactionId) {
        Intent intent = new Intent(this, TiketQrActivity.class);
        intent.putExtra("TIKET_NAMA",   namaSampah);
        intent.putExtra("TIKET_BERAT",  beratStr);
        intent.putExtra("TIKET_POIN",   poinStr);
        intent.putExtra("TIKET_METODE", metode);
        intent.putExtra("TIKET_ID",     transactionId);   // UUID asli dari Supabase
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        btnTukarSekarang.setEnabled(!isLoading);
        btnTukarSekarang.setText(isLoading ? "Memproses..." : "Tukar Sekarang");
        btnTukarSekarang.setAlpha(isLoading ? 0.6f : 1.0f);
    }
}
