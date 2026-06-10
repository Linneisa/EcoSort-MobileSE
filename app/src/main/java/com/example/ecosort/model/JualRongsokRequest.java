package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class JualRongsokRequest {

    @SerializedName("user_id")      private final String userId;
    @SerializedName("nama_penjual") private final String namaPenjual;
    @SerializedName("nama_barang")  private final String namaBarang;
    @SerializedName("lokasi")       private final String lokasi;
    @SerializedName("harga_deal")   private final long   hargaDeal = 0L;
    @SerializedName("status")       private final String status    = "menunggu_verifikasi";

    public JualRongsokRequest(String userId, String namaPenjual,
                               String namaBarang, String lokasi) {
        this.userId      = userId;
        this.namaPenjual = namaPenjual;
        this.namaBarang  = namaBarang;
        this.lokasi      = lokasi;
    }
}
