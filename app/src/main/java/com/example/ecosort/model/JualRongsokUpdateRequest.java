package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class JualRongsokUpdateRequest {

    @SerializedName("harga_deal")  private final long   hargaDeal;
    @SerializedName("status")      private final String status      = "selesai";
    @SerializedName("verified_at") private final String verifiedAt;
    @SerializedName("verified_by") private final String verifiedBy;

    public JualRongsokUpdateRequest(long hargaDeal, String verifiedBy) {
        this.hargaDeal  = hargaDeal;
        this.verifiedBy = verifiedBy;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.verifiedAt = sdf.format(new Date());
    }
}
