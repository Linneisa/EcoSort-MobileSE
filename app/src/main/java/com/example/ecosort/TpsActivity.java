package com.example.ecosort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecosort.model.LokasiTpsModel;
import com.example.ecosort.network.SupabaseClient;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TpsActivity extends AppCompatActivity {

    private MapView mapView;
    private TpsAdapter adapter;
    private final List<LokasiTpsModel> listTps = new ArrayList<>();

    // Default center: Bandung
    private static final double DEFAULT_LAT = -6.9175;
    private static final double DEFAULT_LNG = 107.6191;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Configuration.getInstance().load(getApplicationContext(),
                getSharedPreferences("osmdroid", Context.MODE_PRIVATE));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tps);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        setupMap();
        setupRecyclerView();
        setupHeader();
        loadLokasiTps();
    }

    private void setupMap() {
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(13.0);
        mapView.getController().setCenter(new GeoPoint(DEFAULT_LAT, DEFAULT_LNG));
    }

    private void setupRecyclerView() {
        RecyclerView rvTps = findViewById(R.id.rvTps);
        rvTps.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new TpsAdapter(listTps, tps -> {
            if (!tps.hasValidCoordinates()) {
                Toast.makeText(this, "Koordinat TPS tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }
            tampilkanDialogPilihan(tps);
        });
        rvTps.setAdapter(adapter);
    }

    private void setupHeader() {
        ImageView btnBackTps = findViewById(R.id.btnBackTps);
        btnBackTps.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        CardView btnProfilTop = findViewById(R.id.btnProfilTop);
        if (btnProfilTop != null) {
            btnProfilTop.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfilActivity.class)));
        }

        ImageView imgProfilHeader = findViewById(R.id.imgProfilHeader);
        if (imgProfilHeader != null) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String fotoPath = prefs.getString("foto_profil_path", null);
            if (fotoPath != null) {
                imgProfilHeader.setImageURI(Uri.parse(fotoPath));
            } else {
                imgProfilHeader.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }

    private void loadLokasiTps() {
        Log.d("TPS_DEBUG", "loadLokasiTps() dipanggil — menggunakan anon key (data publik)");

        SupabaseClient.getApiService()
                .getLokasiTps("eq.true", "*")
                .enqueue(new Callback<List<LokasiTpsModel>>() {
                    @Override
                    public void onResponse(Call<List<LokasiTpsModel>> call,
                                           Response<List<LokasiTpsModel>> response) {
                        Log.d("TPS_DEBUG", "HTTP response code: " + response.code());

                        if (!response.isSuccessful()) {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null)
                                    errorBody = response.errorBody().string();
                            } catch (Exception ignored) {}
                            Log.e("TPS_DEBUG", "Error body: " + errorBody);
                            Toast.makeText(TpsActivity.this,
                                    "Gagal memuat TPS (kode " + response.code() + "): " + errorBody,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (response.body() == null || response.body().isEmpty()) {
                            Log.w("TPS_DEBUG", "Response 200 OK tapi data kosong. " +
                                    "Cek: (1) tabel lokasi_tps ada? (2) ada baris is_active=true? (3) RLS Supabase aktif?");
                            Toast.makeText(TpsActivity.this,
                                    "Tidak ada TPS aktif. Cek tabel lokasi_tps di Supabase.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d("TPS_DEBUG", "Berhasil: " + response.body().size() + " TPS diterima");
                        for (LokasiTpsModel t : response.body()) {
                            Log.d("TPS_DEBUG", "  id=" + t.getId()
                                    + " nama=" + t.getNama()
                                    + " lat=" + t.getLatitude()
                                    + " lng=" + t.getLongitude()
                                    + " active=" + t.isActive());
                        }
                        listTps.clear();
                        listTps.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        tampilkanMarker();
                    }

                    @Override
                    public void onFailure(Call<List<LokasiTpsModel>> call, Throwable t) {
                        Log.e("TPS_DEBUG", "onFailure: " + t.getMessage(), t);
                        Toast.makeText(TpsActivity.this,
                                "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void tampilkanDialogPilihan(LokasiTpsModel tps) {
        String[] pilihan = {"Google Maps", "Lihat di Peta App"};
        new AlertDialog.Builder(this)
                .setTitle("Buka dengan aplikasi apa?")
                .setItems(pilihan, (dialog, which) -> {
                    if (which == 0) {
                        bukaGoogleMaps(tps);
                    } else {
                        GeoPoint point = new GeoPoint(tps.getLatitude(), tps.getLongitude());
                        mapView.getController().animateTo(point);
                        mapView.getController().setZoom(17.0);
                    }
                })
                .show();
    }

    private void bukaGoogleMaps(LokasiTpsModel tps) {
        double lat = tps.getLatitude();
        double lng = tps.getLongitude();

        Uri gmmUri = Uri.parse("google.navigation:q=" + lat + "," + lng);
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, gmmUri);
        mapsIntent.setPackage("com.google.android.apps.maps");

        if (mapsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapsIntent);
        } else {
            String url = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    private void tampilkanMarker() {
        mapView.getOverlays().clear();
        LokasiTpsModel pertama = null;
        for (LokasiTpsModel tps : listTps) {
            if (!tps.hasValidCoordinates()) {
                Log.w("TPS_DEBUG", "Skip marker '" + tps.getNama() + "': koordinat null/0");
                continue;
            }
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(tps.getLatitude(), tps.getLongitude()));
            marker.setTitle(tps.getNama());
            marker.setSnippet(tps.getAlamat());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(marker);
            if (pertama == null) pertama = tps;
        }
        if (pertama != null) {
            mapView.getController().animateTo(
                    new GeoPoint(pertama.getLatitude(), pertama.getLongitude()));
        }
        mapView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
