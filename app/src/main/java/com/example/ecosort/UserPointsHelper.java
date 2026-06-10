package com.example.ecosort;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.ecosort.model.PoinPatchRequest;
import com.example.ecosort.model.UserPointsModel;
import com.example.ecosort.model.UserPointsRequest;
import com.example.ecosort.network.SupabaseClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPointsHelper {

    private static final String PREFS_NAME  = "UserPrefs";
    private static final String KEY_POIN    = "total_poin";

    public interface PoinCallback {
        void onSuccess(int totalPoin);
        void onFailure(String pesan);
    }

    /** Ambil total poin dari cache SharedPreferences (tidak perlu network). */
    public static int getCachedPoin(Context context) {
        return prefs(context).getInt(KEY_POIN, 0);
    }

    /**
     * Dipanggil setelah login atau register.
     * Cek apakah record user_points sudah ada:
     *   - Sudah ada  → simpan ke cache
     *   - Belum ada  → buat record baru dengan total_poin = 0
     */
    public static void initPoin(Context context) {
        SharedPreferences sp    = prefs(context);
        String userId           = sp.getString("user_id", "");
        String accessToken      = sp.getString("auth_access_token", "");
        if (userId.isEmpty() || accessToken.isEmpty()) return;

        String bearer = "Bearer " + accessToken;

        SupabaseClient.getApiService()
                .getUserPoints(bearer, "eq." + userId, "total_poin")
                .enqueue(new Callback<List<UserPointsModel>>() {

                    @Override
                    public void onResponse(Call<List<UserPointsModel>> call,
                                           Response<List<UserPointsModel>> response) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        if (response.body().isEmpty()) {
                            // Belum ada record — buat baru dengan 0 poin
                            buatRecordAwal(context, userId, bearer);
                        } else {
                            // Sudah ada — simpan ke cache
                            int poin = response.body().get(0).getTotalPoin();
                            prefs(context).edit().putInt(KEY_POIN, poin).apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserPointsModel>> call, Throwable t) {
                        // Gagal silent — tampilkan cache yang sudah ada
                    }
                });
    }

    /**
     * Ambil poin terbaru dari Supabase dan perbarui cache.
     * Cocok dipanggil saat halaman dibuka (setelah menampilkan cache dulu).
     */
    public static void fetchPoin(Context context, PoinCallback callback) {
        SharedPreferences sp = prefs(context);
        String userId        = sp.getString("user_id", "");
        String accessToken   = sp.getString("auth_access_token", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            if (callback != null) callback.onFailure("Belum login");
            return;
        }

        SupabaseClient.getApiService()
                .getUserPoints("Bearer " + accessToken, "eq." + userId, "total_poin")
                .enqueue(new Callback<List<UserPointsModel>>() {

                    @Override
                    public void onResponse(Call<List<UserPointsModel>> call,
                                           Response<List<UserPointsModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (!response.body().isEmpty()) {
                                int poin = response.body().get(0).getTotalPoin();
                                prefs(context).edit().putInt(KEY_POIN, poin).apply();
                                if (callback != null) callback.onSuccess(poin);
                            } else {
                                // Record belum ada — buat baru dengan 0 poin (sama seperti initPoin)
                                buatRecordAwal(context, userId, "Bearer " + accessToken);
                                prefs(context).edit().putInt(KEY_POIN, 0).apply();
                                if (callback != null) callback.onSuccess(0);
                            }
                        } else {
                            if (callback != null) callback.onFailure("Data tidak ditemukan");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserPointsModel>> call, Throwable t) {
                        if (callback != null) callback.onFailure(t.getMessage());
                    }
                });
    }

    /** Tambah poin user sebesar {@code jumlah}. */
    public static void addPoin(Context context, int jumlah, PoinCallback callback) {
        int sekarang = getCachedPoin(context);
        patchPoin(context, sekarang + jumlah, callback);
    }

    /**
     * Tambah poin ke user LAIN berdasarkan userId-nya (dipakai admin saat verifikasi).
     * Tidak menyentuh SharedPreferences — hanya update Supabase.
     *
     * @param targetUserId  user_id pemilik transaksi
     * @param bearerToken   token admin ("Bearer ...")
     * @param jumlah        poin yang ditambahkan
     */
    public static void addPoinToUser(String targetUserId, String bearerToken,
                                     int jumlah, PoinCallback callback) {
        SupabaseClient.getApiService()
                .getUserPoints(bearerToken, "eq." + targetUserId, "total_poin")
                .enqueue(new Callback<List<UserPointsModel>>() {

                    @Override
                    public void onResponse(Call<List<UserPointsModel>> call,
                                           Response<List<UserPointsModel>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            if (callback != null) callback.onFailure("Gagal fetch poin user");
                            return;
                        }

                        if (response.body().isEmpty()) {
                            // User belum punya record — buat baru langsung dengan jumlah poin
                            SupabaseClient.getApiService()
                                    .insertUserPoints(bearerToken, new UserPointsRequest(targetUserId, jumlah))
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> c, Response<Void> r) {
                                            if (r.isSuccessful()) {
                                                if (callback != null) callback.onSuccess(jumlah);
                                            } else {
                                                if (callback != null) callback.onFailure("Gagal buat record poin");
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Void> c, Throwable t) {
                                            if (callback != null) callback.onFailure(t.getMessage());
                                        }
                                    });
                        } else {
                            int newTotal = response.body().get(0).getTotalPoin() + jumlah;
                            SupabaseClient.getApiService()
                                    .updateUserPoints(bearerToken, "eq." + targetUserId,
                                            new PoinPatchRequest(newTotal))
                                    .enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> c, Response<Void> r) {
                                            if (r.isSuccessful()) {
                                                if (callback != null) callback.onSuccess(newTotal);
                                            } else {
                                                if (callback != null) callback.onFailure("Gagal update poin: " + r.code());
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<Void> c, Throwable t) {
                                            if (callback != null) callback.onFailure(t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserPointsModel>> call, Throwable t) {
                        if (callback != null) callback.onFailure(t.getMessage());
                    }
                });
    }

    /** Kurangi poin user sebesar {@code jumlah} (tidak bisa negatif). */
    public static void kurangiPoin(Context context, int jumlah, PoinCallback callback) {
        int sekarang = getCachedPoin(context);
        int baru     = Math.max(0, sekarang - jumlah);
        patchPoin(context, baru, callback);
    }

    /**
     * Kurangi poin dengan validasi kecukupan dari server (bukan cache).
     * Callback.onFailure dipanggil dengan pesan "INSUFFICIENT" jika poin tidak cukup.
     */
    public static void kurangiPoinValidated(Context context, int jumlah, PoinCallback callback) {
        fetchPoin(context, new PoinCallback() {
            @Override
            public void onSuccess(int currentPoin) {
                if (currentPoin < jumlah) {
                    if (callback != null) callback.onFailure("INSUFFICIENT");
                    return;
                }
                patchPoin(context, currentPoin - jumlah, callback);
            }
            @Override
            public void onFailure(String pesan) {
                if (callback != null) callback.onFailure(pesan);
            }
        });
    }

    // ── Private helpers ─────────────────────────────────────

    private static void buatRecordAwal(Context context, String userId, String bearer) {
        SupabaseClient.getApiService()
                .insertUserPoints(bearer, new UserPointsRequest(userId, 0))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        // Cache sudah 0 (default), tidak perlu update
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) { }
                });
    }

    private static void patchPoin(Context context, int newPoin, PoinCallback callback) {
        SharedPreferences sp = prefs(context);
        String userId        = sp.getString("user_id", "");
        String accessToken   = sp.getString("auth_access_token", "");

        if (userId.isEmpty() || accessToken.isEmpty()) {
            if (callback != null) callback.onFailure("Belum login");
            return;
        }

        SupabaseClient.getApiService()
                .updateUserPoints("Bearer " + accessToken, "eq." + userId,
                        new PoinPatchRequest(newPoin))
                .enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            prefs(context).edit().putInt(KEY_POIN, newPoin).apply();
                            if (callback != null) callback.onSuccess(newPoin);
                        } else {
                            if (callback != null) callback.onFailure("Gagal update (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (callback != null) callback.onFailure(t.getMessage());
                    }
                });
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
