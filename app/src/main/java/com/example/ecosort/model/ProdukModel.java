package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

// Model yang memetakan kolom tabel "produk" di Supabase.
public class ProdukModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nama")
    private String nama;

    @SerializedName("harga")
    private String harga;

    @SerializedName("berat")
    private String berat;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("gambar_url")
    private String gambarUrl;

    public int getId()           { return id; }
    public String getNama()      { return nama; }
    public String getHarga()     { return harga; }
    public String getBerat()     { return berat; }
    public String getKategori()  { return kategori; }
    public String getGambarUrl() { return gambarUrl; }
}
