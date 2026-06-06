package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private TextView btnSemua, btnOrganik, btnPlastik, btnLogam, btnRongsok;
    private EditText etCariBarang;
    private RecyclerView rvMarketplace;
    private List<Produk> semuaProdukList;
    private ImageView btnBackMarketplace;
    private FloatingActionButton fabJualRongsok;

    private String kategoriSaatIni = "Semua";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        btnBackMarketplace = findViewById(R.id.btnBackMarketplace);
        btnBackMarketplace.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnSemua = findViewById(R.id.btnFilterSemua);
        btnOrganik = findViewById(R.id.btnFilterOrganik);
        btnPlastik = findViewById(R.id.btnFilterPlastik);
        btnLogam = findViewById(R.id.btnFilterLogam);
        btnRongsok = findViewById(R.id.btnFilterRongsok); // Inisialisasi Tab Rongsok
        etCariBarang = findViewById(R.id.etCariBarang);
        fabJualRongsok = findViewById(R.id.fabJualRongsok);
        // Aksi Tombol Melayang (+) Untuk Membuka Formulir
        fabJualRongsok.setOnClickListener(v -> {
            Intent intent = new Intent(MarketplaceActivity.this, JualRongsokActivity.class);
            startActivity(intent);
        });

        rvMarketplace = findViewById(R.id.rvMarketplace);
        rvMarketplace.setLayoutManager(new GridLayoutManager(this, 2));

        // Pengisian Data (Termasuk Barang Rongsokan Baru)
        semuaProdukList = new ArrayList<>();
        // Data Lama (Poin)
        semuaProdukList.add(new Produk("Botol Plastik PET", "500 pts", "/1 kg", "Plastik", R.drawable.img_botol));
        semuaProdukList.add(new Produk("Kardus Bekas Tebal", "350 pts", "/1 kg", "Organik", R.drawable.img_kardus));
        semuaProdukList.add(new Produk("Kaleng Soda Aluminium", "800 pts", "/1 kg", "Logam", R.drawable.img_kaleng));
        semuaProdukList.add(new Produk("Minyak Jelantah Jernih", "1.200 pts", "/1 ltr", "Organik", R.drawable.img_minyak));

        // Data Baru (Barang Rongsokan dengan Rupiah untuk demo dosen)
        semuaProdukList.add(new Produk("Meja Belajar Kayu Patah", "Rp 45.000", "Sukajadi", "Rongsok", R.drawable.img_kardus));
        semuaProdukList.add(new Produk("Kipas Angin Rusak (Besi)", "Rp 35.000", "Coblong", "Rongsok", R.drawable.img_besi));
        semuaProdukList.add(new Produk("Aki Mobil Bekas Mati", "Rp 70.000", "Andir", "Rongsok", R.drawable.img_besi));
        semuaProdukList.add(new Produk("Tumpukan Koran Bekas", "Rp 15.000", "Pajajaran", "Rongsok", R.drawable.img_kardus));

        perbaruiTampilan();

        etCariBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { perbaruiTampilan(); }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tombol Filter Kategori
        btnSemua.setOnClickListener(v -> ubahKategori(btnSemua, "Semua"));
        btnOrganik.setOnClickListener(v -> ubahKategori(btnOrganik, "Organik"));
        btnPlastik.setOnClickListener(v -> ubahKategori(btnPlastik, "Plastik"));
        btnLogam.setOnClickListener(v -> ubahKategori(btnLogam, "Logam"));
        btnRongsok.setOnClickListener(v -> ubahKategori(btnRongsok, "Rongsok"));



        // Setup Profil Top
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v -> startActivity(new Intent(MarketplaceActivity.this, ProfilActivity.class)));
        }

        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String fotoPath = sharedPref.getString("foto_profil_path", null);
            if (fotoPath != null) {
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    private void ubahKategori(TextView tombolPilihan, String kategori) {
        setKategoriAktif(tombolPilihan);
        kategoriSaatIni = kategori;
        perbaruiTampilan();
    }

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
        ProdukAdapter adapter = new ProdukAdapter(this, listTersaring);
        rvMarketplace.setAdapter(adapter);
    }

    private void setKategoriAktif(TextView tombolTerpilih) {
        TextView[] daftarTombol = {btnSemua, btnOrganik, btnPlastik, btnLogam, btnRongsok};
        for (TextView btn : daftarTombol) {
            btn.setBackgroundResource(R.drawable.bg_tab_inactive);

            // Khusus warna tombol Rongsok dibuat beda
            if(btn.getId() == R.id.btnFilterRongsok) {
                btn.setTextColor(Color.parseColor("#34D399"));
            } else {
                btn.setTextColor(Color.parseColor("#475569"));
            }
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
        tombolTerpilih.setBackgroundResource(R.drawable.bg_tab_active);
        tombolTerpilih.setTextColor(Color.parseColor("#050F0B"));
        tombolTerpilih.setTypeface(null, android.graphics.Typeface.BOLD);
    }
}