package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

    private static final String NOMOR_ADMIN = "6281273103919";

    private Button   btnSimpan;
    private EditText etNamaPengirim, etNamaBarang, etLokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jual_rongsok);

        findViewById(R.id.btnBackJual).setOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed());

        btnSimpan       = findViewById(R.id.btnSimpanJualan);
        etNamaPengirim  = findViewById(R.id.etNamaPengirim);
        etNamaBarang    = findViewById(R.id.etNamaBarang);
        etLokasi        = findViewById(R.id.etLokasiBarang);

        // Pre-fill nama dari SharedPreferences jika ada
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

        if (userId.isEmpty() || accessToken.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        JualRongsokRequest request = new JualRongsokRequest(userId, nama, barang, lokasi);

        SupabaseClient.getApiService()
                .insertJualRongsok("Bearer " + accessToken, request)
                .enqueue(new Callback<List<JualRongsokModel>>() {

                    @Override
                    public void onResponse(Call<List<JualRongsokModel>> call,
                                           Response<List<JualRongsokModel>> response) {
                        setLoading(false);

                        if (response.isSuccessful()
                                && response.body() != null
                                && !response.body().isEmpty()) {

                            String transactionId = response.body().get(0).getId();
                            bukaWhatsApp(nama, barang, lokasi, transactionId);

                        } else {
                            Toast.makeText(JualRongsokActivity.this,
                                    "Gagal menyimpan pengajuan (kode " + response.code() + "). " +
                                    "Coba lagi atau hubungi admin langsung.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<JualRongsokModel>> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(JualRongsokActivity.this,
                                "Tidak dapat terhubung ke server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bukaWhatsApp(String nama, String barang, String lokasi, String transactionId) {
        // Short ID untuk pesan WhatsApp (8 karakter pertama UUID)
        String shortId = (transactionId != null && transactionId.length() >= 8)
                ? transactionId.substring(0, 8).toUpperCase()
                : (transactionId != null ? transactionId : "UNKNOWN");

        String pesan =
                "Halo Admin EcoSort, saya *" + nama + "* ingin menjual rongsokan.\n\n" +
                "📦 *Nama Barang:* " + barang + "\n" +
                "📍 *Lokasi Jemput:* " + lokasi + "\n" +
                "🔖 *ID Transaksi:* " + shortId + "\n\n" +
                "_(Foto barang akan saya kirimkan di sini.)_";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://api.whatsapp.com/send?phone=" + NOMOR_ADMIN
                        + "&text=" + Uri.encode(pesan)));

        try {
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(this,
                    "Pengajuan tersimpan! Namun gagal membuka WhatsApp. " +
                    "Silakan hubungi admin secara manual.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void setLoading(boolean isLoading) {
        btnSimpan.setEnabled(!isLoading);
        btnSimpan.setText(isLoading ? "Menyimpan..." : "Ajukan & Mulai Chat Admin");
        btnSimpan.setAlpha(isLoading ? 0.6f : 1.0f);
    }
}
