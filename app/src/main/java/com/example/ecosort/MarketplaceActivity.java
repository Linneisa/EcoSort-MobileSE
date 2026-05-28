package com.example.ecosort;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView; // 👇 Import ImageView ditambahkan
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private TextView btnSemua, btnOrganik, btnPlastik, btnLogam;
    private EditText etCariBarang;
    private RecyclerView rvMarketplace;
    private List<Produk> semuaProdukList;
    private ImageView btnBackMarketplace; // 👇 Tambahkan variabel untuk tombol back manual

    // VARIABEL BARU: Untuk mengingat kategori apa yang sedang diklik user
    private String kategoriSaatIni = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        // 👇 PERBAIKAN: Hubungkan tombol back manual dari XML dan beri logika klik
        btnBackMarketplace = findViewById(R.id.btnBackMarketplace);
        btnBackMarketplace.setOnClickListener(v -> {
            onBackPressed(); // Menutup halaman ini dan otomatis kembali ke Home
        });

        // Inisialisasi Komponen Lainnya
        btnSemua = findViewById(R.id.btnFilterSemua);
        btnOrganik = findViewById(R.id.btnFilterOrganik);
        btnPlastik = findViewById(R.id.btnFilterPlastik);
        btnLogam = findViewById(R.id.btnFilterLogam);
        etCariBarang = findViewById(R.id.etCariBarang);

        // Setup RecyclerView Grid
        rvMarketplace = findViewById(R.id.rvMarketplace);
        rvMarketplace.setLayoutManager(new GridLayoutManager(this, 2));

        // Pengisian Data
        semuaProdukList = new ArrayList<>();
        semuaProdukList.add(new Produk("Botol Plastik PET", "500 pts", "/1 kg", "Plastik", R.drawable.img_botol));
        semuaProdukList.add(new Produk("Kardus Bekas Tebal", "350 pts", "/1 kg", "Organik", R.drawable.img_kardus));
        semuaProdukList.add(new Produk("Kaleng Soda Aluminium", "800 pts", "/1 kg", "Logam", R.drawable.img_kaleng));
        semuaProdukList.add(new Produk("Minyak Jelantah Jernih", "1.200 pts", "/1 ltr", "Organik", R.drawable.img_minyak));
        semuaProdukList.add(new Produk("Gelas Plastik Air Mineral", "400 pts", "/1 kg", "Plastik", R.drawable.img_gelas));
        semuaProdukList.add(new Produk("Besi Siku Bekas", "1.500 pts", "/1 kg", "Logam", R.drawable.img_besi));

        // Tampilan Pertama Kali Buka
        perbaruiTampilan();

        // Sensor Ketikan
        etCariBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                perbaruiTampilan();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tombol Filter Kategori
        btnSemua.setOnClickListener(v -> {
            setKategoriAktif(btnSemua);
            kategoriSaatIni = "Semua";
            perbaruiTampilan();
        });
        btnOrganik.setOnClickListener(v -> {
            setKategoriAktif(btnOrganik);
            kategoriSaatIni = "Organik";
            perbaruiTampilan();
        });
        btnPlastik.setOnClickListener(v -> {
            setKategoriAktif(btnPlastik);
            kategoriSaatIni = "Plastik";
            perbaruiTampilan();
        });
        btnLogam.setOnClickListener(v -> {
            setKategoriAktif(btnLogam);
            kategoriSaatIni = "Logam";
            perbaruiTampilan();
        });
    }

    // =========================================================================
    // MESIN UTAMA: Menyaring berdasarkan Kategori AND Ketikan Pencarian
    // =========================================================================
    private void perbaruiTampilan() {
        String keyword = etCariBarang.getText().toString().trim().toLowerCase();
        List<Produk> listTersaring = new ArrayList<>();

        for (Produk p : semuaProdukList) {
            boolean cocokKategori = kategoriSaatIni.equals("Semua") || p.getKategori().equalsIgnoreCase(kategoriSaatIni);
            boolean cocokKeyword = p.getNama().toLowerCase().contains(keyword);

            if (cocokKategori && cocokKeyword) {
                listTersaring.add(p);
            }
        }
        tampilkanDataKeGrid(listTersaring);
    }

    private void tampilkanDataKeGrid(List<Produk> list) {
        ProdukAdapter adapter = new ProdukAdapter(this, list);
        rvMarketplace.setAdapter(adapter);
    }

    private void setKategoriAktif(TextView tombolTerpilih) {
        TextView[] daftarTombol = {btnSemua, btnOrganik, btnPlastik, btnLogam};
        for (TextView btn : daftarTombol) {
            btn.setBackgroundResource(R.drawable.bg_tab_inactive);
            btn.setTextColor(Color.parseColor("#475569"));
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        tombolTerpilih.setBackgroundResource(R.drawable.bg_tab_active);
        tombolTerpilih.setTextColor(Color.parseColor("#050F0B"));
        tombolTerpilih.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}