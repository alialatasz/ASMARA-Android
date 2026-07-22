package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HasilKuisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_kuis);

        // Get data from Intent
        int skor = getIntent().getIntExtra("SKOR", 0);
        int benar = getIntent().getIntExtra("BENAR", 0);
        int salah = getIntent().getIntExtra("SALAH", 0);
        int total = getIntent().getIntExtra("TOTAL", 25);
        String namaSiswa = getIntent().getStringExtra("NAMA_SISWA");
        String kelasSiswa = getIntent().getStringExtra("KELAS_SISWA");

        if (namaSiswa == null) namaSiswa = "Siswa";
        if (kelasSiswa == null) kelasSiswa = "-";

        // Simpan hasil kuis ke profil anak (lokal)
        ProfilActivity.simpanHasilKuis(this, skor, benar);

        // Simpan hasil kuis ke Firebase (untuk database guru)
        simpanKeFirebase(namaSiswa, kelasSiswa, skor, benar, salah, total);

        TextView tvScoreNumber = findViewById(R.id.tv_score_number);
        TextView tvCorrect = findViewById(R.id.tv_correct);
        TextView tvWrong = findViewById(R.id.tv_wrong);
        TextView tvGradeTitle = findViewById(R.id.tv_grade_title);
        ImageView imgGradeIcon = findViewById(R.id.img_grade_icon);
        
        TextView star1 = findViewById(R.id.star1);
        TextView star2 = findViewById(R.id.star2);
        TextView star3 = findViewById(R.id.star3);

        tvScoreNumber.setText(String.valueOf(skor));
        tvCorrect.setText(benar + " Benar");
        tvWrong.setText(salah + " Salah");

        // Logic for Grade
        if (skor >= 90) {
            imgGradeIcon.setImageResource(R.drawable.ic_hasil_luar_biasa);
            tvGradeTitle.setText("LUAR BIASA!");
            tvGradeTitle.setTextColor(getColor(R.color.accent_orange));
            star1.setAlpha(1.0f); star2.setAlpha(1.0f); star3.setAlpha(1.0f);
        } else if (skor >= 70) {
            imgGradeIcon.setImageResource(R.drawable.ic_hasil_hebat);
            tvGradeTitle.setText("HEBAT!");
            tvGradeTitle.setTextColor(getColor(R.color.success));
            star1.setAlpha(1.0f); star2.setAlpha(1.0f); star3.setAlpha(0.3f);
        } else if (skor >= 50) {
            imgGradeIcon.setImageResource(R.drawable.ic_hasil_bagus);
            tvGradeTitle.setText("BAGUS!");
            tvGradeTitle.setTextColor(getColor(R.color.primary));
            star1.setAlpha(1.0f); star2.setAlpha(0.3f); star3.setAlpha(0.3f);
        } else {
            imgGradeIcon.setImageResource(R.drawable.ic_hasil_semangat);
            tvGradeTitle.setText("TETAP SEMANGAT!");
            tvGradeTitle.setTextColor(getColor(R.color.danger));
            star1.setAlpha(0.3f); star2.setAlpha(0.3f); star3.setAlpha(0.3f);
        }

        // Tembakkan Konfeti!
        ConfettiView confettiView = findViewById(R.id.confetti_view);
        if (confettiView != null && skor >= 70) {
            confettiView.startConfetti();
        }

        // Mainkan SFX kuis selesai
        android.media.MediaPlayer sfxPlayer = android.media.MediaPlayer.create(this, R.raw.quiz_selesai);
        if (sfxPlayer != null) {
            sfxPlayer.start();
            sfxPlayer.setOnCompletionListener(android.media.MediaPlayer::release);
        }

        // Tombol Ulangi Kuis
        android.view.View btnUlangi = findViewById(R.id.btn_ulangi_kuis);
        if (btnUlangi != null) {
            btnUlangi.setOnClickListener(v -> {
                SoundHelper.playClick();
                Intent intent = new Intent(HasilKuisActivity.this, KuisMasukActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        // Tombol Kembali ke Home
        android.view.View btnHome = findViewById(R.id.btn_kembali_home);
        btnHome.setOnClickListener(v -> {
            SoundHelper.playClick();
            Intent intent = new Intent(HasilKuisActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Menyimpan hasil kuis siswa ke Firebase Realtime Database
     * agar guru bisa melihat progres siswa.
     */
    private void simpanKeFirebase(String nama, String kelas, int skor, int benar, int salah, int total) {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance(
                "https://asmara-44eec-default-rtdb.asia-southeast1.firebasedatabase.app"
            ).getReference("hasil_kuis");

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            
            // Format ID khusus untuk murid agar jadi folder: KELAS_NAMA (contoh: 1A_Andi)
            // Menghapus karakter aneh dan spasi agar aman dijadikan key Firebase
            String safeNama = nama.trim().replaceAll("[^a-zA-Z0-9 ]", "").replaceAll("\\s+", "_");
            String safeKelas = kelas.trim().replaceAll("[^a-zA-Z0-9 ]", "").replaceAll("\\s+", "_");
            String key = safeKelas + "_" + safeNama;

            Map<String, Object> data = new HashMap<>();
            data.put("nama", nama);
            data.put("kelas", kelas);
            data.put("skor", skor);
            data.put("benar", benar);
            data.put("salah", salah);
            data.put("total_soal", total);
            data.put("waktu", timestamp);

            // Jika key berhasil dibuat, akan melakukan update. Jika tidak (kosong), buat ID acak.
            if (!key.isEmpty() && !key.equals("_")) {
                ref.child(key).setValue(data);
            } else {
                ref.push().setValue(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
