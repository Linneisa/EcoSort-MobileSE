package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import com.example.ecosort.model.TransaksiCancelRequest;
import android.graphics.Color;
import android.net.Uri;
import java.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.JualRongsokModel;
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
    private static final int REQUEST_VIEW_TIKET = 1001;

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
    private List<JualRongsokModel>      rongsokList;
    private int loadedCount = 0; // increments to 4 when all done

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

        // ── 4. Jual rongsok milik user (semua status) ─────────────────────────
        SupabaseClient.getApiService()
                .getJualRongsokByUser(bearer, "eq." + userId, "*", "created_at.desc")
                .enqueue(new Callback<List<JualRongsokModel>>() {
                    @Override
                    public void onResponse(Call<List<JualRongsokModel>> call,
                                           Response<List<JualRongsokModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            rongsokList = response.body();
                            Log.d(TAG, "jual_rongsok: " + rongsokList.size() + " baris");
                        } else {
                            rongsokList = new ArrayList<>();
                            Log.e(TAG, "jual_rongsok gagal: HTTP " + response.code());
                        }
                        onOneLoaded();
                    }
                    @Override
                    public void onFailure(Call<List<JualRongsokModel>> call, Throwable t) {
                        rongsokList = new ArrayList<>();
                        Log.e(TAG, "jual_rongsok network error: " + t.getMessage());
                        onOneLoaded();
                    }
                });
    }

    /** Dipanggil dari empat callback — proses data setelah keempatnya selesai. */
    private synchronized void onOneLoaded() {
        loadedCount++;
        if (loadedCount == 4) {
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
            boolean menunggu = "menunggu".equals(t.getStatus());
            if (menunggu) judul += " (Menunggu Verifikasi)";
            // Poin belum dikreditkan sampai admin verifikasi — tampilkan 0 saat masih pending
            String poinTampil = menunggu ? "0" : String.valueOf(t.getPoinDidapat());
            Riwayat r = new Riwayat(judul, formatTanggal(t.getTanggal()), poinTampil, "Masuk");
            r.setTransactionId(t.getId());
            r.setStatus(t.getStatus());
            r.setNamaSampah(t.getNamaSampah());
            r.setBeratStr(String.valueOf(t.getBerat()));
            items.add(new RiwayatItem(t.getTanggal(), r));
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

        // Jual rongsok → Masuk (Rupiah)
        NumberFormat rupiah = NumberFormat.getNumberInstance(new Locale("in", "ID"));
        for (JualRongsokModel r : rongsokList) {
            boolean menunggu = "menunggu_verifikasi".equals(r.getStatus());
            String judul = "Jual: " + r.getNamaBarang();
            if (menunggu) judul += " (Menunggu Konfirmasi)";
            // Simpan angka mentah untuk diformat adapter, atau "Menunggu" bila belum ada harga
            String poinStr;
            if (menunggu || r.getHargaDeal() == null) {
                poinStr = "Menunggu";
            } else {
                poinStr = String.valueOf(r.getHargaDeal());
            }
            Riwayat rv = new Riwayat(judul, formatTanggal(r.getCreatedAt()), poinStr, "Masuk");
            rv.setIsRupiah(true);
            items.add(new RiwayatItem(r.getCreatedAt(), rv));
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
            RiwayatAdapter adapter = new RiwayatAdapter(filtered);
            adapter.setOnLihatKodeListener(riwayat -> {
                Intent intent = new Intent(RiwayatActivity.this, TiketQrActivity.class);
                intent.putExtra("TIKET_ID",     riwayat.getTransactionId());
                intent.putExtra("TIKET_NAMA",   riwayat.getNamaSampah() != null ? riwayat.getNamaSampah() : "");
                intent.putExtra("TIKET_BERAT",  riwayat.getBeratStr()   != null ? riwayat.getBeratStr()   : "-");
                intent.putExtra("TIKET_POIN",   riwayat.getPoin());
                intent.putExtra("TIKET_METODE", "-");
                intent.putExtra("IS_REVIEW",    true);
                startActivityForResult(intent, REQUEST_VIEW_TIKET);
            });
            adapter.setOnBatalkanListener(this::batalkanTransaksi);
            rvRiwayat.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIEW_TIKET && resultCode == RESULT_OK) {
            loadRiwayat();
        }
    }

    private void batalkanTransaksi(Riwayat riwayat) {
        new AlertDialog.Builder(this)
                .setTitle("Batalkan Transaksi")
                .setMessage("Apakah kamu yakin ingin membatalkan transaksi ini?")
                .setPositiveButton("Ya, Batalkan", (d, w) -> kirimBatalkan(riwayat))
                .setNegativeButton("Tidak", null)
                .show();
    }

    private void kirimBatalkan(Riwayat riwayat) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("auth_access_token", "");

        SupabaseClient.getApiService()
                .cancelTransaksi(
                        "Bearer " + accessToken,
                        "eq." + riwayat.getTransactionId(),
                        new TransaksiCancelRequest())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RiwayatActivity.this,
                                    "Transaksi berhasil dibatalkan.", Toast.LENGTH_SHORT).show();
                            loadRiwayat();
                        } else {
                            Toast.makeText(RiwayatActivity.this,
                                    "Gagal membatalkan transaksi (kode " + response.code() + ")",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RiwayatActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
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
