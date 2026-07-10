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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.animation.OvershootInterpolator;
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
import android.widget.FrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.io.File;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.ArCoreApk;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

    // Launcher untuk meminta izin kamera (untuk QR Scanner)
    private final ActivityResultLauncher<String> requestCameraLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                mulaiScanQR();
            } else {
                Toast.makeText(this, "Izin kamera diperlukan untuk memindai QR Code.", Toast.LENGTH_LONG).show();
            }
        });

    // Launcher untuk hasil scan QR
    private final ActivityResultLauncher<ScanOptions> qrScanLauncher =
        registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                prosesHasilScanQR(result.getContents());
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
        findViewById(R.id.fl_header_avatar).setOnClickListener(v -> {
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
        // TOMBOL 1: SCAN QR
        // =====================================================
        FrameLayout btnScanQR = findViewById(R.id.btn_scan_qr);
        btnScanQR.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                // Cek izin kamera sebelum membuka scanner
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    mulaiScanQR();
                } else {
                    requestCameraLauncher.launch(Manifest.permission.CAMERA);
                }
            }, 150);
        });

        // =====================================================
        // TOMBOL 2: PETUALANGAN ASMARA (Dunia 3D)
        // =====================================================
        FrameLayout btnPetualangan = findViewById(R.id.btn_petualangan);
        btnPetualangan.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(this::tampilkanDialogDunia3D, 150);
        });

        // =====================================================
        // TOMBOL 3: KOLEKSI SEJARAH
        // =====================================================
        FrameLayout btnKoleksi = findViewById(R.id.btn_koleksi);
        btnKoleksi.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, KoleksiActivity.class);
                startActivity(intent);
            }, 150);
        });

        // =====================================================
        // TOMBOL 4: PANDUAN PENGGUNAAN
        // =====================================================
        FrameLayout btnCaraPakai = findViewById(R.id.btn_cara_pakai);
        btnCaraPakai.setOnClickListener(v -> {
            animateButton(v);
            v.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, CaraPakaiActivity.class);
                startActivity(intent);
            }, 150);
        });

        // =====================================================
        // TOMBOL 5: MATERI (Baru)
        // =====================================================
        FrameLayout btnMateri = findViewById(R.id.btn_materi);
        if (btnMateri != null) {
            btnMateri.setOnClickListener(v -> {
                animateButton(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(MainActivity.this, MateriListActivity.class);
                    startActivity(intent);
                }, 150);
            });
        }

        // =====================================================
        // TOMBOL 6: KUIS
        // =====================================================
        FrameLayout btnKuis = findViewById(R.id.btn_kuis);
        if (btnKuis != null) {
            btnKuis.setOnClickListener(v -> {
                animateButton(v);
                v.postDelayed(() -> {
                    Intent intent = new Intent(MainActivity.this, KuisMasukActivity.class);
                    startActivity(intent);
                }, 150);
            });
        }

        // ------------------ ANIMASI MASKOT ------------------
        ImageView ivMaskot = findViewById(R.id.iv_maskot);
        if (ivMaskot != null) {
            ivMaskot.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MascotDialogActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            
            // Persiapan Animasi Idle (Pulse/Berdenyut)
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ivMaskot, "scaleX", 1.0f, 1.06f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ivMaskot, "scaleY", 1.0f, 1.06f);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setDuration(1000); // 1 detik membesar, 1 detik mengecil
            scaleDownY.setDuration(1000);

            AnimatorSet pulseSet = new AnimatorSet();
            pulseSet.playTogether(scaleDownX, scaleDownY);

            // Persiapan Animasi Masuk (Pop-up & Bounce)
            // Sembunyikan dulu di bawah layar
            ivMaskot.setTranslationY(300f);
            ivMaskot.setScaleX(0.5f);
            ivMaskot.setScaleY(0.5f);
            ivMaskot.setAlpha(0f);

            // Jalankan animasi masuk
            ivMaskot.animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(300) // Tunggu sebentar setelah halaman terbuka
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .withEndAction(() -> {
                        // Setelah selesai melompat masuk, mulai animasi idle/berdenyut
                        pulseSet.start();
                    })
                    .start();
        }
        // ----------------------------------------------------

        // Buat channel notifikasi sejak awal (wajib untuk Android 8+)
        NotificationReceiver.createNotificationChannel(this);
    }

    /**
     * Membuka layar pemindai QR Code menggunakan ZXing.
     */
    private void mulaiScanQR() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Arahkan kamera ke QR Code pada smartcard atau miniatur Gedung Papak");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CustomScannerActivity.class);
        qrScanLauncher.launch(options);
    }

    /**
     * Memproses hasil scan QR Code dan mengarahkan ke halaman yang sesuai.
     * Format QR yang didukung:
     *   - QR_BALOK → Membuka Viewer 3D bentuk Balok
     *   - QR_KUBUS → Membuka Viewer 3D bentuk Kubus
     *   - QR_LIMAS → Membuka Viewer 3D bentuk Limas
     *   - QR_PRISMA → Membuka Viewer 3D bentuk Prisma
     *   - QR_TABUNG → Membuka Viewer 3D bentuk Tabung
     *   - QR_PERSEGI_PANJANG → Membuka Viewer 3D bentuk Persegi Panjang
     *   - QR_GEDUNG → Membuka Viewer 3D Gedung Papak (utuh)
     */
    private void prosesHasilScanQR(String hasilScan) {
        String kode = hasilScan.trim().toUpperCase();

        String tipeBangunRuang = null;
        String namaBangunRuang = null;
        String bagianGedung = null;

        switch (kode) {
            case "QR_BALOK":
                tipeBangunRuang = "BALOK";
                namaBangunRuang = "Balok";
                bagianGedung = "Tiang Gedung Papak";
                break;
            case "QR_KUBUS":
                tipeBangunRuang = "KUBUS";
                namaBangunRuang = "Kubus";
                bagianGedung = "Dasar Tiang Gedung Papak";
                break;
            case "QR_LIMAS":
                tipeBangunRuang = "LIMAS";
                namaBangunRuang = "Limas Segi Empat";
                bagianGedung = "Atap Gedung Papak";
                break;
            case "QR_PRISMA":
                tipeBangunRuang = "PRISMA";
                namaBangunRuang = "Prisma Segitiga";
                bagianGedung = "Atap Samping Gedung Papak";
                break;
            case "QR_TABUNG":
                tipeBangunRuang = "TABUNG";
                namaBangunRuang = "Tabung";
                bagianGedung = "Pilar Gedung Papak";
                break;
            case "QR_PERSEGI_PANJANG":
                tipeBangunRuang = "PERSEGI_PANJANG";
                namaBangunRuang = "Persegi Panjang";
                bagianGedung = "Jendela Gedung Papak";
                break;
            case "QR_GEDUNG":
            case "ASMARA_GEDUNG_PAPAK_AR":
                tipeBangunRuang = "GEDUNG";
                namaBangunRuang = "Gedung Papak";
                bagianGedung = "Gedung Papak (Utuh)";
                break;
            default:
                // QR tidak dikenali
                Toast.makeText(this,
                    "QR Code tidak dikenali: \"" + hasilScan + "\"\nGunakan QR Code dari smartcard ASMARA.",
                    Toast.LENGTH_LONG).show();
                return;
        }

        // Buka MateriARActivity dengan data bangun ruang (fitur AR baru)
        Intent intent = new Intent(this, MateriARActivity.class);
        intent.putExtra("TIPE_BANGUN", tipeBangunRuang);
        intent.putExtra("NAMA_BANGUN", namaBangunRuang);
        intent.putExtra("BAGIAN_GEDUNG", bagianGedung);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Mengecek apakah perangkat ini mendukung ARCore.
     */
    @Override
    protected void onResume() {
        super.onResume();
        perbaruiAvatarHeader();
    }
    
    private void perbaruiAvatarHeader() {
        TextView tvHeaderAvatar = findViewById(R.id.tv_header_avatar);
        ShapeableImageView ivHeaderAvatar = findViewById(R.id.iv_header_avatar);
        FrameLayout flHeaderAvatar = findViewById(R.id.fl_header_avatar);
        
        if (tvHeaderAvatar == null || ivHeaderAvatar == null || flHeaderAvatar == null) return;
        
        boolean useCustomPhoto = prefs.getBoolean("use_custom_photo", false);
        int currentAvatar = prefs.getInt("avatar_index", 0);
        
        // Update Nama (Ditaruh di atas agar tidak kena return early custom photo)
        TextView tvHeaderName = findViewById(R.id.tv_header_name);
        if (tvHeaderName != null) {
            String namaAnak = prefs.getString("nama_anak", "");
            if (namaAnak != null && !namaAnak.trim().isEmpty()) {
                // Gunakan kata pertama saja agar tidak terlalu panjang
                String namaPanggilan = namaAnak.trim().split(" ")[0];
                tvHeaderName.setText("Halo, " + namaPanggilan);
            } else {
                tvHeaderName.setText("ASMARA");
            }
        }

        if (useCustomPhoto) {
            File f = new File(getFilesDir(), "avatar.jpg");
            if (f.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                ivHeaderAvatar.setImageBitmap(bmp);
                ivHeaderAvatar.setVisibility(View.VISIBLE);
                tvHeaderAvatar.setVisibility(View.GONE);
                flHeaderAvatar.setBackgroundResource(R.drawable.circle_white);
                flHeaderAvatar.setBackgroundTintList(null);
                return;
            }
        }
        
        String[] AVATAR_EMOJI = {"🦊", "🐻", "🦁", "🐸", "🦋", "🐬", "🦅", "🐼"};
        int[] AVATAR_COLORS  = {
            0xFFFF8F00, 0xFF5D4037, 0xFFEF6C00, 0xFF2E7D32,
            0xFF7B1FA2, 0xFF0277BD, 0xFF1565C0, 0xFF37474F
        };
        
        ivHeaderAvatar.setVisibility(View.GONE);
        tvHeaderAvatar.setVisibility(View.VISIBLE);
        tvHeaderAvatar.setText(AVATAR_EMOJI[currentAvatar]);
        flHeaderAvatar.setBackgroundResource(R.drawable.circle_white);
        flHeaderAvatar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(AVATAR_COLORS[currentAvatar]));
        
    }

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
        tvJudul.setText("🌐 Petualangan ASMARA");
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
                : "⚠️ HP kamu belum mendukung fitur kamera AR.\nGunakan Mode Layar 3D sebagai gantinya.");
        tvKetAR.setTextSize(11);
        tvKetAR.setTextColor(arCoreDidukung ? 0xFF888888 : 0xFFE53935);
        tvKetAR.setGravity(Gravity.CENTER);
        tvKetAR.setPadding(0, 0, 0, 30);
        containerUtama.addView(tvKetAR);

        // =====================
        // TOMBOL 2: Mode Layar 3D (Virtual) — KOMPATIBEL SEMUA HP
        // =====================
        MaterialButton btnVirtual = new MaterialButton(this);
        btnVirtual.setText("🖥️  Mode Layar 3D (Semua HP)");
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
        tvKetVirtual.setText("Lihat gedung 3D di layar. Putar dengan jari! Semua HP bisa!");
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
            Intent intent = new Intent(MainActivity.this, MateriARActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnVirtual.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(MainActivity.this, Viewer3DActivity.class);
            intent.putExtra("TIPE_BANGUN", "GEDUNG");
            intent.putExtra("NAMA_BANGUN", "Gedung Papak");
            intent.putExtra("BAGIAN_GEDUNG", "Gedung Papak (Utuh)");
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
