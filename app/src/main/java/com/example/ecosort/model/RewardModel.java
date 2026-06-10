package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class RewardModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("harga_poin")
    private int hargaPoin;

    @SerializedName("stok")
    private int stok;

    @SerializedName("kategori")
    private String kategori;

    public int    getId()        { return id; }
    public String getNama()      { return nama; }
    public String getDeskripsi() { return deskripsi; }
    public int    getHargaPoin() { return hargaPoin; }
    public int    getStok()      { return stok; }
    public String getKategori()  { return kategori; }

    // Setter stok untuk update lokal di adapter setelah tukar berhasil
    public void setStok(int stok) { this.stok = stok; }
}
