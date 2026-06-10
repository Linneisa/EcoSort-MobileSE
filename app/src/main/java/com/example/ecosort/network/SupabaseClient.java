package com.example.ecosort.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    private static final String BASE_URL = "https://oshqjvaqvnuhroteofen.supabase.co/rest/v1/";
    private static final String AUTH_URL = "https://oshqjvaqvnuhroteofen.supabase.co/auth/v1/";
    private static final String ANON_KEY =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9zaHFqdmFxdm51aHJvdGVvZmVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA4OTI1NzYsImV4cCI6MjA5NjQ2ODU3Nn0" +
            ".Qex1ocdCKfioB_LopwXMS_T60zWyfOKyLNPHOKS1vI0";

    private static Retrofit restRetrofit = null;
    private static Retrofit authRetrofit = null;

    // OkHttpClient dibagi oleh kedua client
    private static OkHttpClient buildOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("apikey", ANON_KEY)
                            .header("Content-Type", "application/json");

                    // Gunakan token user jika sudah di-set lewat @Header("Authorization").
                    // Jika belum ada, gunakan anon key sebagai fallback.
                    if (original.header("Authorization") == null) {
                        builder.header("Authorization", "Bearer " + ANON_KEY);
                    }

                    return chain.proceed(builder.method(original.method(), original.body()).build());
                })
                .addInterceptor(logging)
                .build();
    }

    // Gson yang toleran: handle Double dari JSON string ("1.23") maupun JSON number (1.23)
    private static Gson buildGson() {
        JsonDeserializer<Double> doubleDeserializer = (json, type, ctx) -> {
            try { return json.getAsDouble(); } catch (Exception e) { return null; }
        };
        return new GsonBuilder()
                .registerTypeAdapter(Double.class, doubleDeserializer)
                .registerTypeAdapter(double.class, doubleDeserializer)
                .create();
    }

    // REST API client — untuk tabel data (jadwal, produk, dll.)
    public static SupabaseApiService getApiService() {
        if (restRetrofit == null) {
            restRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(buildGson()))
                    .client(buildOkHttpClient())
                    .build();
        }
        return restRetrofit.create(SupabaseApiService.class);
    }

    // Auth client — untuk login dan register
    public static AuthApiService getAuthService() {
        if (authRetrofit == null) {
            authRetrofit = new Retrofit.Builder()
                    .baseUrl(AUTH_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
        }
        return authRetrofit.create(AuthApiService.class);
    }
}
