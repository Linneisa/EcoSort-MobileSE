package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TransaksiSampahRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("nama_sampah")
    private String namaSampah;

    @SerializedName("berat")
    private double berat;

    @SerializedName("poin_didapat")
    private int poinDidapat;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("status")
    private String status;

    public TransaksiSampahRequest(String userId, String namaSampah, double berat, int poinDidapat) {
        this.userId      = userId;
        this.namaSampah  = namaSampah;
        this.berat       = berat;
        this.poinDidapat = poinDidapat;
        this.status      = "menunggu";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.tanggal = sdf.format(new Date());
    }
}
