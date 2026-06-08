package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.PenukaranRequest;
import com.example.ecosort.model.RewardModel;
import com.example.ecosort.model.StokUpdateRequest;
import com.example.ecosort.network.SupabaseClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardActivity extends AppCompatActivity implements RewardAdapter.OnTukarClickListener {

    private RecyclerView  rvReward;
    private RewardAdapter adapter;
    private String        userId;
    private String        bearerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        // Ambil sesi dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = prefs.getString("user_id", "");
        String accessToken = prefs.getString("auth_access_token", "");
        bearerToken = "Bearer " + accessToken;

        // Tombol back
        ImageView btnBackReward = findViewById(R.id.btnBackReward);
        if (btnBackReward != null) {
            btnBackReward.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        // Tombol profil
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v ->
                    startActivity(new Intent(RewardActivity.this, ProfilActivity.class)));
        }

        // Foto profil header
        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            String fotoPath = prefs.getString("foto_profil_path", null);
            if (fotoPath != null) {
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }

        // RecyclerView
        rvReward = findViewById(R.id.rvReward);
        rvReward.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new RewardAdapter(this);
        rvReward.setAdapter(adapter);

        fetchRewards();
    }

    private void fetchRewards() {
        SupabaseClient.getApiService()
                .getRewards("*", "harga_poin.asc")
                .enqueue(new Callback<List<RewardModel>>() {

                    @Override
                    public void onResponse(Call<List<RewardModel>> call,
                                           Response<List<RewardModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.setData(response.body());
                        } else {
                            Toast.makeText(RewardActivity.this,
                                    "Gagal memuat reward (kode " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RewardModel>> call, Throwable t) {
                        Toast.makeText(RewardActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTukar(RewardModel reward, int position) {
        if (userId.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dialog konfirmasi penukaran
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Penukaran")
                .setMessage("Tukar \"" + reward.getNama() + "\" dengan "
                        + formatPoin(reward.getHargaPoin()) + "?")
                .setPositiveButton("Tukar", (dialog, which) ->
                        prosesTukar(reward, position))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void prosesTukar(RewardModel reward, int position) {
        PenukaranRequest penukaranReq = new PenukaranRequest(
                userId, reward.getId(), reward.getHargaPoin());

        // Langkah 1: catat transaksi penukaran
        SupabaseClient.getApiService()
                .insertPenukaran(bearerToken, penukaranReq)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Langkah 2: kurangi stok
                            int stokBaru = reward.getStok() - 1;
                            kurangiStok(reward, position, stokBaru);
                        } else {
                            Toast.makeText(RewardActivity.this,
                                    "Penukaran gagal (kode " + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RewardActivity.this,
                                "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void kurangiStok(RewardModel reward, int position, int stokBaru) {
        StokUpdateRequest stokReq = new StokUpdateRequest(stokBaru);

        SupabaseClient.getApiService()
                .updateStok(bearerToken, "eq." + reward.getId(), stokReq)
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Update stok UI lokal
                            adapter.updateStokAtPosition(position, stokBaru);
                            // Kurangi poin user
                            UserPointsHelper.kurangiPoin(
                                    RewardActivity.this,
                                    reward.getHargaPoin(),
                                    null // tidak perlu callback di sini
                            );
                            Toast.makeText(RewardActivity.this,
                                    "Penukaran berhasil! Stok tersisa: " + stokBaru,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Transaksi sudah tercatat, tapi stok gagal dikurangi — tetap beri tahu user
                            Toast.makeText(RewardActivity.this,
                                    "Penukaran tercatat, stok belum terupdate.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RewardActivity.this,
                                "Penukaran tercatat, namun gagal update stok.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String formatPoin(int poin) {
        if (poin >= 1000) {
            return String.format("%,d pts", poin).replace(',', '.');
        }
        return poin + " pts";
    }
}
