package com.example.ecosort;

import android.os.Bundle;
import android.widget.ImageView; // Import ImageView ditambahkan
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RewardActivity extends AppCompatActivity {

    private RecyclerView rvReward;
    private ImageView btnBackReward; // Tambahkan variabel tombol back manual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        // 👇 PERBAIKAN: Hubungkan tombol back manual dari XML dan beri logika klik
        btnBackReward = findViewById(R.id.btnBackReward);
        btnBackReward.setOnClickListener(v -> {
            onBackPressed(); // Menutup halaman ini dan otomatis kembali ke Home
        });

        rvReward = findViewById(R.id.rvReward);
        rvReward.setLayoutManager(new GridLayoutManager(this, 2)); // Tampilan grid 2 kolom

        // Pengisian Data Dummy
        List<Reward> listData = new ArrayList<>();
        listData.add(new Reward("Saldo E-Wallet Rp10k", "500 pts", "Stok: 50", R.drawable.img_wallet));
        listData.add(new Reward("Voucher Belanja Rp50k", "2.200 pts", "Stok: 15", R.drawable.img_voucher));
        listData.add(new Reward("Pulsa All Operator Rp20k", "950 pts", "Stok: 34", R.drawable.img_pulsa));
        listData.add(new Reward("Token Listrik Rp50k", "2.300 pts", "Stok: 8", R.drawable.img_token));
        listData.add(new Reward("Tumbler EcoSort Eksklusif", "4.000 pts", "Stok: 3", R.drawable.img_tumbler));
        listData.add(new Reward("Alat Makan Stainless Set", "1.200 pts", "Stok: 20", R.drawable.img_alatmakan));

        RewardAdapter adapter = new RewardAdapter(listData);
        rvReward.setAdapter(adapter);
    }
}