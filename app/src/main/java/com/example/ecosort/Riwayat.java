package com.example.ecosort;

public class Riwayat {
    private String judul;
    private String tanggal;
    private String poin; // Cukup angkanya saja, misal: "500"
    private String jenis; // Hanya diisi "Masuk" atau "Keluar"

    // Hanya diisi untuk transaksi sampah — null untuk penukaran reward
    private String  transactionId;
    private String  status;
    private String  namaSampah;
    private String  beratStr;

    // True untuk entri jual rongsok — tampilkan poin sebagai Rupiah, bukan "+X pts"
    private boolean isRupiah;

    public Riwayat(String judul, String tanggal, String poin, String jenis) {
        this.judul = judul;
        this.tanggal = tanggal;
        this.poin = poin;
        this.jenis = jenis;
    }

    public String getJudul()   { return judul; }
    public String getTanggal() { return tanggal; }
    public String getPoin()    { return poin; }
    public String getJenis()   { return jenis; }

    public void   setTransactionId(String id) { transactionId = id; }
    public String getTransactionId()          { return transactionId; }
    public void   setStatus(String s)         { status = s; }
    public String getStatus()                 { return status; }
    public void   setNamaSampah(String n)     { namaSampah = n; }
    public String getNamaSampah()             { return namaSampah; }
    public void    setBeratStr(String b)       { beratStr = b; }
    public String  getBeratStr()               { return beratStr; }
    public void    setIsRupiah(boolean v)      { isRupiah = v; }
    public boolean isRupiah()                  { return isRupiah; }
}