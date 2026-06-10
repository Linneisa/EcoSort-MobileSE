package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PoinPatchRequest {

    @SerializedName("total_poin")
    private int totalPoin;

    @SerializedName("updated_at")
    private String updatedAt;

    public PoinPatchRequest(int totalPoin) {
        this.totalPoin = totalPoin;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.updatedAt = sdf.format(new Date());
    }
}
