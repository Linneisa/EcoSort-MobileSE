package com.example.ecosort;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TpsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tps);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        android.widget.ListView listViewTps = findViewById(R.id.listTps);

        String[] dataTps = {
                "TPS Terpadu Babakan Siliwangi - Jl. Siliwangi",
                "TPS Pajajaran - Jl. Pajajaran",
                "TPS Ciroyom - Pasar Ciroyom Barat",
                "TPS Tamansari - Kawasan Tamansari",
                "TPS Tegalega - Kawasan Monumen BLA",
                "TPS Pahlawan - Jl. Cikutra Barat"
        };

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                dataTps
        );

        listViewTps.setAdapter(adapter);

    }
}