package com.example.ecosort;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetailProdukActivity extends AppCompatActivity {

    private ImageView btnBackDetail, imgDetailProduk;
    private TextView txtNamaDetail, txtHargaDetail, txtTotalPoinEstimasi;
    private EditText edtBeratEstimasi;
    private Button btnTambahKeranjang;

    private int hargaSatuItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        btnBackDetail = findViewById(R.id.btnBackDetail);
        imgDetailProduk = findViewById(R.id.imgDetailProduk);
        txtNamaDetail = findViewById(R.id.txtNamaDetail);
        txtHargaDetail = findViewById(R.id.txtHargaDetail);
        txtTotalPoinEstimasi = findViewById(R.id.txtTotalPoinEstimasi);
        edtBeratEstimasi = findViewById(R.id.edtBeratEstimasi);
        btnTambahKeranjang = findViewById(R.id.btnTambahKeranjang);

        // Ambil data dari Intent
        String nama = getIntent().getStringExtra("PRODUK_NAMA");
        String hargaString = getIntent().getStringExtra("PRODUK_HARGA");
        int gambarResId = getIntent().getIntExtra("PRODUK_GAMBAR", android.R.drawable.ic_menu_gallery);

        if (nama != null) txtNamaDetail.setText(nama);
        if (hargaString != null) txtHargaDetail.setText(hargaString + " /kg");
        imgDetailProduk.setImageResource(gambarResId);

        // Ekstrak angka saja dari string harga
        if (hargaString != null) {
            String angkaSaja = hargaString.replaceAll("[^0-9]", "");
            if (!angkaSaja.isEmpty()) {
                hargaSatuItem = Integer.parseInt(angkaSaja);
            }
        }

        // Hitung poin otomatis saat user mengetik berat sampah
        edtBeratEstimasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        double berat = Double.parseDouble(s.toString());
                        int totalPoin = (int) (berat * hargaSatuItem);
                        txtTotalPoinEstimasi.setText(totalPoin + " pts");
                    } catch (NumberFormatException e) {
                        txtTotalPoinEstimasi.setText("0 pts");
                    }
                } else {
                    txtTotalPoinEstimasi.setText("0 pts");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tombol Kembali
        if (btnBackDetail != null) {
            btnBackDetail.setOnClickListener(v -> finish());
        }

        // FIX UPDATE: Mengarahkan langsung ke halaman KeranjangActivity
        if (btnTambahKeranjang != null) {
            btnTambahKeranjang.setOnClickListener(v -> {
                String inputBerat = edtBeratEstimasi.getText().toString();
                if (inputBerat.isEmpty() || Double.parseDouble(inputBerat) <= 0) {
                    Toast.makeText(this, "Silakan masukkan berat sampah yang valid!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentKeCart = new Intent(DetailProdukActivity.this, KeranjangActivity.class);
                    intentKeCart.putExtra("KARTU_NAMA", txtNamaDetail.getText().toString());
                    intentKeCart.putExtra("KARTU_BERAT", inputBerat);
                    intentKeCart.putExtra("KARTU_POIN", txtTotalPoinEstimasi.getText().toString());
                    startActivity(intentKeCart);
                    finish();
                }
            });
        }
    }
}