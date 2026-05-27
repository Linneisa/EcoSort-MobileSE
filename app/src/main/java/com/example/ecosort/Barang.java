package com.example.ecosort;

public class Barang {
    private int fotoBarang;
    private String namaBarang;
    private String hargaBarang;

    public Barang(int fotoBarang, String namaBarang, String hargaBarang) {
        this.fotoBarang = fotoBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
    }


    public int getFotoBarang() {
        return fotoBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getHargaBarang() {
        return hargaBarang;
    }
}