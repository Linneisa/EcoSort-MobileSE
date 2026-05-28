package com.example.ecosort;

public class Produk {
    private String nama;
    private String harga;
    private String berat;
    private String kategori;
    private int gambarResId; // 👇 Langkah 1: Tambahkan variabel untuk menampung ID gambar

    // 👇 Langkah 2: Perbarui konstruktor agar mau menerima 5 data (ditambah int gambarResId)
    public Produk(String nama, String harga, String berat, String kategori, int gambarResId) {
        this.nama = nama;
        this.harga = harga;
        this.berat = berat;
        this.kategori = kategori;
        this.gambarResId = gambarResId;
    }

    public String getNama() {
        return nama;
    }

    public String getHarga() {
        return harga;
    }

    public String getBerat() {
        return berat;
    }

    public String getKategori() {
        return kategori;
    }

    // 👇 Langkah 3: Tambahkan fungsi Getter ini agar Adapter bisa mengambil gambarnya
    public int getGambarResId() {
        return gambarResId;
    }
}