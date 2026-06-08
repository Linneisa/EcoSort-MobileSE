package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.JualRongsokModel;
import com.example.ecosort.model.JualRongsokRequest;
import com.example.ecosort.network.SupabaseClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JualRongsokActivity extends AppCompatActivity {

    private static final String TAG          = "JualRongsok";
    private static final String NOMOR_ADMIN  = "6281273103919";

    private Button   btnSimpan;
    private EditText etNamaPengirim, etNamaBarang, etLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual_rongsok);

        findViewById(R.id.btnBackJual).setOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed());

        btnSimpan      = findViewById(R.id.btnSimpanJualan);
        etNamaPengirim = findViewById(R.id.etNamaPengirim);
        etNamaBarang   = findViewById(R.id.etNamaBarang);
        etLokasi       = findViewById(R.id.etLokasiBarang);

        // Pre-fill nama dari cache jika ada
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String cachedNama = prefs.getString("nama", "");
        if (!cachedNama.isEmpty()) etNamaPengirim.setText(cachedNama);

        btnSimpan.setOnClickListener(v -> ajukanJualan(prefs));
    }

    private void ajukanJualan(SharedPreferences prefs) {
        String nama   = etNamaPengirim.getText().toString().trim();
        String barang = etNamaBarang.getText().toString().trim();
        String lokasi = etLokasi.getText().toString().trim();

        if (nama.isEmpty() || barang.isEmpty() || lokasi.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId      = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");

        // Debug: pastikan sesi tersedia
        Log.d(TAG, "user_id = [" + userId + "]");
        Log.d(TAG, "token ada = " + !accessToken.isEmpty());

        if (userId.isEmpty()) {
            Toast.makeText(this,
                    "Sesi tidak ditemukan. Silakan login ulang.", Toast.LENGTH_LONG).show();
            return;
        }
        if (accessToken.isEmpty()) {
            Toast.makeText(this,
                    "Token tidak valid. Silakan login ulang.", Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);

        JualRongsokRequest request = new JualRongsokRequest(userId, nama, barang, lokasi);
        Log.d(TAG, "Mengirim pengajuan: " + nama + " | " + barang + " | " + lokasi);

        SupabaseClient.getApiService()
                .insertJualRongsok("Bearer " + accessToken, request)
                .enqueue(new Callback<List<JualRongsokModel>>() {

                    @Override
                    public void onResponse(Call<List<JualRongsokModel>> call,
                                           Response<List<JualRongsokModel>> response) {
                        setLoading(false);
                        Log.d(TAG, "Response code: " + response.code());

                        if (response.isSuccessful()) {
                            // INSERT berhasil — coba ambil ID dari body
                            // (body bisa kosong jika RLS SELECT belum diset, tapi data SUDAH tersimpan)
                            String transactionId = null;
                            if (response.body() != null && !response.body().isEmpty()) {
                                transactionId = response.body().get(0).getId();
                                Log.d(TAG, "ID transaksi: " + transactionId);
                            } else {
                                Log.w(TAG, "INSERT sukses tapi body kosong " +
                                        "(cek RLS SELECT policy di tabel jual_rongsok)");
                            }

                            // Data sudah tersimpan → buka WhatsApp
                            bukaWhatsApp(nama, barang, lokasi, transactionId);

                        } else {
                            // INSERT gagal — log error body untuk diagnosa
                            String errBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errBody = response.errorBody().string();
                                }
                            } catch (Exception ignored) {}

                            Log.e(TAG, "INSERT gagal HTTP " + response.code()
                                    + " — " + errBody);

                            String pesanError;
                            switch (response.code()) {
                                case 401:
                                    pesanError = "Sesi expired. Silakan login ulang.";
                                    break;
                                case 403:
                                    pesanError = "Akses ditolak (RLS). Hubungi admin untuk " +
                                                 "mengaktifkan izin INSERT di tabel jual_rongsok.";
                                    break;
                                case 422:
                                    pesanError = "Data tidak valid: " + errBody;
                                    break;
                                default:
                                    pesanError = "Gagal menyimpan pengajuan " +
                                                 "(HTTP " + response.code() + ").";
                            }
                            Toast.makeText(JualRongsokActivity.this,
                                    pesanError, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<JualRongsokModel>> call, Throwable t) {
                        setLoading(false);
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        Toast.makeText(JualRongsokActivity.this,
                                "Tidak dapat terhubung ke server. " +
                                "Periksa koneksi internet Anda.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void bukaWhatsApp(String nama, String barang, String lokasi, String transactionId) {
        String shortId = "UNKNOWN";
        if (transactionId != null && !transactionId.isEmpty()) {
            shortId = transactionId.length() >= 8
                    ? transactionId.substring(0, 8).toUpperCase()
                    : transactionId.toUpperCase();
        }

        String pesan =
                "Halo Admin EcoSort, saya *" + nama + "* ingin menjual rongsokan.\n\n" +
                "📦 *Nama Barang:* " + barang + "\n" +
                "📍 *Lokasi Jemput:* " + lokasi + "\n" +
                "🔖 *ID Transaksi:* " + shortId + "\n\n" +
                "_(Foto barang akan saya kirimkan di sini.)_";

        Log.d(TAG, "Membuka WhatsApp ke " + NOMOR_ADMIN);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://api.whatsapp.com/send?phone=" + NOMOR_ADMIN
                        + "&text=" + Uri.encode(pesan)));

        try {
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Gagal buka WhatsApp: " + e.getMessage());
            Toast.makeText(this,
                    "Pengajuan tersimpan!\n" +
                    "Gagal membuka WhatsApp. " +
                    "Silakan hubungi admin di +62 812-7310-3919.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean isLoading) {
        btnSimpan.setEnabled(!isLoading);
        btnSimpan.setText(isLoading ? "Menyimpan..." : "Ajukan & Mulai Chat Admin");
        btnSimpan.setAlpha(isLoading ? 0.6f : 1.0f);
    }
}
