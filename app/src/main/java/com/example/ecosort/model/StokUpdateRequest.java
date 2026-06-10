package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class StokUpdateRequest {

    @SerializedName("stok")
    private int stok;

    public StokUpdateRequest(int stok) {
        this.stok = stok;
    }
}
