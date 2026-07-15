package com.asmara.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.widget.FrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.Objects;
public class ProfilActivity extends AppCompatActivity {

    // Data avatar: emoji + warna latar
    private static final String[] AVATAR_EMOJI = {"🦊", "🐻", "🦁", "🐸", "🦋", "🐬", "🦅", "🐼"};
    private static final int[] AVATAR_COLORS  = {
        0xFFFF8F00, 0xFF5D4037, 0xFFEF6C00, 0xFF2E7D32,
        0xFF7B1FA2, 0xFF0277BD, 0xFF1565C0, 0xFF37474F
    };

    private SharedPreferences prefs;
    private int currentAvatar;
    private TextView tvAvatar;
    private android.widget.FrameLayout flAvatar;
    private TextInputEditText etNama;
    private android.widget.ProgressBar pbXp;
    private TextView tvLevelName, tvXpText, tvDisplayNama;
    private ShapeableImageView ivAvatarPhoto;
    private ActivityResultLauncher<Intent> photoPickerLauncher;

    // Helper statis untuk menyimpan dan membaca data profil
    public static void simpanHasilKuis(Context ctx, int skor, int benar) {
        SharedPreferences p = ctx.getSharedPreferences("asmara_prefs", Context.MODE_PRIVATE);
        int kuisSelesai = p.getInt("kuis_selesai", 0) + 1;
        int skorTerbaik = Math.max(p.getInt("skor_terbaik", 0), skor);

        int bintangBaru = skor >= 90 ? 3 : skor >= 70 ? 2 : skor >= 50 ? 1 : 0;
        int totalBintang = p.getInt("total_bintang", 0) + bintangBaru;
        
        int totalXp = p.getInt("total_xp", 0) + skor;

        p.edit()
            .putInt("kuis_selesai", kuisSelesai)
            .putInt("skor_terbaik", skorTerbaik)
            .putInt("total_bintang", totalBintang)
            .putInt("total_xp", totalXp)
            .apply();
            
        // Sinkronisasi otomatis ke Firebase
        FirebaseManager.backupDataProfil(ctx);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        prefs = getSharedPreferences("asmara_prefs", Context.MODE_PRIVATE);

        tvAvatar   = findViewById(R.id.tv_avatar);
        flAvatar   = findViewById(R.id.fl_avatar);
        etNama     = findViewById(R.id.et_nama);
        pbXp       = findViewById(R.id.pb_xp);
        tvLevelName= findViewById(R.id.tv_level_name);
        tvXpText   = findViewById(R.id.tv_xp_text);
        tvDisplayNama = findViewById(R.id.tv_display_nama);
        ivAvatarPhoto = findViewById(R.id.iv_avatar_photo);

        photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            InputStream is = getContentResolver().openInputStream(selectedImageUri);
                            File f = new File(getFilesDir(), "avatar.jpg");
                            FileOutputStream fos = new FileOutputStream(f);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                            fos.close();
                            is.close();
                            
                            prefs.edit().putBoolean("use_custom_photo", true).apply();
                            perbaruiAvatar();
                            
                            Toast.makeText(this, "Foto profil berhasil diubah!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Gagal memuat foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );

        // Cek Bonus Login Harian (+50 XP)
        cekBonusHarian();

        // Muat data tersimpan
        currentAvatar = prefs.getInt("avatar_index", 0);
        String namaTersimpan = prefs.getString("nama_anak", "");
        etNama.setText(namaTersimpan);
        if (!namaTersimpan.isEmpty()) {
            tvDisplayNama.setText("Halo, " + namaTersimpan + "!");
        }
        
        perbaruiAvatar();
        muatStatistik();
        muatBadge();

        // Tombol kembali
        FloatingActionButton btnBack = findViewById(R.id.btn_back_profil);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Tombol ganti avatar — tampilkan dialog pilih avatar
        FloatingActionButton btnGantiAvatar = findViewById(R.id.btn_ganti_avatar);
        btnGantiAvatar.setOnClickListener(v -> tampilkanDialogAvatar());

        // Simpan nama saat tekan tombol
        MaterialButton btnSimpanNama = findViewById(R.id.btn_simpan_nama);
        btnSimpanNama.setOnClickListener(v -> simpanNama());

        // Simpan nama saat tekan Done di keyboard
        etNama.setOnEditorActionListener((tv, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                simpanNama();
                return true;
            }
            return false;
        });

        // Tombol reset
        MaterialButton btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Reset Progress?")
                .setMessage("Semua statistik dan lencana kamu akan direset ke nol. Nama dan avatar tetap tersimpan. Yakin?")
                .setPositiveButton("Ya, Re  set", (dialog, which) -> {
                    prefs.edit()
                        .putInt("kuis_selesai", 0)
                        .putInt("skor_terbaik", 0)
                        .putInt("total_bintang", 0)
                        .putInt("total_xp", 0)
                        .putString("terakhir_login", "")
                        .apply();
                    FirebaseManager.backupDataProfil(ProfilActivity.this);
                    muatStatistik();
                    muatBadge();
                    Toast.makeText(this, "Progress berhasil direset", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
        });
    }

    private void simpanNama() {
        String nama = Objects.requireNonNull(etNama.getText()).toString().trim();
        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong!");
            return;
        }
        prefs.edit().putString("nama_anak", nama).apply();
        tvDisplayNama.setText("Halo, " + nama + "!");
        
        // Sinkronisasi ke Firebase
        FirebaseManager.backupDataProfil(this);
        
        // Sembunyikan keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etNama.getWindowToken(), 0);
        Toast.makeText(this, "Nama berhasil disimpan!", Toast.LENGTH_SHORT).show();
    }

