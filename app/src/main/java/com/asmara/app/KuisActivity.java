package com.asmara.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class KuisActivity extends AppCompatActivity {

    private List<Soal> soalList;
    private int currentSoalIndex = 0;
    
    // State Tracking
    private int[] jawabanUser; // -1 if not answered
    private boolean[] isTimeout; // true if time ran out
    
    private TextView tvProgress, tvPertanyaan, tvTimer;
    private ProgressBar pbKuis;
    private ImageView ivKuisImage;
    private MaterialButton btnOpsiA, btnOpsiB, btnOpsiC;
    private MaterialButton btnPrev, btnNext;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 40000; // 40 detik per soal
    private final long TIMER_DURATION = 40000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kuis);

        tvProgress = findViewById(R.id.tv_progress);
        tvPertanyaan = findViewById(R.id.tv_pertanyaan);
        tvTimer = findViewById(R.id.tv_timer);
        pbKuis = findViewById(R.id.pb_kuis);
        ivKuisImage = findViewById(R.id.iv_kuis_image);
        btnOpsiA = findViewById(R.id.btn_opsi_a);
        btnOpsiB = findViewById(R.id.btn_opsi_b);
        btnOpsiC = findViewById(R.id.btn_opsi_c);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);

        FloatingActionButton btnBack = findViewById(R.id.btn_back_kuis);
        btnBack.setOnClickListener(v -> finish());

        loadDataSoal();
        pbKuis.setMax(soalList.size());
        
        jawabanUser = new int[soalList.size()];
        isTimeout = new boolean[soalList.size()];
        for(int i=0; i<soalList.size(); i++) {
            jawabanUser[i] = -1;
            isTimeout[i] = false;
        }

        tampilkanSoal();

        btnOpsiA.setOnClickListener(v -> animateButtonAndCheck(v, 0));
        btnOpsiB.setOnClickListener(v -> animateButtonAndCheck(v, 1));
        btnOpsiC.setOnClickListener(v -> animateButtonAndCheck(v, 2));
        
        btnPrev.setOnClickListener(v -> {
            if (currentSoalIndex > 0) {
                currentSoalIndex--;
                tampilkanSoal();
            }
        });
        
        btnNext.setOnClickListener(v -> {
            if (currentSoalIndex < soalList.size() - 1) {
                currentSoalIndex++;
                tampilkanSoal();
            } else {
                selesaiKuis();
            }
        });
    }

    private void animateButtonAndCheck(View view, int indeksPilihan) {
        if (jawabanUser[currentSoalIndex] != -1) return; 
        if (countDownTimer != null) countDownTimer.cancel();
        kunciPilihan(true);
        
        view.animate()
                .scaleX(0.95f).scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    view.animate()
                            .scaleX(1.05f).scaleY(1.05f)
                            .setDuration(100)
                            .withEndAction(() -> {
                                view.animate()
                                        .scaleX(1.0f).scaleY(1.0f)
                                        .setDuration(100)
                                        .withEndAction(() -> cekJawaban(indeksPilihan))
                                        .start();
                            })
                            .start();
                })
                .start();
    }

    private void loadDataSoal() {
        soalList = new ArrayList<>();
        soalList.add(new Soal("Saat berkunjung ke Gedung Papak, Dika melihat bagian bawah tiang bendera yang berbentuk ....", new String[]{"Kubus", "Balok", "Limas"}, 1, "Benar! Bagian bawah tiang tersebut memiliki panjang, lebar, dan tinggi yang berbeda sehingga membentuk balok.", "soalno1"));
        soalList.add(new Soal("Bagian atap Gedung Papak berbentuk bangun ruang apa?", new String[]{"Balok", "Kubus", "Limas"}, 2, "Tepat! Atap bangunannya mengerucut ke atas sehingga membentuk bangun ruang limas.", "soalno2"));
        soalList.add(new Soal("Gambar dadu di bawah ini, berbentuk bangun ruang apa?", new String[]{"Limas", "Kubus", "Balok"}, 1, "Benar! Dadu memiliki panjang sisi yang sama di setiap permukaannya, yang merupakan ciri utama kubus.", "soalno3"));
        soalList.add(new Soal("Balok mempunyai bentuk yang ....", new String[]{"Panjang", "Bulat", "Runcing"}, 0, "Betul! Secara umum, balok memiliki bentuk yang memanjang karena perbedaaan ukuran rusuknya.", null));
        soalList.add(new Soal("Tiang Gedung Papak pada gambar diatas berbentuk…", new String[]{"Kubus", "Limas", "Balok"}, 2, "Hebat! Tiang penyangga tersebut berbentuk balok memanjang yang kokoh.", "soalno4"));
        soalList.add(new Soal("Benda di bawah ini yang berbentuk limas adalah…", new String[]{"Tempat Pensil", "Piramida", "Kotak Kado"}, 1, "Tepat! Piramida adalah contoh paling jelas dari bangun ruang limas.", null));
        soalList.add(new Soal("Bangun ruang yang memiliki puncak adalah ....", new String[]{"Limas", "Kubus", "Balok"}, 0, "Benar! Sisi-sisi tegak pada limas bertemu pada satu titik yang disebut puncak.", null));
        soalList.add(new Soal("Bentuk limas pada Gedung Papak sering digunakan untuk ....", new String[]{"Atap", "Roda", "Jendela"}, 0, "Betul! Bentuk limas sangat umum digunakan sebagai struktur atap bangunan.", null));
        soalList.add(new Soal("Limas mempunyai bagian atas yang ....", new String[]{"Runcing", "Bulat", "Datar"}, 0, "Tepat! Titik puncak pada limas membuatnya terlihat runcing di bagian atas.", null));
        soalList.add(new Soal("Batu bata yang digunakan membangun Gedung Papak berbentuk ....", new String[]{"Balok", "Kubus", "Limas"}, 0, "Benar! Batu bata memiliki struktur bangun ruang balok.", "soalno9"));
        soalList.add(new Soal("Balok membantu membuat miniatur Gedung Papak pada bagian ....", new String[]{"Dinding", "Awan", "Pohon"}, 0, "Betul! Bangun ruang balok sangat cocok digunakan untuk merepresentasikan dinding yang lurus dan tegak.", null));
        soalList.add(new Soal("Kotak tisu seperti pada gambar diatas berbentuk…", new String[]{"Kubus", "Balok", "Prisma"}, 1, "Benar! Kotak tisu tersebut berbentuk balok.", "soalno11"));
        soalList.add(new Soal("Bentuk bangunan pada Gedung Papak diatas adalah..", new String[]{"Kubus", "Balok", "Limas"}, 1, "Tepat! Struktur utama bangunan tersebut memanjang sehingga menyerupai balok.", "soalno12"));
        soalList.add(new Soal("Benda berbentuk kubus yang ada di sekitar kita adalah ....", new String[]{"Dadu", "Penggaris", "Buku Tulis"}, 0, "Benar! Dadu adalah salah satu contoh benda sehari-hari yang berbentuk kubus sempurna.", null));
        soalList.add(new Soal("Ketika kamu melihat atap pada bangunan Gedung Papak, berbentuk apakah atapnya?", new String[]{"Limas", "Balok", "Kubus"}, 0, "Betul! Sekali lagi, atap bangunan ini mengerucut menyerupai limas.", null));
        soalList.add(new Soal("Berbentuk apakah kotak susu pada gambar diatas?", new String[]{"Balok", "Kubus", "Limas"}, 0, "Tepat! Kemasan kotak susu memanjang vertikal sehingga berbentuk balok.", "soalno15"));
        soalList.add(new Soal("Gedung Papak berada di Kota?", new String[]{"Semarang", "Bandung", "Salatiga"}, 2, "Hebat! Gedung Papak adalah warisan arsitektur yang ikonik di Kota Salatiga.", null));
        soalList.add(new Soal("Gedung Papak Sekarang menjadi?", new String[]{"Kantor Wali Kota", "Rumah Sakit", "Kantor Pos"}, 0, "Benar! Bangunan bersejarah ini dialihfungsikan sebagai pusat pemerintahan kota.", null));
        soalList.add(new Soal("Gedung Papak memiliki ciri khas bangunan?", new String[]{"Modern", "Kolonial", "Tradisional"}, 1, "Tepat! Arsitekturnya kental dengan gaya kolonial Belanda era Hindia Belanda.", null));
        soalList.add(new Soal("Kubus, Balok dan Limas termasuk bangun?", new String[]{"Datar", "Ruang", "Lengkung"}, 1, "Benar! Ketiganya memiliki volume dan dimensi tiga, sehingga disebut bangun ruang.", null));
        soalList.add(new Soal("Gedung Papak dominan berwarna?", new String[]{"Putih", "Merah", "Hitam"}, 0, "Betul! Warna putih sangat dominan pada arsitektur gaya kolonial untuk memantulkan panas.", null));
        soalList.add(new Soal("Saat mengamati Gedung Papak, Ali melihat bangunan utama yang bentuknya lebih panjang daripada kubus. Bangunan tersebut menyerupai ....", new String[]{"Limas", "Balok", "Bola"}, 1, "Tepat! Balok memiliki dimensi yang lebih memanjang dibandingkan kubus.", null));
        soalList.add(new Soal("Indah dan teman-temannya membuat miniatur Gedung Papak. Untuk membuat bagian atap yang runcing, mereka menggunakan bangun ruang ....", new String[]{"Kubus", "Balok", "Limas"}, 2, "Benar! Bangun limas sangat pas merepresentasikan atap.", null));
        soalList.add(new Soal("Saat membuat atap miniatur Gedung Papak, kelompok Nurul menggunakan limas. Mereka melihat bagian samping limas berbentuk segitiga. Limas memiliki sisi samping berbentuk ....", new String[]{"Lingkaran", "Persegi", "Segitiga"}, 2, "Tepat! Semua sisi tegak pada sebuah limas berbentuk segitiga.", null));
        soalList.add(new Soal("Saat membuat miniatur Gedung Papak, Bagas memegang sebuah kubus. Ia memperhatikan bahwa setiap sisi kubus berbentuk ...", new String[]{"Persegi", "Segitiga", "Lingkaran"}, 0, "Sempurna! Sebuah kubus dibentuk oleh enam buah sisi yang semuanya berbentuk persegi.", null));
    }

    private void tampilkanSoal() {
        if (countDownTimer != null) countDownTimer.cancel();

        Soal soal = soalList.get(currentSoalIndex);

        tvProgress.setText("Soal " + (currentSoalIndex + 1) + " dari " + soalList.size());
        pbKuis.setProgress(currentSoalIndex + 1);

        tvPertanyaan.setText(soal.getPertanyaan());
        String[] opsi = soal.getOpsi();
        
        resetButtonVisuals();
        btnOpsiA.setText("A. " + opsi[0]);
        btnOpsiB.setText("B. " + opsi[1]);
        btnOpsiC.setText("C. " + opsi[2]);

        if (soal.getImagePath() != null) {
            ivKuisImage.setVisibility(View.VISIBLE);
            int imageRes = getResources().getIdentifier(soal.getImagePath(), "drawable", getPackageName());
            if(imageRes != 0) ivKuisImage.setImageResource(imageRes);
        } else {
            ivKuisImage.setVisibility(View.GONE);
        }
        
        btnPrev.setEnabled(currentSoalIndex > 0);
        
        if (currentSoalIndex == soalList.size() - 1) {
            btnNext.setText("SELESAI ✅");
            btnNext.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.success)));
        } else {
            btnNext.setText("SELANJUTNYA →");
            btnNext.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.primary)));
        }

        // Cek apakah soal sudah dijawab
        if (jawabanUser[currentSoalIndex] != -1 || isTimeout[currentSoalIndex]) {
            tvTimer.setText("—");
            tvTimer.setTextColor(getColor(R.color.text_medium));
            kunciPilihan(true);
            tampilkanJawaban(jawabanUser[currentSoalIndex], soal.getJawabanBenar());
        } else {
            timeLeftInMillis = TIMER_DURATION;
            tvTimer.setTextColor(getColor(R.color.accent_orange));
            kunciPilihan(false);
            startTimer();
        }
    }

    private void resetButtonVisuals() {
        MaterialButton[] btns = {btnOpsiA, btnOpsiB, btnOpsiC};
        for(MaterialButton b : btns) {
            b.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            b.setStrokeColor(ColorStateList.valueOf(getColor(R.color.primary)));
            b.setTextColor(getColor(R.color.text_dark));
        }
    }

    private void kunciPilihan(boolean kunci) {
        btnOpsiA.setEnabled(!kunci);
        btnOpsiB.setEnabled(!kunci);
        btnOpsiC.setEnabled(!kunci);
    }

    private void tampilkanJawaban(int userAns, int correctAns) {
        MaterialButton[] btns = {btnOpsiA, btnOpsiB, btnOpsiC};
        
        // Highlight correct answer
        btns[correctAns].setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.success)));
        btns[correctAns].setTextColor(getColor(R.color.white));
        
        // Highlight wrong answer if user chose wrong
        if (userAns != -1 && userAns != correctAns) {
            btns[userAns].setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.danger)));
            btns[userAns].setTextColor(getColor(R.color.white));
        }
    }

    private void cekJawaban(int indeksPilihan) {
        if (countDownTimer != null) countDownTimer.cancel();
        
        jawabanUser[currentSoalIndex] = indeksPilihan;
        Soal soal = soalList.get(currentSoalIndex);
        
        kunciPilihan(true);
        tampilkanJawaban(indeksPilihan, soal.getJawabanBenar());

        String rawFeedback = soal.getFeedback();
        // Menghapus kata pujian di awal kalimat agar tidak tabrakan saat jawaban salah
        String cleanedFeedback = rawFeedback.replaceAll("^(?i)(Benar!|Tepat!|Hebat!|Betul!|Sempurna!)\\s*", "");

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        if (indeksPilihan == soal.getJawabanBenar()) {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 50, 50, 50}, -1)); // Getar pendek ganda
                } else {
                    vibrator.vibrate(new long[]{0, 50, 50, 50}, -1);
                }
            }
            Snackbar.make(findViewById(android.R.id.content), "✅ Benar! " + cleanedFeedback, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.success)).show();
        } else {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)); // Getar panjang sekali
                } else {
                    vibrator.vibrate(300);
                }
            }
            Snackbar.make(findViewById(android.R.id.content), "❌ Salah. " + cleanedFeedback, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.danger)).show();
        }

        // Auto next to unanswered
        tvPertanyaan.postDelayed(this::autoNextUnanswered, 1500);
    }
    
    private void autoNextUnanswered() {
        // Cari soal berikutnya yang belum dijawab
        int nextIndex = -1;
        for (int i = 1; i <= soalList.size(); i++) {
            int idx = (currentSoalIndex + i) % soalList.size();
            if (jawabanUser[idx] == -1 && !isTimeout[idx]) {
                nextIndex = idx;
                break;
            }
        }
        
        if (nextIndex != -1) {
            currentSoalIndex = nextIndex;
            tampilkanSoal();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerUI();
            }
            @Override
            public void onFinish() {
                isTimeout[currentSoalIndex] = true;
                kunciPilihan(true);
                tampilkanJawaban(-1, soalList.get(currentSoalIndex).getJawabanBenar());
                Snackbar.make(findViewById(android.R.id.content), "⏰ Waktu Habis!", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.danger)).show();
                tvPertanyaan.postDelayed(() -> autoNextUnanswered(), 1500);
            }
        }.start();
    }

    private void updateTimerUI() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText(seconds + "s");
        if (seconds <= 5) {
            tvTimer.setTextColor(getColor(R.color.danger));
        } else {
            tvTimer.setTextColor(getColor(R.color.accent_orange));
        }
    }

    private void selesaiKuis() {
        if (countDownTimer != null) countDownTimer.cancel();
        
        int jawabanBenar = 0;
        int jawabanSalah = 0;
        
        for (int i = 0; i < soalList.size(); i++) {
            if (jawabanUser[i] == soalList.get(i).getJawabanBenar()) {
                jawabanBenar++;
            } else if (jawabanUser[i] != -1 || isTimeout[i]) {
                jawabanSalah++;
            }
        }
        
        int score = (int) (((double) jawabanBenar / soalList.size()) * 100);

        Intent intent = new Intent(this, HasilKuisActivity.class);
        intent.putExtra("SKOR", score);
        intent.putExtra("BENAR", jawabanBenar);
        intent.putExtra("SALAH", jawabanSalah);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}

