package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class PenukaranRiwayatModel {

    @SerializedName("id")          private int    id;
    @SerializedName("user_id")     private String userId;
    @SerializedName("reward_id")   private int    rewardId;
    @SerializedName("jumlah_poin") private int    jumlahPoin;
    @SerializedName("status")      private String status;
    @SerializedName("tanggal")     private String tanggal;
    @SerializedName("rewards")     private NamaReward rewards;

    public int    getId()        { return id; }
    public String getUserId()    { return userId; }
    public int    getRewardId()  { return rewardId; }
    public int    getJumlahPoin(){ return jumlahPoin; }
    public String getStatus()    { return status; }
    public String getTanggal()   { return tanggal; }

    public String getNamaReward() {
        return (rewards != null && rewards.nama != null)
                ? rewards.nama
                : "Reward #" + rewardId;
    }

    public static class NamaReward {
        @SerializedName("nama") String nama;
    }
}
