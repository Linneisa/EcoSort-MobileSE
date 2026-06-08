package com.example.ecosort;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.ecosort.model.AuthError;
import com.example.ecosort.model.AuthResponse;
import com.example.ecosort.model.SignUpRequest;
import com.example.ecosort.network.SupabaseClient;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private AppCompatButton btnRegisterSubmit;
    private EditText etRegisterName, etRegisterEmail, etRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ImageView btnBackRegister = findViewById(R.id.btnBackRegister);
        etRegisterName     = findViewById(R.id.etRegisterName);
        etRegisterEmail    = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnRegisterSubmit  = findViewById(R.id.btnRegisterSubmit);

        btnBackRegister.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        btnRegisterSubmit.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name     = etRegisterName.getText().toString().trim();
        String email    = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        SupabaseClient.getAuthService()
                .signUp(new SignUpRequest(email, password, name))
                .enqueue(new Callback<AuthResponse>() {

                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            // Simpan sesi lengkap agar initPoin bisa langsung berjalan
                            simpanSesiRegister(response.body(), name, email);
                            UserPointsHelper.initPoin(RegisterActivity.this);

                            Toast.makeText(RegisterActivity.this,
                                    "Pendaftaran berhasil! Silakan masuk.",
                                    Toast.LENGTH_LONG).show();
                            finish(); // kembali ke LoginActivity
                        } else {
                            String pesan = parseError(response.errorBody());
                            Toast.makeText(RegisterActivity.this, pesan, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this,
                                "Tidak dapat terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void simpanSesiRegister(
            com.example.ecosort.model.AuthResponse auth, String nama, String email) {
        android.content.SharedPreferences.Editor ed =
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        ed.putString("nama", nama);
        ed.putString("email", email);
        if (auth.getAccessToken()  != null) ed.putString("auth_access_token",  auth.getAccessToken());
        if (auth.getRefreshToken() != null) ed.putString("auth_refresh_token", auth.getRefreshToken());
        if (auth.getUser() != null && auth.getUser().getId() != null) {
            ed.putString("user_id", auth.getUser().getId());
        }
        ed.apply();
    }

    private void setLoading(boolean isLoading) {
        btnRegisterSubmit.setEnabled(!isLoading);
        btnRegisterSubmit.setText(isLoading ? "Mendaftar..." : "Daftar");
    }

    private String parseError(ResponseBody errorBody) {
        if (errorBody == null) return "Pendaftaran gagal, silakan coba lagi";
        try {
            return new Gson().fromJson(errorBody.charStream(), AuthError.class).getMessage();
        } catch (Exception e) {
            return "Pendaftaran gagal, silakan coba lagi";
        }
    }
}
