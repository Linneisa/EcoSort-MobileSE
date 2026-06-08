package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.PenukaranRiwayatModel;
import com.example.ecosort.model.RewardModel;
import com.example.ecosort.model.TransaksiSampahModel;
import com.example.ecosort.network.SupabaseClient;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatActivity extends AppCompatActivity {

    private static final String TAG = "RiwayatActivity";

    private TextView     btnSemua, btnMasuk, btnKeluar, tvEmptyRiwayat;
    private RecyclerView rvRiwayat;
    private ProgressBar  progressRiwayat;
    private ImageView    btnBackHistory;

    private final List<Riwayat> semuaRiwayatList = new ArrayList<>();
    private String filterAktif = "Semua";

    // 3 parallel loading counters
    private List<TransaksiSampahModel>  transaksiList;
    private List<PenukaranRiwayatModel> penukaranList;
    private List<RewardModel>           rewardsList;
    private int loadedCount = 0; // increments to 3 when all done

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        btnBackHistory  = findViewById(R.id.btnBackHistory);
        btnSemua        = findViewById(R.id.btnFilterSemua);
        btnMasuk        = findViewById(R.id.btnFilterMasuk);
        btnKeluar       = findViewById(R.id.btnFilterKeluar);
        rvRiwayat       = findViewById(R.id.rvRiwayat);
        progressRiwayat = findViewById(R.id.progressRiwayat);
        tvEmptyRiwayat  = findViewById(R.id.tvEmptyRiwayat);

        rvRiwayat.setLayoutManager(new LinearLayoutManager(this));

        btnBackHistory.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnSemua.setOnClickListener(v -> {
            setKategoriAktif(btnSemua);
            filterAktif = "Semua";
            filterData();
        });
        btnMasuk.setOnClickListener(v -> {
            setKategoriAktif(btnMasuk);
            filterAktif = "Masuk";
            filterData();
        });
        btnKeluar.setOnClickListener(v -> {
            setKategoriAktif(btnKeluar);
            filterAktif = "Keluar";
            filterData();
        });

        // Foto profil header
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfilActivity.class)));
        }
        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            String fotoPath = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    .getString("foto_profil_path", null);
            if (fotoPath != null) imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            else imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
        }

        loadRiwayat();
    }

    // ── Supabase ─────────────────────────────────────────────────────────────

    private void loadRiwayat() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId      = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            showLoading(false);
            tvEmptyRiwayat.setVisibility(View.VISIBLE);
            tvEmptyRiwayat.setText("Silakan login terlebih dahulu.");
            return;
        }

        String bearer = "Bearer " + accessToken;
        showLoading(true);
        loadedCount = 0;

        // ── 1. Transaksi sampah (pemasukan) ──────────────────────────────────
        SupabaseClient.getApiService()
                .getTransaksiByUser(bearer, "eq." + userId, "*", "tanggal.desc")
                .enqueue(new Callback<List<TransaksiSampahModel>>() {
                    @Override
                    public void onResponse(Call<List<TransaksiSampahModel>> call,
                                           Response<List<TransaksiSampahModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            transaksiList = response.body();
                            Log.d(TAG, "transaksi_sampah: " + transaksiList.size() + " baris");
                        } else {
                            transaksiList = new ArrayList<>();
                            Log.e(TAG, "transaksi_sampah gagal: HTTP " + response.code());
                        }
                        onOneLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<TransaksiSampahModel>> call, Throwable t) {
                        transaksiList = new ArrayList<>();
                        Log.e(TAG, "transaksi_sampah network error: " + t.getMessage());
                        onOneLoaded();
                    }
                });

        // ── 2. Penukaran reward (pengeluaran) — select * tanpa join ──────────
        //
        // PENTING: Jangan pakai "select=*,rewards(nama)" via @Query karena Retrofit
        // akan URL-encode tanda kurung () sehingga PostgREST gagal parse join-nya.
        // Nama reward diambil dari fetch #3 (rewards list) dan digabungkan di memory.
        SupabaseClient.getApiService()
                .getPenukaranByUser(bearer, "eq." + userId, "*", "tanggal.desc")
                .enqueue(new Callback<List<PenukaranRiwayatModel>>() {
                    @Override
                    public void onResponse(Call<List<PenukaranRiwayatModel>> call,
                                           Response<List<PenukaranRiwayatModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            penukaranList = response.body();
                            Log.d(TAG, "penukaran_reward: " + penukaranList.size() + " baris");
                        } else {
                            penukaranList = new ArrayList<>();
                            Log.e(TAG, "penukaran_reward gagal: HTTP " + response.code());
                        }
                        onOneLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<PenukaranRiwayatModel>> call, Throwable t) {
                        penukaranList = new ArrayList<>();
                        Log.e(TAG, "penukaran_reward network error: " + t.getMessage());
                        onOneLoaded();
                    }
                });

        // ── 3. Daftar reward (untuk lookup nama, public endpoint) ────────────
        SupabaseClient.getApiService()
                .getRewards("id,nama", "id.asc")
                .enqueue(new Callback<List<RewardModel>>() {
                    @Override
                    public void onResponse(Call<List<RewardModel>> call,
                                           Response<List<RewardModel>> response) {
                        rewardsList = (response.isSuccessful() && response.body() != null)
                                ? response.body() : new ArrayList<>();
                        Log.d(TAG, "rewards: " + rewardsList.size() + " baris");
                        onOneLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<RewardModel>> call, Throwable t) {
                        rewardsList = new ArrayList<>();
                        Log.e(TAG, "rewards network error: " + t.getMessage());
                        onOneLoaded();
                    }
                });
    }

    /** Dipanggil dari tiga callback — proses data setelah ketiganya selesai. */
    private synchronized void onOneLoaded() {
        loadedCount++;
        if (loadedCount == 3) {
            runOnUiThread(this::prosesData);
        }
    }

    private void prosesData() {
        // Buat map rewardId → nama untuk lookup cepat
        Map<Integer, String> namaMap = new HashMap<>();
        for (RewardModel r : rewardsList) {
            namaMap.put(r.getId(), r.getNama());
        }

        List<RiwayatItem> items = new ArrayList<>();

        // Transaksi sampah → Masuk
        for (TransaksiSampahModel t : transaksiList) {
            String judul = t.getNamaSampah();
            if ("menunggu".equals(t.getStatus())) judul += " (Menunggu Verifikasi)";
            items.add(new RiwayatItem(
                    t.getTanggal(),
                    new Riwayat(judul, formatTanggal(t.getTanggal()),
                            String.valueOf(t.getPoinDidapat()), "Masuk")));
        }

        // Penukaran reward → Keluar
        for (PenukaranRiwayatModel p : penukaranList) {
            String namaReward = namaMap.containsKey(p.getRewardId())
                    ? namaMap.get(p.getRewardId())
                    : "Reward #" + p.getRewardId();
            items.add(new RiwayatItem(
                    p.getTanggal(),
                    new Riwayat("Tukar: " + namaReward,
                            formatTanggal(p.getTanggal()),
                            String.valueOf(p.getJumlahPoin()), "Keluar")));
        }

        // Urutkan terbaru → terlama (ISO 8601 string aman dibandingkan lexicografis)
        Collections.sort(items, (a, b) -> {
            if (a.tanggalIso == null) return 1;
            if (b.tanggalIso == null) return -1;
            return b.tanggalIso.compareTo(a.tanggalIso);
        });

        semuaRiwayatList.clear();
        for (RiwayatItem item : items) semuaRiwayatList.add(item.riwayat);

        showLoading(false);
        filterData();
    }

    // ── UI ────────────────────────────────────────────────────────────────────

    private void filterData() {
        List<Riwayat> filtered = new ArrayList<>();
        if ("Semua".equals(filterAktif)) {
            filtered.addAll(semuaRiwayatList);
        } else {
            for (Riwayat r : semuaRiwayatList) {
                if (r.getJenis().equalsIgnoreCase(filterAktif)) filtered.add(r);
            }
        }

        if (filtered.isEmpty()) {
            rvRiwayat.setVisibility(View.GONE);
            tvEmptyRiwayat.setVisibility(View.VISIBLE);
            tvEmptyRiwayat.setText("Belum ada transaksi");
        } else {
            tvEmptyRiwayat.setVisibility(View.GONE);
            rvRiwayat.setVisibility(View.VISIBLE);
            rvRiwayat.setAdapter(new RiwayatAdapter(filtered));
        }
    }

    private void showLoading(boolean show) {
        progressRiwayat.setVisibility(show ? View.VISIBLE : View.GONE);
        rvRiwayat.setVisibility(View.GONE);
        tvEmptyRiwayat.setVisibility(View.GONE);
    }

    private void setKategoriAktif(TextView tombolTerpilih) {
        for (TextView btn : new TextView[]{btnSemua, btnMasuk, btnKeluar}) {
            btn.setBackgroundResource(R.drawable.bg_tab_inactive);
            btn.setTextColor(Color.parseColor("#475569"));
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        tombolTerpilih.setBackgroundResource(R.drawable.bg_tab_active);
        tombolTerpilih.setTextColor(Color.parseColor("#050F0B"));
        tombolTerpilih.setTypeface(null, android.graphics.Typeface.BOLD);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Konversi ISO 8601 Supabase → "dd MMM yyyy • HH:mm" */
    private static String formatTanggal(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            String s = iso.replaceAll("(\\.[0-9]+)?(\\+.*|Z).*$", "");
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(s);
            SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy • HH:mm",
                    new Locale("in", "ID"));
            fmt.setTimeZone(TimeZone.getDefault());
            return fmt.format(date);
        } catch (Exception e) {
            return iso;
        }
    }

    private static class RiwayatItem {
        final String  tanggalIso;
        final Riwayat riwayat;

        RiwayatItem(String tanggalIso, Riwayat riwayat) {
            this.tanggalIso = tanggalIso;
            this.riwayat    = riwayat;
        }
    }
}
