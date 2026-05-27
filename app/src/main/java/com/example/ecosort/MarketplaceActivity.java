package com.example.ecosort;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MarketplaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marketplace);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        android.widget.ListView listViewMarketplace = findViewById(R.id.listMarketplace);

        String[] dataMarketplace = {
                "Botol Plastik Bekas (PET) - Rp 3.000 / Kg",
                "Kardus Bekas Lembaran - Rp 1.500 / Kg",
                "Kertas HVS & Buku Bekas - Rp 2.000 / Kg",
                "Minyak Jelantah Rumah Tangga - Rp 5.000 / Liter",
                "Kaleng Minuman Alumunium - Rp 9.000 / Kg",
                "Botol Kaca Bening - Rp 1.000 / Kg"
        };

        android.widget.ArrayAdapter<String> adapterMarket = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataMarketplace
        );

        listViewMarketplace.setAdapter(adapterMarket);

    }
}