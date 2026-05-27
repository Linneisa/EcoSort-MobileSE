package com.example.ecosort;

public class Reward {
    private String nama;
    private String poin;
    private String stok;

    public Reward(String nama, String poin, String stok) {
        this.nama = nama;
        this.poin = poin;
        this.stok = stok;
    }

    public String getNama() { return nama; }
    public String getPoin() { return poin; }
    public String getStok() { return stok; }
}