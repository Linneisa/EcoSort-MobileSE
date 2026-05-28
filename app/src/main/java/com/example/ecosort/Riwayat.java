package com.example.ecosort;

public class Riwayat {
    private String judul;
    private String tanggal;
    private String poin; // Cukup angkanya saja, misal: "500"
    private String jenis; // Hanya diisi "Masuk" atau "Keluar"

    public Riwayat(String judul, String tanggal, String poin, String jenis) {
        this.judul = judul;
        this.tanggal = tanggal;
        this.poin = poin;
        this.jenis = jenis;
    }

    public String getJudul() { return judul; }
    public String getTanggal() { return tanggal; }
    public String getPoin() { return poin; }
    public String getJenis() { return jenis; }
}