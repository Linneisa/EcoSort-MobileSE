package com.example.ecosort;

public class Jadwal {
    private String tanggal;
    private String hari;
    private String jenisSampah;
    private String jam;
    private String status;

    public Jadwal(String tanggal, String hari, String jenisSampah, String jam, String status) {
        this.tanggal = tanggal;
        this.hari = hari;
        this.jenisSampah = jenisSampah;
        this.jam = jam;
        this.status = status;
    }

    public String getTanggal() { return tanggal; }
    public String getHari() { return hari; }
    public String getJenisSampah() { return jenisSampah; }
    public String getJam() { return jam; }
    public String getStatus() { return status; }
}