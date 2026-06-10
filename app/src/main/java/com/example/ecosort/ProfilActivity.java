package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.ProfileModel;
import com.example.ecosort.network.SupabaseClient;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        findViewById(R.id.btnBackProfil).setOnClickListener(v -> finish());
        findViewById(R.id.btnEditProfilHeader).setOnClickListener(v ->
                startActivity(new Intent(ProfilActivity.this, UbahProfilActivity.class)));
        findViewById(R.id.btnBantuan).setOnClickListener(v -> {
            startActivity(new Intent(ProfilActivity.this, CsActivity.class));
        });
        // Logika Tombol Keluar Akun
        androidx.cardview.widget.CardView btnLogout = findViewById(R.id.menuLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // 1. Bersihkan memori sesi (SharedPreferences)
                android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();

                // Hapus semua data sesi agar tidak bocor ke akun berikutnya
                editor.clear();
                editor.apply();

                Toast.makeText(ProfilActivity.this, "Berhasil Keluar Akun", Toast.LENGTH_SHORT).show();

                // 2. Lempar kembali ke LoginActivity
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);

                // 3. FLAG PENTING: Bersihkan riwayat halaman agar tidak bisa di-back
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Membuka memori (SharedPreferences)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView tvNama = findViewById(R.id.tvNamaProfil);
        TextView tvEmail = findViewById(R.id.tvEmailProfil);
        ImageView ivFoto = findViewById(R.id.ivFotoProfilHeader);
        View layoutTombolAdmin = findViewById(R.id.layoutTombolAdmin);

        // Ambil role pengguna saat ini
        String rolePengguna = prefs.getString("ROLE", "User");

        if (rolePengguna.equals("Admin")) {
            // TAMPILAN KHUSUS ADMIN
            tvNama.setText("Admin EcoSort");
            tvEmail.setText("admin@ecosort.com");

            if (ivFoto != null) {
                ivFoto.setImageResource(android.R.drawable.ic_secure);
            }

            // Munculkan grup tombol admin
            if (layoutTombolAdmin != null) {
                layoutTombolAdmin.setVisibility(View.VISIBLE);

                View btnMenuAdmin = findViewById(R.id.btnMenuAdmin);
                if (btnMenuAdmin != null) {
                    btnMenuAdmin.setOnClickListener(v ->
                            startActivity(new Intent(ProfilActivity.this, AdminScanActivity.class)));
                }

                View btnMenuRongsok = findViewById(R.id.btnMenuRongsok);
                if (btnMenuRongsok != null) {
                    btnMenuRongsok.setOnClickListener(v ->
                            startActivity(new Intent(ProfilActivity.this, AdminRongsokanActivity.class)));
                }
            }

        } else {
            // TAMPILAN USER BIASA
            tvNama.setText(prefs.getString("nama", "Pengguna EcoSort"));
            tvEmail.setText(prefs.getString("email", "-"));

            // Tampilkan HP dari cache lokal terlebih dahulu
            TextView tvNomorHP = findViewById(R.id.tvNomorHP);
            if (tvNomorHP != null) {
                String cachedHp = prefs.getString("hp", "");
                tvNomorHP.setText(cachedHp.isEmpty() ? "-" : cachedHp);
            }

            // Tampilkan poin dari cache, lalu refresh dari Supabase
            TextView tvPoinProfil = findViewById(R.id.tvPoinProfil);
            if (tvPoinProfil != null) {
                tvPoinProfil.setText(formatPoin(UserPointsHelper.getCachedPoin(this)) + " pts");
                UserPointsHelper.fetchPoin(this, new UserPointsHelper.PoinCallback() {
                    @Override
                    public void onSuccess(int totalPoin) {
                        tvPoinProfil.setText(formatPoin(totalPoin) + " pts");
                    }
                    @Override
                    public void onFailure(String pesan) { /* tampilkan cache */ }
                });
            }

            // Fetch HP dari Supabase (cache-then-network)
            fetchProfileDariSupabase(prefs, tvNomorHP);

            // Load foto profil user jika ada
            String uriString = prefs.getString("foto_profil_path", null);
            if (uriString != null && ivFoto != null) {
                ivFoto.setImageURI(Uri.parse(uriString));
            } else if (ivFoto != null) {
                ivFoto.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            // Sembunyikan grup tombol admin
            if (layoutTombolAdmin != null) {
                layoutTombolAdmin.setVisibility(View.GONE);
            }
        }
    }

    private String formatPoin(int poin) {
        return NumberFormat.getNumberInstance(new Locale("in", "ID")).format(poin);
    }

    private void fetchProfileDariSupabase(SharedPreferences prefs, TextView tvNomorHP) {
        String userId      = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");

        if (userId.isEmpty() || accessToken.isEmpty()) return;

        String bearerToken = "Bearer " + accessToken;

        SupabaseClient.getApiService()
                .getProfile(bearerToken, "eq." + userId, "phone,full_name,email")
                .enqueue(new Callback<List<ProfileModel>>() {

                    @Override
                    public void onResponse(Call<List<ProfileModel>> call,
                                           Response<List<ProfileModel>> response) {
                        if (!response.isSuccessful()
                                || response.body() == null
                                || response.body().isEmpty()) return;

                        ProfileModel profile = response.body().get(0);

                        // Update SharedPreferences (perbarui cache)
                        SharedPreferences.Editor editor = prefs.edit();

                        if (profile.getPhone() != null) {
                            editor.putString("hp", profile.getPhone());
                            if (tvNomorHP != null) {
                                tvNomorHP.setText(profile.getPhone().isEmpty()
                                        ? "-" : profile.getPhone());
                            }
                        }
                        if (profile.getFullName() != null && !profile.getFullName().isEmpty()) {
                            editor.putString("nama", profile.getFullName());
                            TextView tvNama = findViewById(R.id.tvNamaProfil);
                            if (tvNama != null) tvNama.setText(profile.getFullName());
                        }

                        editor.apply();
                    }

                    @Override
                    public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                        // Gagal fetch — UI tetap menampilkan data cache, tidak perlu error toast
                    }
                });
    }
}