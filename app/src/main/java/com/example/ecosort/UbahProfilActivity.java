package com.example.ecosort;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.ProfileModel;
import com.example.ecosort.model.ProfileRequest;
import com.example.ecosort.network.SupabaseClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UbahProfilActivity extends AppCompatActivity {

    private ImageView ivFotoProfil;
    private Uri imageUri;
    private TextView btnSimpan;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    ivFotoProfil.setImageURI(uri);
                    getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);

        ivFotoProfil = findViewById(R.id.ivFotoProfil);
        btnSimpan    = findViewById(R.id.btnSimpan);

        findViewById(R.id.cardEditFoto).setOnClickListener(v -> pickImage.launch("image/*"));
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        EditText etNama    = findViewById(R.id.etNama);
        EditText etNomorHP = findViewById(R.id.etNomorHP);
        EditText etEmail   = findViewById(R.id.etEmail);

        // Isi field dari cache SharedPreferences
        etNama.setText(prefs.getString("nama", ""));
        etNomorHP.setText(prefs.getString("hp", ""));
        etEmail.setText(prefs.getString("email", ""));

        String savedUri = prefs.getString("foto_profil_path", null);
        if (savedUri != null) ivFotoProfil.setImageURI(Uri.parse(savedUri));

        btnSimpan.setOnClickListener(v -> {
            String nama  = etNama.getText().toString().trim();
            String hp    = etNomorHP.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // 1. Simpan ke SharedPreferences (cache lokal, instan)
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("nama",  nama);
            editor.putString("hp",    hp);
            editor.putString("email", email);
            if (imageUri != null) editor.putString("foto_profil_path", imageUri.toString());
            editor.apply();

            // 2. Sync ke Supabase
            String userId      = prefs.getString("user_id", "");
            String accessToken = prefs.getString("auth_access_token", "");

            if (userId.isEmpty() || accessToken.isEmpty()) {
                // Belum login dengan Supabase, cukup simpan lokal
                Toast.makeText(this, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            setLoading(true);

            String bearerToken = "Bearer " + accessToken;
            ProfileRequest request = new ProfileRequest(userId, nama, hp, email);

            SupabaseClient.getApiService()
                    .upsertProfile(bearerToken, request)
                    .enqueue(new Callback<List<ProfileModel>>() {

                        @Override
                        public void onResponse(Call<List<ProfileModel>> call,
                                               Response<List<ProfileModel>> response) {
                            setLoading(false);
                            if (response.isSuccessful()) {
                                Toast.makeText(UbahProfilActivity.this,
                                        "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UbahProfilActivity.this,
                                        "Tersimpan lokal. Gagal sync ke server (kode " + response.code() + ")",
                                        Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }

                        @Override
                        public void onFailure(Call<List<ProfileModel>> call, Throwable t) {
                            setLoading(false);
                            Toast.makeText(UbahProfilActivity.this,
                                    "Tersimpan lokal. Tidak dapat terhubung ke server.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        });
    }

    private void setLoading(boolean isLoading) {
        btnSimpan.setEnabled(!isLoading);
        btnSimpan.setText(isLoading ? "Menyimpan..." : "SIMPAN");
        btnSimpan.setAlpha(isLoading ? 0.6f : 1.0f);
    }
}
