package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecosort.model.AuthError;
import com.example.ecosort.model.AuthRequest;
import com.example.ecosort.model.AuthResponse;
import com.example.ecosort.model.UserMetadata;
import com.example.ecosort.network.SupabaseClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private MaterialButton btnLogin;
    private TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Jika sudah punya sesi, arahkan sesuai role
        SharedPreferences savedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        if (savedPrefs.contains("ROLE")) {
            Class<?> dest = "Admin".equals(savedPrefs.getString("ROLE", "User"))
                    ? AdminScanActivity.class
                    : MainActivity.class;
            startActivity(new Intent(this, dest));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
            );
        }

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        SupabaseClient.getAuthService()
                .signIn(new AuthRequest(email, password))
                .enqueue(new Callback<AuthResponse>() {

                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            simpanSesi(response.body(), email);
                            UserPointsHelper.initPoin(LoginActivity.this);
                            // Arahkan berdasarkan role yang baru disimpan
                            Class<?> dest = email.equalsIgnoreCase("admin@ecosort.com")
                                    ? AdminScanActivity.class
                                    : MainActivity.class;
                            startActivity(new Intent(LoginActivity.this, dest));
                            finish();
                        } else {
                            String pesan = parseError(response.errorBody());
                            Toast.makeText(LoginActivity.this, pesan, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(LoginActivity.this,
                                "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void simpanSesi(AuthResponse auth, String email) {
        SharedPreferences.Editor editor =
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();

        editor.putString("ROLE", email.equalsIgnoreCase("admin@ecosort.com") ? "Admin" : "User");
        editor.putString("email", email);

        if (auth.getAccessToken()  != null) editor.putString("auth_access_token",  auth.getAccessToken());
        if (auth.getRefreshToken() != null) editor.putString("auth_refresh_token", auth.getRefreshToken());

        if (auth.getUser() != null) {
            if (auth.getUser().getId() != null) {
                editor.putString("user_id", auth.getUser().getId());
            }

            // Ambil nama dari user_metadata Supabase (disimpan saat signup)
            UserMetadata metadata = auth.getUser().getUserMetadata();
            if (metadata != null && metadata.getFullName() != null && !metadata.getFullName().isEmpty()) {
                editor.putString("nama", metadata.getFullName());
            }
        }

        editor.apply();
    }

    private void setLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnLogin.setText(isLoading ? "Memuat..." : "MASUK");
    }

    private String parseError(ResponseBody errorBody) {
        if (errorBody == null) return "Login gagal, silakan coba lagi";
        try {
            return new Gson().fromJson(errorBody.charStream(), AuthError.class).getMessage();
        } catch (Exception e) {
            return "Login gagal, silakan coba lagi";
        }
    }
}
