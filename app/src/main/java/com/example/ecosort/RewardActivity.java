package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// DI SINI PERBAIKANNYA: Nama kelas harus sesuai dengan nama file
public class RewardActivity extends AppCompatActivity {

    private RecyclerView rvReward;
    private ImageView btnBackReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        btnBackReward = findViewById(R.id.btnBackReward);
        btnBackReward.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        rvReward = findViewById(R.id.rvReward);
        rvReward.setLayoutManager(new GridLayoutManager(this, 2));

        List<Reward> listData = new ArrayList<>();
        listData.add(new Reward("Saldo E-Wallet Rp10k", "5.000 pts", "Stok: 50", R.drawable.img_wallet));
        listData.add(new Reward("Voucher Belanja Rp50k", "22.000 pts", "Stok: 15", R.drawable.img_voucher));
        listData.add(new Reward("Pulsa All Operator Rp20k", "9.500 pts", "Stok: 34", R.drawable.img_pulsa));
        listData.add(new Reward("Token Listrik Rp50k", "23.000 pts", "Stok: 8", R.drawable.img_token));
        listData.add(new Reward("Tumbler EcoSort Eksklusif", "40.000 pts", "Stok: 3", R.drawable.img_tumbler));
        listData.add(new Reward("Alat Makan Stainless Set", "10.000 pts", "Stok: 20", R.drawable.img_alatmakan));

        RewardAdapter adapter = new RewardAdapter(listData);
        rvReward.setAdapter(adapter);

        // Tombol profil
        androidx.cardview.widget.CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v -> {
                Intent intent = new Intent(RewardActivity.this, ProfilActivity.class);
                startActivity(intent);
            });
        }

        // Ambil foto profil dari memori
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
}