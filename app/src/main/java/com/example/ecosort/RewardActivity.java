package com.example.ecosort;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RewardActivity extends AppCompatActivity {

    private RecyclerView rvReward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        rvReward = findViewById(R.id.rvReward);
        rvReward.setLayoutManager(new GridLayoutManager(this, 2)); // Tampilan grid 2 kolom

        // Mengisi data dummy list hadiah
        List<Reward> listData = new ArrayList<>();
        listData.add(new Reward("Saldo E-Wallet Rp10k", "500 pts", "Stok: 50"));
        listData.add(new Reward("Voucher Belanja Rp50k", "2.200 pts", "Stok: 15"));
        listData.add(new Reward("Pulsa All Operator Rp20k", "950 pts", "Stok: 34"));
        listData.add(new Reward("Token Listrik Rp50k", "2.300 pts", "Stok: 8"));
        listData.add(new Reward("Tumbler EcoSort Eksklusif", "4.000 pts", "Stok: 3"));
        listData.add(new Reward("Sedotan Stainless Set", "1.200 pts", "Stok: 20"));

        RewardAdapter adapter = new RewardAdapter(listData);
        rvReward.setAdapter(adapter);
    }
}