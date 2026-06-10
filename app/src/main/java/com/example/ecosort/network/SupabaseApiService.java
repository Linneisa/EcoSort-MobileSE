package com.example.ecosort.network;

import com.example.ecosort.model.JadwalModel;
import com.example.ecosort.model.LokasiTpsModel;
import com.example.ecosort.model.JualRongsokModel;
import com.example.ecosort.model.JualRongsokRequest;
import com.example.ecosort.model.JualRongsokUpdateRequest;
import com.example.ecosort.model.PenukaranRequest;
import com.example.ecosort.model.PenukaranRiwayatModel;
import com.example.ecosort.model.PoinPatchRequest;
import com.example.ecosort.model.TransaksiSampahModel;
import com.example.ecosort.model.TransaksiSampahRequest;
import com.example.ecosort.model.TransaksiCancelRequest;
import com.example.ecosort.model.TransaksiVerifikasiRequest;
import com.example.ecosort.model.ProfileModel;
import com.example.ecosort.model.ProfileRequest;
import com.example.ecosort.model.ProdukModel;
import com.example.ecosort.model.RewardModel;
import com.example.ecosort.model.StokUpdateRequest;
import com.example.ecosort.model.UserPointsModel;
import com.example.ecosort.model.UserPointsRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApiService {

    // ── LOKASI TPS ──────────────────────────────────────────
    // Data publik — hanya butuh apikey, tidak perlu Bearer token user
    @GET("lokasi_tps")
    Call<List<LokasiTpsModel>> getLokasiTps(
            @Query("is_active") String isActiveFilter,
            @Query("select") String select
    );

    // ── JADWAL ──────────────────────────────────────────────
    // Ambil semua jadwal. Contoh panggil: getJadwal("*", "tanggal.asc")
    @GET("jadwal")
    Call<List<JadwalModel>> getJadwal(
            @Query("select") String select,
            @Query("order") String order
    );

    // Tambah jadwal baru
    @Headers("Prefer: return=representation")
    @POST("jadwal")
    Call<List<JadwalModel>> tambahJadwal(@Body JadwalModel jadwal);

    // Hapus jadwal berdasarkan ID  (contoh: deleteJadwal("eq.5"))
    @DELETE("jadwal")
    Call<Void> deleteJadwal(@Query("id") String filterEq);

    // ── PRODUK ──────────────────────────────────────────────
    @GET("produk")
    Call<List<ProdukModel>> getProduk(
            @Query("select") String select,
            @Query("order") String order
    );

    // ── PROFIL ──────────────────────────────────────────────
    // Ambil profil user. Contoh: getProfile("Bearer <token>", "eq.<uuid>", "*")
    @GET("profiles")
    Call<List<ProfileModel>> getProfile(
            @Header("Authorization") String bearerToken,
            @Query("id") String userIdFilter,
            @Query("select") String select
    );

    // Upsert profil (insert jika belum ada, update jika sudah ada berdasarkan id)
    @POST("profiles")
    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    Call<List<ProfileModel>> upsertProfile(
            @Header("Authorization") String bearerToken,
            @Body ProfileRequest body
    );

    // ── REWARDS ─────────────────────────────────────────────
    // Ambil semua reward yang tersedia
    @GET("rewards")
    Call<List<RewardModel>> getRewards(
            @Query("select") String select,
            @Query("order") String order
    );

    // Simpan transaksi penukaran reward
    @POST("penukaran_reward")
    @Headers("Prefer: return=minimal")
    Call<Void> insertPenukaran(
            @Header("Authorization") String bearerToken,
            @Body PenukaranRequest body
    );

    // Kurangi stok reward setelah ditukar — contoh: updateStok("Bearer x", "eq.5", body)
    @PATCH("rewards")
    @Headers("Prefer: return=minimal")
    Call<Void> updateStok(
            @Header("Authorization") String bearerToken,
            @Query("id") String filterEq,
            @Body StokUpdateRequest body
    );

    // ── USER POINTS ─────────────────────────────────────────
    // Ambil poin user — filter: "eq.<uuid>"
    @GET("user_points")
    Call<List<UserPointsModel>> getUserPoints(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    // Buat record poin baru (dipanggil saat pertama kali login/register)
    @POST("user_points")
    @Headers("Prefer: return=minimal")
    Call<Void> insertUserPoints(
            @Header("Authorization") String bearerToken,
            @Body UserPointsRequest body
    );

    // ── TRANSAKSI SAMPAH ─────────────────────────────────────
    // Simpan transaksi — kembalikan record agar bisa ambil ID-nya
    @POST("transaksi_sampah")
    @Headers("Prefer: return=representation")
    Call<List<TransaksiSampahModel>> insertTransaksiSampah(
            @Header("Authorization") String bearerToken,
            @Body TransaksiSampahRequest body
    );

    // Ambil transaksi berdasarkan UUID (dipakai admin saat scan QR)
    @GET("transaksi_sampah")
    Call<List<TransaksiSampahModel>> getTransaksiById(
            @Header("Authorization") String bearerToken,
            @Query("id") String idFilter,
            @Query("select") String select
    );

    // ── JUAL RONGSOK ─────────────────────────────────────────
    // User ajukan jual rongsok — return=representation agar dapat UUID-nya
    @POST("jual_rongsok")
    @Headers("Prefer: return=representation")
    Call<List<JualRongsokModel>> insertJualRongsok(
            @Header("Authorization") String bearerToken,
            @Body JualRongsokRequest body
    );

    // User ambil riwayat jual rongsok miliknya (semua status)
    @GET("jual_rongsok")
    Call<List<JualRongsokModel>> getJualRongsokByUser(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    // Admin ambil semua pengajuan berstatus 'menunggu'
    @GET("jual_rongsok")
    Call<List<JualRongsokModel>> getJualRongsokMenunggu(
            @Header("Authorization") String bearerToken,
            @Query("status") String statusFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    // Admin update harga_deal dan set status='selesai'
    @PATCH("jual_rongsok")
    @Headers("Prefer: return=minimal")
    Call<Void> updateJualRongsok(
            @Header("Authorization") String bearerToken,
            @Query("id") String idFilter,
            @Body JualRongsokUpdateRequest body
    );

    // Ambil transaksi sampah milik user sejak tanggal tertentu (untuk hitung poin mingguan)
    @GET("transaksi_sampah")
    Call<List<TransaksiSampahModel>> getTransaksiByUserSince(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("tanggal") String tanggalFilter,
            @Query("select") String select
    );

    // Ambil semua transaksi sampah milik user (untuk Riwayat)
    @GET("transaksi_sampah")
    Call<List<TransaksiSampahModel>> getTransaksiByUser(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    // Ambil semua penukaran reward milik user + nama reward via join (untuk Riwayat)
    @GET("penukaran_reward")
    Call<List<PenukaranRiwayatModel>> getPenukaranByUser(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    // Admin update transaksi setelah verifikasi timbangan
    @PATCH("transaksi_sampah")
    @Headers("Prefer: return=minimal")
    Call<Void> updateTransaksiVerifikasi(
            @Header("Authorization") String bearerToken,
            @Query("id") String idFilter,
            @Body TransaksiVerifikasiRequest body
    );

    // User membatalkan transaksi yang masih menunggu verifikasi
    @PATCH("transaksi_sampah")
    @Headers("Prefer: return=minimal")
    Call<Void> cancelTransaksi(
            @Header("Authorization") String bearerToken,
            @Query("id") String idFilter,
            @Body TransaksiCancelRequest body
    );

    // Update total poin user
    @PATCH("user_points")
    @Headers("Prefer: return=minimal")
    Call<Void> updateUserPoints(
            @Header("Authorization") String bearerToken,
            @Query("user_id") String userIdFilter,
            @Body PoinPatchRequest body
    );
}
