package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class UserPointsModel {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("total_poin")
    private int totalPoin;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getId()       { return id; }
    public String getUserId()   { return userId; }
    public int    getTotalPoin(){ return totalPoin; }
    public String getUpdatedAt(){ return updatedAt; }
}
