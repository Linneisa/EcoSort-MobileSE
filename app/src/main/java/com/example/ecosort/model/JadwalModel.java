package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

// Model yang memetakan kolom tabel "jadwal" di Supabase.
// Nama field di sini harus SAMA dengan nama kolom di database.
public class JadwalModel {

    @SerializedName("id")
    private int id;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("hari")
    private String hari;

    @SerializedName("jenis_sampah")   // kolom DB pakai snake_case
    private String jenisSampah;

    @SerializedName("jam")
    private String jam;

    @SerializedName("status")
    private String status;

    public int getId()           { return id; }
    public String getTanggal()   { return tanggal; }
    public String getHari()      { return hari; }
    public String getJenisSampah() { return jenisSampah; }
    public String getJam()       { return jam; }
    public String getStatus()    { return status; }
}
