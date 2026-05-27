package com.example.ecosort;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class TpsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tps);
        // Baris ini tidak perlu diubah
        // ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //     Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //     v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //     return insets;
        // });

        // 1. Ganti pencarian R.id ke R.id.gridTps
        android.widget.GridView gridViewTps = findViewById(R.id.gridTps);

        // Data yang sama
        String[] dataTps = {
                "TPS Terpadu Babakan Siliwangi - Jl. Siliwangi",
                "TPS Pajajaran - Jl. Pajajaran",
                "TPS Ciroyom - Pasar Ciroyom Barat",
                "TPS Tamansari - Kawasan Tamansari",
                "TPS Tegalega - Kawasan Monumen BLA",
                "TPS Pahlawan - Jl. Cikutra Barat"
        };

        // 2. Gunakan Layout Kartu yang Baru
        // Kita gunakan layout 'item_grid_layanan' sebagai pengganti layout daftar standar.
        // Kita juga tentukan R.id.textTpsItem sebagai TextView target di dalam kartu tersebut.
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(
                this,
                R.layout.item_grid_layanan,  // GANTI: Layout kartu baru
                R.id.textTpsItem,            // GANTI: TextView target di dalam kartu
                dataTps
        );

        // Set adapter ke GridView (bukan ListView lagi)
        gridViewTps.setAdapter(adapter);

    }
}