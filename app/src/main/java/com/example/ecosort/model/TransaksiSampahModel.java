package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class TransaksiSampahModel {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("nama_sampah")
    private String namaSampah;

    @SerializedName("berat")
    private double berat;

    @SerializedName("berat_asli")
    private Double beratAsli;

    @SerializedName("poin_didapat")
    private int poinDidapat;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("status")
    private String status;

    public String  getId()          { return id; }
    public String  getUserId()      { return userId; }
    public String  getNamaSampah()  { return namaSampah; }
    public double  getBerat()       { return berat; }
    public Double  getBeratAsli()   { return beratAsli; }
    public int     getPoinDidapat() { return poinDidapat; }
    public String  getTanggal()     { return tanggal; }
    public String  getStatus()      { return status; }
}
