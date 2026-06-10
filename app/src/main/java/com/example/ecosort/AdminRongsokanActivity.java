package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.JualRongsokModel;
import com.example.ecosort.model.JualRongsokUpdateRequest;
import com.example.ecosort.network.SupabaseClient;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRongsokanActivity extends AppCompatActivity
        implements RongsokanAdminAdapter.OnKonfirmasiListener {

    private static final String TAG = "AdminRongsok";

    private RecyclerView          rvRongsok;
    private ProgressBar           progressRongsok;
    private LinearLayout          layoutEmpty;
    private RongsokanAdminAdapter adapter;
    private final List<JualRongsokModel> list = new ArrayList<>();

    private String bearerToken  = "";
    private String adminUserId  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_rongsokan);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("auth_access_token", "");
        bearerToken = "Bearer " + accessToken;
        adminUserId = prefs.getString("user_id", "");

        // Tombol back → ProfilActivity
        findViewById(R.id.btnBackRongsok).setOnClickListener(v -> navigateBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { navigateBack(); }
        });

        rvRongsok       = findViewById(R.id.rvRongsokAdmin);
        progressRongsok = findViewById(R.id.progressRongsok);
        layoutEmpty     = findViewById(R.id.layoutEmptyRongsok);

        adapter = new RongsokanAdminAdapter(list, this);
        rvRongsok.setLayoutManager(new LinearLayoutManager(this));
        rvRongsok.setAdapter(adapter);

        loadPengajuan();
    }

    // ── Supabase: ambil daftar menunggu ──────────────────────────────────────

    private void loadPengajuan() {
        showLoading(true);

        SupabaseClient.getApiService()
                .getJualRongsokMenunggu(bearerToken, "eq.menunggu_verifikasi", "*", "created_at.asc")
                .enqueue(new Callback<List<JualRongsokModel>>() {

                    @Override
                    public void onResponse(Call<List<JualRongsokModel>> call,
                                           Response<List<JualRongsokModel>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            list.clear();
                            list.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + list.size() + " pengajuan menunggu");
                        } else {
                            Log.e(TAG, "Gagal load pengajuan: HTTP " + response.code());
                            Toast.makeText(AdminRongsokanActivity.this,
                                    "Gagal memuat data (kode " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }

                        updateEmptyState();
                    }

                    @Override
                    public void onFailure(Call<List<JualRongsokModel>> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Network error: " + t.getMessage());
                        Toast.makeText(AdminRongsokanActivity.this,
                                "Tidak dapat terhubung ke server.",
                                Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                });
    }

    // ── Callback dari adapter ─────────────────────────────────────────────────

    @Override
    public void onKonfirmasi(JualRongsokModel item, int position, long hargaDeal) {
        // Temukan ViewHolder untuk set loading state
        RecyclerView.ViewHolder rawHolder = rvRongsok.findViewHolderForAdapterPosition(position);
        RongsokanAdminAdapter.ViewHolder holder =
                (rawHolder instanceof RongsokanAdminAdapter.ViewHolder)
                        ? (RongsokanAdminAdapter.ViewHolder) rawHolder : null;

        String namaFormatted = NumberFormat.getNumberInstance(new Locale("in", "ID"))
                .format(hargaDeal);

        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Transaksi")
                .setMessage(
                        "Penjual  : " + item.getNamaPenjual() + "\n" +
                        "Barang   : " + item.getNamaBarang() + "\n" +
                        "Lokasi   : " + item.getLokasi() + "\n" +
                        "Harga Deal: Rp " + namaFormatted + "\n\n" +
                        "Tandai transaksi ini sebagai selesai?"
                )
                .setPositiveButton("Selesaikan", (d, w) ->
                        prosesKonfirmasi(item, position, hargaDeal, holder))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesKonfirmasi(JualRongsokModel item, int position,
                                   long hargaDeal, RongsokanAdminAdapter.ViewHolder holder) {
        if (holder != null) adapter.setBtnLoading(holder, true);

        JualRongsokUpdateRequest updateReq = new JualRongsokUpdateRequest(hargaDeal, adminUserId);

        SupabaseClient.getApiService()
                .updateJualRongsok(bearerToken, "eq." + item.getId(), updateReq)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Harga deal tersimpan di jual_rongsok — TIDAK ada penambahan poin
                            if (holder != null) adapter.setBtnLoading(holder, false);
                            adapter.hapusItem(position);
                            updateEmptyState();
                            tampilkanSukses(item.getNamaPenjual(), item.getNamaBarang(), hargaDeal);
                        } else {
                            if (holder != null) adapter.setBtnLoading(holder, false);
                            Log.e(TAG, "Update gagal: HTTP " + response.code());
                            Toast.makeText(AdminRongsokanActivity.this,
                                    "Gagal mengonfirmasi (kode " + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (holder != null) adapter.setBtnLoading(holder, false);
                        Toast.makeText(AdminRongsokanActivity.this,
                                "Tidak dapat terhubung ke server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void tampilkanSukses(String namaPenjual, String namaBarang, long hargaDeal) {
        String hargaFormatted = "Rp " + NumberFormat.getNumberInstance(new Locale("in", "ID"))
                .format(hargaDeal);

        new AlertDialog.Builder(this)
                .setTitle("Transaksi Selesai!")
                .setMessage(
                        "Transaksi " + namaPenjual + " untuk barang \"" +
                        namaBarang + "\" telah diselesaikan.\n\n" +
                        "Harga deal: " + hargaFormatted
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLoading(boolean show) {
        progressRongsok.setVisibility(show ? View.VISIBLE : View.GONE);
        rvRongsok.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        if (list.isEmpty()) {
            rvRongsok.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvRongsok.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void navigateBack() {
        Intent intent = new Intent(this, ProfilActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
