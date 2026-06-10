package com.example.ecosort.model;

import com.google.gson.annotations.SerializedName;

public class JualRongsokModel {

    @SerializedName("id")           private String id;
    @SerializedName("user_id")      private String userId;
    @SerializedName("nama_penjual") private String namaPenjual;
    @SerializedName("nama_barang")  private String namaBarang;
    @SerializedName("lokasi")       private String lokasi;
    @SerializedName("harga_deal")   private Long   hargaDeal;
    @SerializedName("status")       private String status;
    @SerializedName("created_at")   private String createdAt;
    @SerializedName("verified_at")  private String verifiedAt;
    @SerializedName("verified_by")  private String verifiedBy;
    @SerializedName("berat_kg")     private Double beratKg;

    public String getId()          { return id; }
    public String getUserId()      { return userId; }
    public String getNamaPenjual() { return namaPenjual; }
    public String getNamaBarang()  { return namaBarang; }
    public String getLokasi()      { return lokasi; }
    public Long   getHargaDeal()   { return hargaDeal; }
    public String getStatus()      { return status; }
    public String getCreatedAt()   { return createdAt; }
    public String getVerifiedAt()  { return verifiedAt; }
    public String getVerifiedBy()  { return verifiedBy; }
    public Double getBeratKg()     { return beratKg; }
}
