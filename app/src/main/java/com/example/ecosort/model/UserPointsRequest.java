package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UserPointsRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("total_poin")
    private int totalPoin;

    @SerializedName("updated_at")
    private String updatedAt;

    public UserPointsRequest(String userId, int totalPoin) {
        this.userId    = userId;
        this.totalPoin = totalPoin;
        this.updatedAt = nowUtc();
    }

    private static String nowUtc() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }
}