    private void tampilkanDialogAvatar() {
        // Nama-nama lucu untuk setiap avatar
        final String[] AVATAR_NAMES = {
            "Rubah Cerdik", "Beruang Kuat", "Singa Pemberani", "Katak Lincah",
            "Kupu-kupu Indah", "Lumba-lumba Ramah", "Elang Gagah", "Panda Gemas"
        };
        
        android.widget.ListAdapter adapter = new android.widget.ArrayAdapter<String>(
                this, R.layout.item_avatar_dialog, AVATAR_NAMES) {
            @androidx.annotation.NonNull
            @Override
            public View getView(int position, @androidx.annotation.Nullable View convertView, @androidx.annotation.NonNull android.view.ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_avatar_dialog, parent, false);
                }
                
                ImageView icon = convertView.findViewById(R.id.iv_avatar_icon);
                TextView emoji = convertView.findViewById(R.id.tv_avatar_emoji);
                TextView name = convertView.findViewById(R.id.tv_avatar_name);
                
                if (position == 0) {
                    icon.setVisibility(View.VISIBLE);
                    emoji.setVisibility(View.GONE);
                    name.setText("Pilih dari Galeri HP");
                } else {
                    icon.setVisibility(View.GONE);
                    emoji.setVisibility(View.VISIBLE);
                    emoji.setText(AVATAR_EMOJI[position - 1]);
                    name.setText(AVATAR_NAMES[position - 1]);
                }
                return convertView;
            }
            @Override
            public int getCount() {
                return AVATAR_NAMES.length + 1;
            }
        };
        
        new AlertDialog.Builder(this)
            .setTitle("Pilih Avatarmu!")
            .setAdapter(adapter, (dialog, which) -> {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    photoPickerLauncher.launch(intent);
                } else {
                    currentAvatar = which - 1;
                    prefs.edit()
                        .putInt("avatar_index", currentAvatar)
                        .putBoolean("use_custom_photo", false)
                        .apply();
                    perbaruiAvatar();
                    FirebaseManager.backupDataProfil(ProfilActivity.this);
                }
            })
            .show();
    }

    private void perbaruiAvatar() {
        boolean useCustomPhoto = prefs.getBoolean("use_custom_photo", false);
        if (useCustomPhoto) {
            File f = new File(getFilesDir(), "avatar.jpg");
            if (f.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                ivAvatarPhoto.setImageBitmap(bmp);
                ivAvatarPhoto.setVisibility(View.VISIBLE);
                tvAvatar.setVisibility(View.GONE);
                flAvatar.setBackgroundResource(R.drawable.circle_white);
                flAvatar.setBackgroundTintList(null);
                return;
            }
        }
        
        ivAvatarPhoto.setVisibility(View.GONE);
        tvAvatar.setVisibility(View.VISIBLE);
        tvAvatar.setText(AVATAR_EMOJI[currentAvatar]);
        flAvatar.setBackgroundResource(R.drawable.circle_white);
        flAvatar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(AVATAR_COLORS[currentAvatar]));
    }

    private void cekBonusHarian() {
        String hariIni = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(new java.util.Date());
        String loginTerakhir = prefs.getString("terakhir_login", "");
        
        if (!hariIni.equals(loginTerakhir)) {
            int xpSekarang = prefs.getInt("total_xp", 0);
            prefs.edit()
                .putInt("total_xp", xpSekarang + 50)
                .putString("terakhir_login", hariIni)
                .apply();
            FirebaseManager.backupDataProfil(this);
            Toast.makeText(this, "🎁 Bonus Login Harian: +50 XP!", Toast.LENGTH_SHORT).show();
        }
    }

    private void muatStatistik() {
        int totalBintang = prefs.getInt("total_bintang", 0);
        int kuisSelesai  = prefs.getInt("kuis_selesai", 0);
        int skorTerbaik  = prefs.getInt("skor_terbaik", 0);

        ((TextView) findViewById(R.id.tv_total_bintang)).setText(String.valueOf(totalBintang));
        ((TextView) findViewById(R.id.tv_kuis_selesai)).setText(String.valueOf(kuisSelesai));
        ((TextView) findViewById(R.id.tv_skor_terbaik)).setText(String.valueOf(skorTerbaik));
        
        muatXP();
    }

    private void muatXP() {
        int xp = prefs.getInt("total_xp", 0);
        
        // Kalkulasi Level
        String namaLevel = "Penjelajah Pemula";
        int iconResId = R.drawable.ic_level_pemula;
        int xpDasar = 0;
        int xpMax = 100;

        if (xp >= 1000) {
            namaLevel = "Maestro Sejarah";
            iconResId = R.drawable.ic_level_maestro;
            xpDasar = 1000;
            xpMax = xp; // Maxed out
        } else if (xp >= 600) {
            namaLevel = "Pakar Sejarah";
            iconResId = R.drawable.ic_level_pakar;
            xpDasar = 600;
            xpMax = 1000;
        } else if (xp >= 300) {
            namaLevel = "Arkeolog Muda";
            iconResId = R.drawable.ic_level_arkeolog;
            xpDasar = 300;
            xpMax = 600;
        } else if (xp >= 100) {
            namaLevel = "Pengamat Cilik";
            iconResId = R.drawable.ic_level_pengamat;
            xpDasar = 100;
            xpMax = 300;
        }

        tvLevelName.setText(namaLevel);
        tvLevelName.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        
        if (xp >= 1000) {
            pbXp.setMax(100);
            pbXp.setProgress(100);
            tvXpText.setText(xp + " XP (Level Maksimal)");
        } else {
            pbXp.setMax(xpMax - xpDasar);
            pbXp.setProgress(xp - xpDasar);
            tvXpText.setText(xp + " / " + xpMax + " XP");
        }
    }

    private void muatBadge() {
        int kuisSelesai = prefs.getInt("kuis_selesai", 0);
        int skorTerbaik = prefs.getInt("skor_terbaik", 0);

        // Badge 1: Penjelajah Pertama — selesaikan 1 kuis
        setBadgeState(R.id.badge_1, kuisSelesai >= 1);
        // Badge 2: Sang Juara — skor >= 90
        setBadgeState(R.id.badge_2, skorTerbaik >= 90);
        // Badge 3: Rajin Belajar — selesaikan 3 kuis
        setBadgeState(R.id.badge_3, kuisSelesai >= 3);
        // Badge 4: Maestro Sejarah — selesaikan 5 kuis
        setBadgeState(R.id.badge_4, kuisSelesai >= 5);
    }

    /**
     * Jika badge sudah unlock: warna normal + tampil terang.
     * Jika belum: card menjadi abu-abu dan emoji tersamarkan.
     */
    private void setBadgeState(int cardId, boolean unlocked) {
        FrameLayout card = findViewById(cardId);
        if (card == null) return;
        if (unlocked) {
            card.setBackgroundResource(R.drawable.bg_composite_16_white);
            card.setAlpha(1.0f);
            card.setElevation(0f);
        } else {
            card.setBackgroundResource(R.drawable.bg_composite_16_gray);
            card.setAlpha(0.45f);
            card.setElevation(0f);
        }
    }
}
