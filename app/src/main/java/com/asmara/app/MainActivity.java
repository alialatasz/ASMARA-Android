package com.asmara.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private ImageView btnNotifToggle;
    private boolean notifAktif;
    private boolean arCoreDidukung = false;

    // Launcher untuk meminta izin notifikasi (Android 13+)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                aktifkanNotifikasi();
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                    "Izin notifikasi ditolak. Aktifkan di Pengaturan HP untuk pengingat belajar.",
                    Snackbar.LENGTH_LONG).show();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("asmara_prefs", Context.MODE_PRIVATE);
        notifAktif = prefs.getBoolean("notif_aktif", false);

        // Cek apakah HP ini mendukung ARCore
        cekDukunganAR();

        // Navigasi profil lewat header logo
        findViewById(R.id.iv_header_logo).setOnClickListener(v -> {
            Intent profilIntent = new Intent(MainActivity.this, ProfilActivity.class);
            startActivity(profilIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Tombol toggle Notifikasi
        btnNotifToggle = findViewById(R.id.btn_notif_toggle);
        perbaruiIkonNotif();

        btnNotifToggle.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                if (notifAktif) {
                    NotifHelper.batalkanNotifikasi(this);
                    notifAktif = false;
                    prefs.edit().putBoolean("notif_aktif", false).apply();
                    perbaruiIkonNotif();
                    Snackbar.make(findViewById(android.R.id.content),
                        "🔕 Pengingat belajar dinonaktifkan",
                        Snackbar.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.POST_NOTIFICATIONS)
                                == PackageManager.PERMISSION_GRANTED) {
                            aktifkanNotifikasi();
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        }
                    } else {
                        aktifkanNotifikasi();
                    }
                }
            }, 150);
        });

        // =====================================================
        // TOMBOL UTAMA: MASUK DUNIA 3D (dengan animasi berdenyut)
        // =====================================================
        MaterialButton btnMulaiAr = findViewById(R.id.btn_mulai_ar);
        View glowView = findViewById(R.id.glow_dunia3d);

        // Animasi berdenyut terus-menerus pada glow layer
        mulaiAnimasiGlow(glowView);

        // Animasi "shimmer" teks tombol (berkedip halus)
        mulaiAnimasiTombol(btnMulaiAr);

        btnMulaiAr.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(this::tampilkanDialogDunia3D, 150);
        });

        // Tombol Koleksi Sejarah
        MaterialCardView btnKoleksi = findViewById(R.id.btn_koleksi);
        btnKoleksi.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, KoleksiActivity.class);
                startActivity(intent);
            }, 150);
        });

        // Tombol Cara Pakai
        MaterialCardView btnCaraPakai = findViewById(R.id.btn_cara_pakai);
        btnCaraPakai.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, CaraPakaiActivity.class);
                startActivity(intent);
            }, 150);
        });

        // Tombol Uji Pemahaman (Kuis)
        MaterialCardView btnKuis = findViewById(R.id.btn_kuis);
        if (btnKuis != null) {
            btnKuis.setOnClickListener(v -> {
                animateButton(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(MainActivity.this, KuisActivity.class);
                    startActivity(intent);
                }, 150);
            });
        }

        // Buat channel notifikasi sejak awal (wajib untuk Android 8+)
        NotificationReceiver.createNotificationChannel(this);
    }

    /**
     * Mengecek apakah perangkat ini mendukung ARCore.
     */
    private void cekDukunganAR() {
        try {
            ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
            if (availability.isTransient()) {
                // Masih mengecek, coba lagi setelah 200ms
                new android.os.Handler().postDelayed(this::cekDukunganAR, 200);
            } else {
                arCoreDidukung = availability.isSupported();
            }
        } catch (Exception e) {
            arCoreDidukung = false;
        }
    }

    /**
     * Menampilkan pop-up pilihan Mode AR atau Mode Layar 3D.
     */
    private void tampilkanDialogDunia3D() {
        // Membuat layout dialog secara programmatic
        LinearLayout containerUtama = new LinearLayout(this);
        containerUtama.setOrientation(LinearLayout.VERTICAL);
        containerUtama.setPadding(60, 50, 60, 40);
        containerUtama.setGravity(Gravity.CENTER_HORIZONTAL);

        // Judul
        TextView tvJudul = new TextView(this);
        tvJudul.setText("🌐 Masuk Dunia 3D");
        tvJudul.setTextSize(22);
        tvJudul.setTextColor(0xFF311B92);
        tvJudul.setTypeface(null, android.graphics.Typeface.BOLD);
        tvJudul.setGravity(Gravity.CENTER);
        containerUtama.addView(tvJudul);

        // Subjudul
        TextView tvSub = new TextView(this);
        tvSub.setText("Pilih cara kamu melihat Gedung Papak:");
        tvSub.setTextSize(14);
        tvSub.setTextColor(0xFF666666);
        tvSub.setGravity(Gravity.CENTER);
        tvSub.setPadding(0, 12, 0, 30);
        containerUtama.addView(tvSub);

        // =====================
        // TOMBOL 1: Mode AR (Kamera)
        // =====================
        MaterialButton btnAR = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        btnAR.setText(arCoreDidukung ? "📸  Mode AR (Kamera)" : "📸  Mode AR (Tidak Didukung)");
        btnAR.setTextSize(15);
        btnAR.setAllCaps(false);
        btnAR.setCornerRadius(40);
        btnAR.setPadding(0, 30, 0, 30);
        btnAR.setEnabled(arCoreDidukung);

        if (arCoreDidukung) {
            btnAR.setBackgroundColor(0xFF6200EA);
            btnAR.setTextColor(0xFFFFFFFF);
        } else {
            btnAR.setBackgroundColor(0xFFBDBDBD);
            btnAR.setTextColor(0xFF9E9E9E);
        }

        LinearLayout.LayoutParams paramsTombol = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTombol.bottomMargin = 20;
        btnAR.setLayoutParams(paramsTombol);
        containerUtama.addView(btnAR);

        // Keterangan Mode AR
        TextView tvKetAR = new TextView(this);
        tvKetAR.setText(arCoreDidukung ? "Letakkan gedung 3D di lantai kamarmu melalui kamera!"
                : "⚠️ HP kamu belum mendukung fitur kamera AR.");
        tvKetAR.setTextSize(11);
        tvKetAR.setTextColor(arCoreDidukung ? 0xFF888888 : 0xFFE53935);
        tvKetAR.setGravity(Gravity.CENTER);
        tvKetAR.setPadding(0, 0, 0, 30);
        containerUtama.addView(tvKetAR);

        // =====================
        // TOMBOL 2: Mode Layar 3D (Virtual)
        // =====================
        MaterialButton btnVirtual = new MaterialButton(this);
        btnVirtual.setText("🖥️  Mode Layar 3D (Virtual)");
        btnVirtual.setTextSize(15);
        btnVirtual.setAllCaps(false);
        btnVirtual.setCornerRadius(40);
        btnVirtual.setPadding(0, 30, 0, 30);
        btnVirtual.setBackgroundColor(0xFF00BFA5);
        btnVirtual.setTextColor(0xFFFFFFFF);

        LinearLayout.LayoutParams paramsTombol2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTombol2.bottomMargin = 20;
        btnVirtual.setLayoutParams(paramsTombol2);
        containerUtama.addView(btnVirtual);

        // Keterangan Mode Virtual
        TextView tvKetVirtual = new TextView(this);
        tvKetVirtual.setText("Lihat gedung 3D di layar hitam. Putar dengan jari! Semua HP bisa!");
        tvKetVirtual.setTextSize(11);
        tvKetVirtual.setTextColor(0xFF888888);
        tvKetVirtual.setGravity(Gravity.CENTER);
        containerUtama.addView(tvKetVirtual);

        // Bangun Dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(containerUtama)
                .setCancelable(true)
                .create();

        // Set aksi tombol
        btnAR.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnVirtual.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, Viewer3DActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.GradientDrawable() {{
                setCornerRadius(60);
                setColor(0xFFFFFFFF);
            }});
        }

        dialog.show();
    }

    /**
     * Animasi glow berdenyut terus-menerus di belakang tombol "Masuk Dunia 3D".
     */
    private void mulaiAnimasiGlow(View glowView) {
        AnimationSet animSet = new AnimationSet(true);

        // Skala membesar-mengecil
        ScaleAnimation scale = new ScaleAnimation(
            1f, 1.08f, 1f, 1.08f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1200);
        scale.setRepeatCount(Animation.INFINITE);
        scale.setRepeatMode(Animation.REVERSE);

        // Kedip halus (opacity)
        AlphaAnimation alpha = new AlphaAnimation(0.4f, 1.0f);
        alpha.setDuration(1200);
        alpha.setRepeatCount(Animation.INFINITE);
        alpha.setRepeatMode(Animation.REVERSE);

        animSet.addAnimation(scale);
        animSet.addAnimation(alpha);
        glowView.startAnimation(animSet);
    }

    /**
     * Animasi halus pada tombol utama agar terlihat "hidup" dan mengundang klik.
     */
    private void mulaiAnimasiTombol(View btn) {
        ScaleAnimation pulse = new ScaleAnimation(
            1f, 1.03f, 1f, 1.03f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(800);
        pulse.setRepeatCount(Animation.INFINITE);
        pulse.setRepeatMode(Animation.REVERSE);
        btn.startAnimation(pulse);
    }

    private void aktifkanNotifikasi() {
        NotifHelper.jadwalkanNotifikasi(this);
        notifAktif = true;
        prefs.edit().putBoolean("notif_aktif", true).apply();
        perbaruiIkonNotif();
        Snackbar.make(findViewById(android.R.id.content),
            "🔔 Pengingat belajar aktif! Notifikasi akan muncul setiap pukul 16.00",
            Snackbar.LENGTH_LONG).show();
    }

    private void perbaruiIkonNotif() {
        if (notifAktif) {
            btnNotifToggle.setImageResource(R.drawable.ic_notif_on);
        } else {
            btnNotifToggle.setImageResource(R.drawable.ic_notif_off);
        }
    }

    /**
     * Memberikan animasi "tekan" pada tombol untuk feedback visual.
     */
    private void animateButton(View view) {
        ScaleAnimation scale = new ScaleAnimation(
            1f, 0.95f, 1f, 0.95f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(100);
        scale.setRepeatCount(1);
        scale.setRepeatMode(Animation.REVERSE);
        view.startAnimation(scale);
    }
}

