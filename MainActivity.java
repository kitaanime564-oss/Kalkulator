package com.zakat.keuangan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Import pustaka Google AdMob
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Komponen AdMob
    private AdView mAdView;

    // Komponen UI
    private EditText etNominalHarta;
    private TextView tvFormatNominal;
    private TextView tvHasilZakat;
    private TextView tvStatusNisab;
    private TextView tvCatatanHistory;
    private Button btnHitungZakat;
    private Button btnReset;
    private Button btnSimpanCatatan;
    private Button btnHapusHistory;

    // Konstanta Nisab Emas (~85 gram setara perkiraan Rp 85.000.000)
    private static final double NISAB_DEFAULT = 85000000.0;
    private static final String PREF_NAME = "ZakatKeuanganPrefs";
    private static final String KEY_HISTORY = "history_catatan";

    private double lastNominal = 0.0;
    private double lastZakat = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inisialisasi SDK Google AdMob terlebih dahulu
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Inisialisasi AdMob selesai
            }
        });

        // 2. Hubungkan komponen AdView dari file XML layout
        mAdView = findViewById(R.id.adView);

        // 3. Muat iklan Banner menggunakan AdRequest
        AdRequest adRequest = new AdRequest.Builder().build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }

        // 4. Inisialisasi komponen Tampilan UI
        initViews();

        // 5. Muat catatan keuangan yang tersimpan sebelumnya
        loadCatatanHistory();

        // 6. Pasang aksi Listener untuk tombol-tombol
        setupListeners();
    }

    private void initViews() {
        etNominalHarta = findViewById(R.id.etNominalHarta);
        tvFormatNominal = findViewById(R.id.tvFormatNominal);
        tvHasilZakat = findViewById(R.id.tvHasilZakat);
        tvStatusNisab = findViewById(R.id.tvStatusNisab);
        tvCatatanHistory = findViewById(R.id.tvCatatanHistory);
        btnHitungZakat = findViewById(R.id.btnHitungZakat);
        btnReset = findViewById(R.id.btnReset);
        btnSimpanCatatan = findViewById(R.id.btnSimpanCatatan);
        btnHapusHistory = findViewById(R.id.btnHapusHistory);
    }

    private void setupListeners() {
        // Tampilkan format rupiah secara live saat pengguna mengetik angka
        etNominalHarta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (!input.isEmpty()) {
                    try {
                        double nominal = Double.parseDouble(input);
                        tvFormatNominal.setText("Nominal: " + formatRupiah(nominal));
                    } catch (NumberFormatException e) {
                        tvFormatNominal.setText("Nominal: Angka tidak valid");
                    }
                } else {
                    tvFormatNominal.setText("Nominal: Rp 0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tombol Hitung Zakat (2,5%)
        btnHitungZakat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitungZakat();
            }
        });

        // Tombol Reset Form
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNominalHarta.setText("");
                tvHasilZakat.setText("Rp 0");
                tvStatusNisab.setText("*Nisab emas 85 gram (~Rp 85.000.000) dan telah genap 1 haul (tahun).");
                lastNominal = 0.0;
                lastZakat = 0.0;
            }
        });

        // Tombol Simpan ke Catatan Keuangan
        btnSimpanCatatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanCatatanKeuangan();
            }
        });

        // Tombol Hapus Riwayat Catatan
        btnHapusHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });
    }

    /**
     * Menghitung zakat 2.5% dari nominal harta yang diinputkan pengguna
     */
    private void hitungZakat() {
        String inputStr = etNominalHarta.getText().toString().trim();

        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Silakan masukkan nominal harta terlebih dahulu!", Toast.LENGTH_SHORT).show();
            etNominalHarta.requestFocus();
            return;
        }

        try {
            double nominalHarta = Double.parseDouble(inputStr);

            if (nominalHarta <= 0) {
                Toast.makeText(this, "Nominal harta harus lebih besar dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Rumus Zakat Maal: 2,5% (0.025) dari total harta yang telah memenuhi syarat
            double zakatWajib = nominalHarta * 0.025;

            lastNominal = nominalHarta;
            lastZakat = zakatWajib;

            // Tampilkan hasil zakat dalam format mata uang Rupiah
            tvHasilZakat.setText(formatRupiah(zakatWajib));

            // Periksa Nisab (Standar 85 gram emas)
            if (nominalHarta >= NISAB_DEFAULT) {
                tvStatusNisab.setText("Status: SUDAH MENCAPAI NISAB (Wajib Zakat jika telah mencapai haul 1 tahun).");
            } else {
                tvStatusNisab.setText("Status: Belum mencapai nisab minimal 85 gram emas (~" + formatRupiah(NISAB_DEFAULT) + "). Tetap dianjurkan berinfaq/sedekah.");
            }

            Toast.makeText(this, "Zakat 2.5% berhasil dihitung!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka yang dimasukkan tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Menyimpan hasil kalkulasi ke riwayat Catatan Keuangan (SharedPreferences)
     */
    private void simpanCatatanKeuangan() {
        if (lastZakat <= 0) {
            Toast.makeText(this, "Lakukan perhitungan zakat terlebih dahulu sebelum menyimpan!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String catatanBaru = "[" + tanggal + "] Harta: " + formatRupiah(lastNominal) + " -> Zakat (2.5%): " + formatRupiah(lastZakat) + "\n";

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String historyLama = prefs.getString(KEY_HISTORY, "");
        String historyGabung = catatanBaru + historyLama;

        prefs.edit().putString(KEY_HISTORY, historyGabung).apply();
        tvCatatanHistory.setText(historyGabung);

        Toast.makeText(this, "Perhitungan berhasil dicatat ke keuangan!", Toast.LENGTH_SHORT).show();
    }

    private void loadCatatanHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");
        if (!history.isEmpty()) {
            tvCatatanHistory.setText(history);
        } else {
            tvCatatanHistory.setText("Belum ada riwayat perhitungan yang tersimpan.");
        }
    }

    private void clearHistory() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_HISTORY).apply();
        tvCatatanHistory.setText("Belum ada riwayat perhitungan yang tersimpan.");
        Toast.makeText(this, "Riwayat catatan berhasil dibersihkan!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Memformat angka desimal menjadi format Rupiah Indonesia (cth: Rp 2.500.000)
     */
    private String formatRupiah(double nominal) {
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(nominal);
    }

    // Manajemen daur hidup (lifecycle) AdView demi menghemat memori perangkat
    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}