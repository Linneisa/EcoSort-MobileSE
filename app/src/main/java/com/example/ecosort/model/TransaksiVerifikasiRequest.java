package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class TransaksiVerifikasiRequest {

    @SerializedName("berat_asli")
    private double beratAsli;

    @SerializedName("poin_didapat")
    private int poinDidapat;

    @SerializedName("status")
    private String status;

    public TransaksiVerifikasiRequest(double beratAsli, int poinDidapat) {
        this.beratAsli   = beratAsli;
        this.poinDidapat = poinDidapat;
        this.status      = "terverifikasi";
    }
}
