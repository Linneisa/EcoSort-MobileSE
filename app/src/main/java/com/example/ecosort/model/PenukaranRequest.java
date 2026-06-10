package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PenukaranRequest {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("reward_id")
    private int rewardId;

    @SerializedName("jumlah_poin")
    private int jumlahPoin;

    @SerializedName("status")
    private String status;

    @SerializedName("tanggal")
    private String tanggal;

    public PenukaranRequest(String userId, int rewardId, int jumlahPoin) {
        this.userId    = userId;
        this.rewardId  = rewardId;
        this.jumlahPoin = jumlahPoin;
        this.status    = "sukses";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.tanggal = sdf.format(new Date());
    }
}
