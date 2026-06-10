package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class LokasiTpsModel {
    @SerializedName("id")         private String id;
    @SerializedName("nama")       private String nama;
    @SerializedName("alamat")     private String alamat;
    @SerializedName("latitude")   private double latitude;
    @SerializedName("longitude")  private double longitude;
    @SerializedName("is_active")  private boolean isActive;
    @SerializedName("created_at") private String createdAt;

    public String getId()         { return id; }
    public String getNama()       { return nama; }
    public String getAlamat()     { return alamat; }
    public double getLatitude()   { return latitude; }
    public double getLongitude()  { return longitude; }
    public boolean isActive()     { return isActive; }
    public String getCreatedAt()  { return createdAt; }

    public boolean hasValidCoordinates() {
        return latitude != 0.0 && longitude != 0.0;
    }
}
