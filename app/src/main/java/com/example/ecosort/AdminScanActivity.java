package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.ecosort.model.TransaksiSampahModel;
import com.example.ecosort.model.TransaksiVerifikasiRequest;
import com.example.ecosort.network.SupabaseClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminScanActivity extends AppCompatActivity {

    private LinearLayout layoutHasilScan;
    private TextView     txtIdTransaksi, txtJenisSampah, txtEstimasiBerat, txtPoinFinal;
    private EditText     etBeratAsli;
    private Button       btnKonfirmasi;

    // Data transaksi yang sedang diproses
    private TransaksiSampahModel currentTransaksi;
    private double               ratePerKg = 0; // poin per kg, dihitung dari estimasi

    private String bearerToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_scan);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("auth_access_token", "");
        bearerToken = "Bearer " + accessToken;

        // Tombol back → ProfilActivity
        ImageView btnBackAdmin = findViewById(R.id.btnBackAdmin);
        btnBackAdmin.setOnClickListener(v -> navigateBack());

        // Override back gesture / hardware back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack();
            }
        });

        CardView btnScanBarcode = findViewById(R.id.btnScanBarcode);
        layoutHasilScan   = findViewById(R.id.layoutHasilScan);
        txtIdTransaksi    = findViewById(R.id.txtIdTransaksi);
        txtJenisSampah    = findViewById(R.id.txtJenisSampah);
        txtEstimasiBerat  = findViewById(R.id.txtEstimasiBerat);
        txtPoinFinal      = findViewById(R.id.txtPoinFinal);
        etBeratAsli       = findViewById(R.id.etBeratAsli);
        btnKonfirmasi     = findViewById(R.id.btnKonfirmasiSelesai);

        // Tombol SCAN
        btnScanBarcode.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(AdminScanActivity.this);
            integrator.setPrompt("Arahkan kamera ke QR Code Tiket");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();
        });

        // Hitung ulang poin saat admin mengetik berat asli
        etBeratAsli.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && currentTransaksi != null) {
                    try {
                        double beratAsli = Double.parseDouble(s.toString());
                        int poinBaru     = (int) (beratAsli * ratePerKg);
                        txtPoinFinal.setText(poinBaru + " pts");
                    } catch (NumberFormatException e) {
                        txtPoinFinal.setText("0 pts");
                    }
                } else {
                    txtPoinFinal.setText("0 pts");
                }
            }
        });

        // Tombol Konfirmasi & Cairkan Poin
        btnKonfirmasi.setOnClickListener(v -> {
            String beratInput = etBeratAsli.getText().toString().trim();
            if (beratInput.isEmpty()) {
                Toast.makeText(this, "Masukkan berat asli terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentTransaksi == null) {
                Toast.makeText(this, "Tidak ada transaksi yang dipilih.", Toast.LENGTH_SHORT).show();
                return;
            }

            double beratAsli = 0;
            int    poinBaru  = 0;
            try {
                beratAsli = Double.parseDouble(beratInput);
                poinBaru  = (int) (beratAsli * ratePerKg);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Format berat tidak valid.", Toast.LENGTH_SHORT).show();
                return;
            }

            tampilkanDialogKonfirmasi(beratAsli, poinBaru);
        });
    }

    // Tangkap hasil scan kamera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                fetchTransaksiDariSupabase(result.getContents());
            } else {
                Toast.makeText(this, "Scan dibatalkan.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ── Supabase: Ambil data transaksi berdasarkan UUID dari QR ─────────────

    private void fetchTransaksiDariSupabase(String transactionId) {
        Toast.makeText(this, "Mencari transaksi...", Toast.LENGTH_SHORT).show();
        layoutHasilScan.setVisibility(View.GONE);
        currentTransaksi = null;

        SupabaseClient.getApiService()
                .getTransaksiById(bearerToken, "eq." + transactionId, "*")
                .enqueue(new Callback<List<TransaksiSampahModel>>() {

                    @Override
                    public void onResponse(Call<List<TransaksiSampahModel>> call,
                                           Response<List<TransaksiSampahModel>> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {

                            TransaksiSampahModel t = response.body().get(0);

                            if ("terverifikasi".equals(t.getStatus())) {
                                Toast.makeText(AdminScanActivity.this,
                                        "Transaksi ini sudah terverifikasi sebelumnya.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }

                            currentTransaksi = t;

                            // Hitung rate poin per kg dari estimasi awal
                            ratePerKg = (t.getBerat() > 0)
                                    ? (double) t.getPoinDidapat() / t.getBerat()
                                    : 500; // fallback default

                            tampilkanDataTransaksi(t);

                        } else if (response.isSuccessful()) {
                            Toast.makeText(AdminScanActivity.this,
                                    "Transaksi tidak ditemukan.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminScanActivity.this,
                                    "Gagal ambil data (kode " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TransaksiSampahModel>> call, Throwable t) {
                        Toast.makeText(AdminScanActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tampilkanDataTransaksi(TransaksiSampahModel t) {
        String shortId = t.getId() != null && t.getId().length() >= 8
                ? t.getId().substring(0, 8).toUpperCase()
                : t.getId();

        txtIdTransaksi.setText("ID: " + shortId + "... | " + t.getNamaSampah());
        txtJenisSampah.setText("Jenis: " + t.getNamaSampah());
        txtEstimasiBerat.setText("Estimasi user: " + t.getBerat() + " kg"
                + " (" + t.getPoinDidapat() + " pts estimasi)");
        txtPoinFinal.setText("0 pts");
        etBeratAsli.setText("");

        layoutHasilScan.setVisibility(View.VISIBLE);
    }

    // ── Dialog + Supabase: Verifikasi transaksi ──────────────────────────────

    private void tampilkanDialogKonfirmasi(double beratAsli, int poinBaru) {
        String namaSampah = currentTransaksi.getNamaSampah();

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Verifikasi")
                .setMessage(
                        "Jenis     : " + namaSampah + "\n" +
                        "Berat asli: " + beratAsli + " kg\n" +
                        "Poin      : " + poinBaru + " pts\n\n" +
                        "Cairkan poin ke akun pengguna?"
                )
                .setPositiveButton("Konfirmasi", (d, w) -> verifikasiTransaksi(beratAsli, poinBaru))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void verifikasiTransaksi(double beratAsli, int poinBaru) {
        setBtnLoading(true);

        String idTransaksi = currentTransaksi.getId();
        String userId      = currentTransaksi.getUserId();

        TransaksiVerifikasiRequest req =
                new TransaksiVerifikasiRequest(beratAsli, poinBaru);

        // Step 1: Update transaksi_sampah
        SupabaseClient.getApiService()
                .updateTransaksiVerifikasi(bearerToken, "eq." + idTransaksi, req)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Step 2: Tambah poin ke user pemilik transaksi
                            cairkanPoinUser(userId, poinBaru);
                        } else {
                            setBtnLoading(false);
                            Toast.makeText(AdminScanActivity.this,
                                    "Gagal update transaksi (kode " + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        setBtnLoading(false);
                        Toast.makeText(AdminScanActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cairkanPoinUser(String userId, int poinBaru) {
        UserPointsHelper.addPoinToUser(userId, bearerToken, poinBaru,
                new UserPointsHelper.PoinCallback() {

                    @Override
                    public void onSuccess(int totalPoin) {
                        setBtnLoading(false);
                        tampilkanSukses(poinBaru, totalPoin);
                        resetUI();
                    }

                    @Override
                    public void onFailure(String pesan) {
                        setBtnLoading(false);
                        // Transaksi sudah terverifikasi — beri tahu admin
                        Toast.makeText(AdminScanActivity.this,
                                "Transaksi terverifikasi, tapi poin gagal dicairkan: " + pesan,
                                Toast.LENGTH_LONG).show();
                        resetUI();
                    }
                });
    }

    private void tampilkanSukses(int poinDicairkan, int totalPoinUser) {
        new AlertDialog.Builder(this)
                .setTitle("Verifikasi Berhasil!")
                .setMessage(
                        "+" + poinDicairkan + " pts berhasil dicairkan.\n" +
                        "Total poin pengguna sekarang: " + totalPoinUser + " pts."
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void resetUI() {
        currentTransaksi = null;
        ratePerKg = 0;
        layoutHasilScan.setVisibility(View.GONE);
        etBeratAsli.setText("");
        txtPoinFinal.setText("0 pts");
    }

    private void setBtnLoading(boolean isLoading) {
        btnKonfirmasi.setEnabled(!isLoading);
        btnKonfirmasi.setText(isLoading ? "Memproses..." : "Konfirmasi & Cairkan Poin");
        btnKonfirmasi.setAlpha(isLoading ? 0.6f : 1.0f);
    }

    // Kembali ke ProfilActivity (yang menampilkan menu admin)
    private void navigateBack() {
        Intent intent = new Intent(this, ProfilActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
