package com.example.ecosort;

public class Reward {
    private String nama;
    private String poin;
    private String stok;
    private int gambarResId; // 👇 Langkah 1: Tambahkan variabel untuk menampung ID gambar hadiah

    // 👇 Langkah 2: Perbarui konstruktor agar mau menerima 4 data (ditambah int gambarResId)
    public Reward(String nama, String poin, String stok, int gambarResId) {
        this.nama = nama;
        this.poin = poin;
        this.stok = stok;
        this.gambarResId = gambarResId;
    }

    public String getNama() { return nama; }
    public String getPoin() { return poin; }
    public String getStok() { return stok; }

    // 👇 Langkah 3: Tambahkan fungsi Getter ini agar RewardAdapter bisa mengambil gambarnya
    public int getGambarResId() { return gambarResId; }
}