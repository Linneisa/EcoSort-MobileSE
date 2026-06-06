package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AdminScanActivity extends AppCompatActivity {

    // Deklarasi Variabel Komponen UI
    private LinearLayout layoutHasilScan;
    private TextView txtIdTransaksi, txtJenisSampah, txtEstimasiBerat, txtPoinFinal;
    private EditText etBeratAsli;
    private Button btnKonfirmasi;

    // Variabel penyimpan data simulasi Database
    private String currentIdTransaksi = "";
    private int poinPerKgDariDatabase = 0;
    private String currentUserId = "";
    private String namaPengguna = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_scan);

        // Inisialisasi UI (Menyambungkan Java dengan XML)
        CardView btnScanBarcode = findViewById(R.id.btnScanBarcode);
        layoutHasilScan = findViewById(R.id.layoutHasilScan);
        txtIdTransaksi = findViewById(R.id.txtIdTransaksi);
        txtJenisSampah = findViewById(R.id.txtJenisSampah);
        txtEstimasiBerat = findViewById(R.id.txtEstimasiBerat);
        txtPoinFinal = findViewById(R.id.txtPoinFinal);
        etBeratAsli = findViewById(R.id.etBeratAsli);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasiSelesai);

        // 1. Aksi ketika tombol SCAN ditekan
        btnScanBarcode.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(AdminScanActivity.this);
            integrator.setPrompt("Arahkan kamera ke QR Code Tiket");
            integrator.setBeepEnabled(true);
            integrator.setOrientationLocked(true);
            integrator.initiateScan();
        });

        // 2. Aksi perhitungan otomatis saat Admin mengetik berat di timbangan
        etBeratAsli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        double beratAsli = Double.parseDouble(s.toString());
                        int totalPoin = (int) (beratAsli * poinPerKgDariDatabase);
                        txtPoinFinal.setText(totalPoin + " pts");
                    } catch (NumberFormatException e) {
                        txtPoinFinal.setText("0 pts");
                    }
                } else {
                    txtPoinFinal.setText("0 pts");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 3. Aksi tombol Konfirmasi
        btnKonfirmasi.setOnClickListener(v -> {
            String beratAkhir = etBeratAsli.getText().toString();
            if(beratAkhir.isEmpty()){
                Toast.makeText(this, "Masukkan berat asli terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }
            updateDatabaseTransaksi(currentIdTransaksi, beratAkhir, txtPoinFinal.getText().toString());
        });
    }

    // Tangkap hasil scan kamera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String idHasilScan = result.getContents();
                tarikDataDariDatabase(idHasilScan);
            } else {
                Toast.makeText(this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ========================================================================
    // AREA KONEKSI DATABASE
    // ========================================================================

    private void tarikDataDariDatabase(String idTransaksi) {
        Toast.makeText(this, "Mencari pemilik transaksi " + idTransaksi + "...", Toast.LENGTH_SHORT).show();

        // Simulasi data ditemukan dari Server:
        currentIdTransaksi = idTransaksi;
        poinPerKgDariDatabase = 500;
        currentUserId = "USER-8812";
        namaPengguna = "Christoffer Bintang";

        // Tampilkan datanya ke Layar Admin
        txtIdTransaksi.setText("ID Transaksi: " + currentIdTransaksi + " (" + namaPengguna + ")");
        txtJenisSampah.setText("Jenis: Botol Plastik PET");
        txtEstimasiBerat.setText("Estimasi User: 2 kg");
        etBeratAsli.setText("");
        txtPoinFinal.setText("0 pts");

        layoutHasilScan.setVisibility(View.VISIBLE);
    }

    private void updateDatabaseTransaksi(String idTransaksi, String beratAsli, String poinFinal) {
        // Bersihkan teks " pts" dengan aman untuk mengambil angkanya saja
        int poinYangDidapat = 0;
        try {
            poinYangDidapat = Integer.parseInt(poinFinal.replace(" pts", "").trim());
        } catch (Exception e) {
            poinYangDidapat = 0;
        }

        // Simulasi sukses memasukkan data ke Database
        Toast.makeText(this, "Sukses! " + poinYangDidapat + " Poin otomatis masuk ke akun " + namaPengguna, Toast.LENGTH_LONG).show();

        // Sembunyikan layar lagi
        layoutHasilScan.setVisibility(View.GONE);
        etBeratAsli.setText("");
        currentUserId = "";
    }
}