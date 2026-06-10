package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.ecosort.model.TransaksiSampahModel;
import com.example.ecosort.network.SupabaseClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private ImageView ivFotoProfilHome;
    private TextView tvSelamatPagi;
    private TextView tvTotalPoin;
    private TextView tvPoinMingguIni;
    private CardView menuPeta, menuJadwal, menuMarketplace, menuReward;
    private CardView kartuPoin;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 1. Inisialisasi View
        ivFotoProfilHome = findViewById(R.id.ivFotoProfilHome);
        tvSelamatPagi    = findViewById(R.id.tvSelamatPagi);
        tvTotalPoin      = findViewById(R.id.tvTotalPoin);
        tvPoinMingguIni  = findViewById(R.id.tvPoinMingguIni);
        kartuPoin        = findViewById(R.id.kartuPoin);
        menuPeta = findViewById(R.id.menuPeta);
        menuJadwal = findViewById(R.id.menuJadwal);
        menuMarketplace = findViewById(R.id.menuMarketplace);
        menuReward = findViewById(R.id.menuReward);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // =======================================================
        // AKSI KLIK TOMBOL DAN KARTU FITUR
        // =======================================================

        // Klik Foto Profil (Pojok Kanan Atas) -> Tetap ke ProfilActivity
        if (ivFotoProfilHome != null) {
            ivFotoProfilHome.setOnClickListener(v -> {
                startActivity(new Intent(DashboardActivity.this, ProfilActivity.class));
            });
        }

        // Klik Kartu Poin Besar Atas -> Menuju ke RiwayatActivity
        if (kartuPoin != null) {
            kartuPoin.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(DashboardActivity.this, RiwayatActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka halaman Riwayat", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Klik Menu Peta TPS -> TpsActivity
        if (menuPeta != null) {
            menuPeta.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(DashboardActivity.this, TpsActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka Peta TPS", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Klik Menu Jadwal Pengambilan -> JadwalActivity
        if (menuJadwal != null) {
            menuJadwal.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(DashboardActivity.this, JadwalActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka Jadwal", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Klik Menu Marketplace -> MarketplaceActivity
        if (menuMarketplace != null) {
            menuMarketplace.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(DashboardActivity.this, MarketplaceActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka Marketplace", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Klik Menu Reward Kecil -> RewardActivity
        if (menuReward != null) {
            menuReward.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(DashboardActivity.this, RewardActivity.class));
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal membuka halaman Reward", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // =======================================================
        // BOTTOM NAVIGATION BAR (NAVIGASI MENU BAWAH)
        // =======================================================
        if (bottomNavigation != null) {
            Menu menu = bottomNavigation.getMenu();
            if (menu != null && menu.size() > 0) {
                menu.getItem(0).setChecked(true);
            }

            bottomNavigation.setOnItemSelectedListener(item -> {
                String title = item.getTitle().toString().toLowerCase();

                if (title.contains("home")) {
                    return true;
                } else if (title.contains("riwayat")) {
                    try {
                        startActivity(new Intent(DashboardActivity.this, RiwayatActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(this, "Halaman Riwayat belum siap", Toast.LENGTH_SHORT).show();
                    }
                } else if (title.contains("cs") || title.contains("profil") || title.contains("service")) {
                    // SEKARANG DIAKAN DIALIKHAN KE HALAMAN CS ACTIVITY INTERNAL, TIDAK LANGSUNG KE WA
                    try {
                        startActivity(new Intent(DashboardActivity.this, CsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    } catch (Exception e) {
                        Toast.makeText(this, "Halaman CS belum siap", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bottomNavigation != null) {
            Menu menu = bottomNavigation.getMenu();
            if (menu != null && menu.size() > 0) {
                menu.getItem(0).setChecked(true); // Memastikan menu Home tetap aktif secara visual
            }
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String namaUser = prefs.getString("nama", "Bintang");
        if (tvSelamatPagi != null) {
            int jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String sapaan;
            if (jam < 12) {
                sapaan = "Selamat pagi 👋";
            } else if (jam < 15) {
                sapaan = "Selamat siang ☀️";
            } else if (jam < 18) {
                sapaan = "Selamat sore 🌤️";
            } else {
                sapaan = "Selamat malam 🌙";
            }
            tvSelamatPagi.setText(sapaan + ", " + namaUser);
        }

        // Tampilkan poin dari cache dulu, lalu refresh dari Supabase
        if (tvTotalPoin != null) {
            tvTotalPoin.setText(formatPoin(UserPointsHelper.getCachedPoin(this)));
            UserPointsHelper.fetchPoin(this, new UserPointsHelper.PoinCallback() {
                @Override
                public void onSuccess(int totalPoin) {
                    tvTotalPoin.setText(formatPoin(totalPoin));
                }
                @Override
                public void onFailure(String pesan) { /* tampilkan cache */ }
            });
        }

        loadPoinMingguIni();

        String uriString = prefs.getString("foto_uri", null);
        if (uriString != null && ivFotoProfilHome != null) {
            try {
                ivFotoProfilHome.setImageURI(Uri.parse(uriString));
            } catch (Exception e) {
                ivFotoProfilHome.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        } else if (ivFotoProfilHome != null) {
            ivFotoProfilHome.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }

    private void loadPoinMingguIni() {
        if (tvPoinMingguIni == null) return;
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId      = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");
        if (userId.isEmpty() || accessToken.isEmpty()) return;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String tanggalMin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        SupabaseClient.getApiService()
                .getTransaksiByUserSince(
                        "Bearer " + accessToken,
                        "eq." + userId,
                        "gte." + tanggalMin,
                        "poin_didapat,status")
                .enqueue(new Callback<List<TransaksiSampahModel>>() {
                    @Override
                    public void onResponse(Call<List<TransaksiSampahModel>> call,
                                           Response<List<TransaksiSampahModel>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;
                        int total = 0;
                        for (TransaksiSampahModel t : response.body()) {
                            // Hanya hitung transaksi yang sudah diverifikasi (bukan pending/dibatalkan)
                            String s = t.getStatus();
                            if (!"menunggu".equals(s) && !"dibatalkan".equals(s)) {
                                total += t.getPoinDidapat();
                            }
                        }
                        final int totalPoin = total;
                        runOnUiThread(() -> {
                            if (tvPoinMingguIni == null) return;
                            if (totalPoin == 0) {
                                tvPoinMingguIni.setText("Belum ada transaksi minggu ini");
                            } else {
                                tvPoinMingguIni.setText("+" + totalPoin + " poin minggu ini");
                            }
                        });
                    }
                    @Override
                    public void onFailure(Call<List<TransaksiSampahModel>> call, Throwable t) { }
                });
    }

    private String formatPoin(int poin) {
        return NumberFormat.getNumberInstance(new Locale("in", "ID")).format(poin);
    }
}