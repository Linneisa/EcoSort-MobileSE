package com.example.ecosort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CsActivity extends AppCompatActivity {

    private ImageView btnBackCS;
    private CardView btnHubungiWA, btnHubungiEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs);

        btnBackCS = findViewById(R.id.btnBackCS);
        btnHubungiWA = findViewById(R.id.btnHubungiWA);
        btnHubungiEmail = findViewById(R.id.btnHubungiEmail);

        // Tombol Kembali ke Dashboard
        if (btnBackCS != null) {
            btnBackCS.setOnClickListener(v -> finish());
        }

        if (btnHubungiWA != null) {
            btnHubungiWA.setOnClickListener(v -> {
                try {
                    String nomorCS = "6281273103919";
                    String url = "https://api.whatsapp.com/send?phone=" + nomorCS + "&text=Halo%20Admin%20EcoSort,%20saya%20butuh%20bantuan.";
                    Intent intentWA = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intentWA);
                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp tidak terinstal", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Aksi Kirim Email
        if (btnHubungiEmail != null) {
            btnHubungiEmail.setOnClickListener(v -> {
                try {
                    Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
                    intentEmail.setData(Uri.parse("mailto:support@ecosort.id"));
                    intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Tanya EcoSort");
                    startActivity(intentEmail);
                } catch (Exception e) {
                    Toast.makeText(this, "Aplikasi email tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}