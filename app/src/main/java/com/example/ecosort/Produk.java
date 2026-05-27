package com.example.ecosort;

public class Produk {
    private String nama;
    private String harga;
    private String berat;
    private String kategori;

    public Produk(String nama, String harga, String berat, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.berat = berat;
        this.kategori = kategori;
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
}