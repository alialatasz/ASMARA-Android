package com.asmara.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;

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
    private MaterialButton btnDragDropLaunch;

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
        btnDragDropLaunch = findViewById(R.id.btn_dragdrop_launch);

        btnDragDropLaunch.setOnClickListener(v -> {
            Intent intent = new Intent(this, DragDropActivity.class);
            startActivityForResult(intent, 1500);
        });

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
        soalList.add(new Soal("Saat berkunjung ke Gedung Papak, Kresna melihat bagian bawah tiang bendera yang berbentuk ...", new String[]{"Kubus", "Balok", "Limas"}, 1, "Bagian bawah tiang bendera berbentuk balok karena mempunyai 6 sisi nya berbentuk persegi panjang.", "soal_1"));
        soalList.add(new Soal("Bagian atap Gedung Papak pada gambar di atas berbentuk bangun ruang apa?", new String[]{"Balok", "Kubus", "Limas"}, 2, "Atap Gedung Papak berbentuk limas karena limas memiliki sisi-sisi berbentuk segitiga dan satu titik puncak.", "soal_2"));
        soalList.add(new Soal("Berbentuk apakah bangun di bawah ini?", new String[]{"Persegi", "Persegi Panjang", "Segitiga"}, 1, "Bangun ini adalah persegi panjang karena mempunyai 2 sisi panjang dan 2 sisi pendek.", "soal_3"));
        soalList.add(new Soal("Kubus pada gambar di bawah ini, memiliki jumlah sudut sebanyak… titik sudut", new String[]{"8", "6", "4"}, 0, "Karena salah satu unsur-unsur kubus yaitu mempunyai 8 titik sudut.", "soal_4"));
        soalList.add(new Soal("Tiang Gedung Papak pada gambar di bawah berbentuk…", new String[]{"Kubus", "Balok", "Limas"}, 1, "Tiang Gedung Papak berbentuk balok karena balok mempunyai 6 sisi berbentuk persegi panjang.", "soal_5"));
        soalList.add(new Soal("Berbentuk apakah bangun datar di bawah ini…", new String[]{"Segitiga", "Persegi", "Persegi Panjang"}, 0, "Berbentuk segitiga karena mempunyai 3 sisi dan 3 titik sudut.", "soal_6"));
        soalList.add(new Soal("Bangun ruang yang memiliki puncak adalah ....", new String[]{"Limas", "Kubus", "Balok"}, 0, "Karena salah satu unsur-unsur limas yaitu mempunyai satu titik puncak.", null));
        soalList.add(new Soal("Bagian pada Gedung Papak dibawah ini berbentuk ....", new String[]{"Kubus", "Balok", "Limas"}, 0, "Bagian Gedung Papak ini berbentuk kubus karena semua sisinya berbentuk persegi.", "soal_8"));
        soalList.add(new Soal("Bangun datar yang mempunyai 2 sisi panjang dan 2 sisi pendek yang saling berhadapan disebut…", new String[]{"Persegi", "Persegi Panjang", "Segitiga"}, 1, "Persegi panjang mempunyai 2 sisi panjang dan 2 sisi pendek.", null));
        soalList.add(new Soal("Gambar di bawah ini merupakan jaring-jaring dari bangun ruang..", new String[]{"Balok", "Kubus", "Limas"}, 2, "Jaring-jaring ini dapat dilipat menjadi limas karena mempunyai alas berbentuk persegi dan sisi berbentuk segitiga.", "soal_10"));
        soalList.add(new Soal("Jumlah sisi bangun ruang kubus di bawah ini adalah…", new String[]{"4", "5", "6"}, 2, "Kubus mempunyai 6 sisi yang semuanya berbentuk persegi.", "soal_11"));
        soalList.add(new Soal("Perhatikan bangun berikut. Bangun di atas disebut segitiga karena…", new String[]{"Karena semua sisinya sama panjang", "Karena mempunyai 2 pasang sisi yang sama panjang", "Karena mempunyai 3 sisi"}, 2, "Bangun ini adalah segitiga karena mempunyai 3 sisi dan 3 titik sudut, sesuai dengan ciri-ciri bangun segitiga.", "soal_12"));
        soalList.add(new Soal("Bangun yang memiliki empat sisi sama panjang adalah…", new String[]{"Persegi Panjang", "Segitiga", "Persegi"}, 2, "Bangun ini disebut persegi karena mempunyai 4 sisi yang sama panjang. Persegi juga memiliki 4 titik sudut.", null));
        soalList.add(new Soal("Mengapa jendela Gedung Papak tersebut disebut persegi panjang?", new String[]{"Karena semua sisinya sama panjang", "Karena mempunyai 2 pasang sisi yang sama panjang", "Karena mempunyai 3 sisi"}, 1, "Jendela Gedung Papak berbentuk persegi panjang karena mempunyai 2 sisi panjang dan 2 sisi pendek yang saling berhadapan sama panjang.", "soal_14"));
        soalList.add(new Soal("Susun kembali pintu Gedung Papak berikut! (Drag and Drop)", new String[]{"A", "B", "C"}, 0, "Hebat, kamu berhasil menyusun pintu!", "soal_15_dragdrop"));
        soalList.add(new Soal("Balok pada gambar di bawah ini memiliki sudut yang berjumlah… titik sudut.", new String[]{"8", "2", "6"}, 0, "Karena salah satu unsur-unsur balok yaitu mempunyai 8 titik sudut.", "soal_16"));
        soalList.add(new Soal("Jaring-jaring bangun ruang apakah gambar di bawah ini?", new String[]{"Balok", "Kubus", "Limas"}, 1, "Jaring-jaring ini dapat dilipat menjadi kubus karena terdiri dari 6 sisi berbentuk persegi. Setelah dilipat, semua sisinya membentuk bangun ruang kubus.", "soal_17"));
        soalList.add(new Soal("Gambar di bawah ini merupakan jaring-jaring dari bangun ruang apa?", new String[]{"Kubus", "Balok", "Limas"}, 1, "Jaring-jaring ini dapat dilipat menjadi balok karena memiliki 6 sisi berbentuk persegi panjang. Setelah dilipat, terbentuk bangun ruang balok.", "soal_18"));
        soalList.add(new Soal("Indah dan teman-temannya membuat miniatur Gedung Papak. Untuk membuat bagian atap yang runcing, mereka menggunakan bangun ruang ....", new String[]{"Kubus", "Balok", "Limas"}, 2, "Atap miniatur Gedung Papak menggunakan bangun ruang limas karena limas mempunyai satu titik puncak dan sisi-sisinya berbentuk segitiga.", null));
        soalList.add(new Soal("Saat membuat atap miniatur Gedung Papak, kelompok Nurul menggunakan limas. Limas memiliki sisi samping berbentuk ....", new String[]{"Lingkaran", "Persegi", "Segitiga"}, 2, "Sisi samping limas berbentuk segitiga. Itulah salah satu ciri bangun ruang limas.", null));
        soalList.add(new Soal("Saat membuat miniatur Gedung Papak, Bagas memegang sebuah kubus. Ia memperhatikan bahwa setiap sisi kubus berbentuk ...", new String[]{"Persegi", "Segitiga", "Lingkaran"}, 0, "Setiap sisi kubus berbentuk persegi. Kubus mempunyai 6 sisi yang semuanya berbentuk persegi.", null));
        soalList.add(new Soal("Gedung Papak berada di Kota?", new String[]{"Semarang", "Bandung", "Salatiga"}, 2, "Benar! Gedung Papak berada di Kota Salatiga dan gedung ini sekarang menjadi salah satu bangunan penting di Kota Salatiga.", null));
        soalList.add(new Soal("Gedung Papak sekarang menjadi?", new String[]{"Kantor Wali Kota", "Rumah Sakit", "Kantor Pos"}, 0, "Benar! Sekarang Gedung Papak digunakan sebagai Kantor Wali Kota Salatiga untuk menjalankan pemerintahan kota.", null));
        soalList.add(new Soal("Gedung Papak memiliki ciri khas bangunan?", new String[]{"Modern", "Kolonial", "Tradisional"}, 1, "Benar! Gedung Papak memiliki gaya bangunan kolonial Belanda yang menjadi ciri khasnya sejak dahulu.", null));
        soalList.add(new Soal("Gedung Papak dominan berwarna?", new String[]{"Putih", "Merah", "Hitam"}, 0, "Benar! Gedung Papak memiliki gaya bangunan kolonial Belanda yang menjadi ciri khasnya sejak dahulu.", null));
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

        if (soal.getImageName() != null) {
            ivKuisImage.setVisibility(View.VISIBLE);
            int imageRes = getResources().getIdentifier(soal.getImageName(), "drawable", getPackageName());
            if(imageRes != 0) ivKuisImage.setImageResource(imageRes);
        } else {
            ivKuisImage.setVisibility(View.GONE);
        }
        
        // Handle Drag Drop Question Type
        if ("soal_15_dragdrop".equals(soal.getImageName())) {
            btnOpsiA.setVisibility(View.GONE);
            btnOpsiB.setVisibility(View.GONE);
            btnOpsiC.setVisibility(View.GONE);
            btnDragDropLaunch.setVisibility(View.VISIBLE);
        } else {
            btnOpsiA.setVisibility(View.VISIBLE);
            btnOpsiB.setVisibility(View.VISIBLE);
            btnOpsiC.setVisibility(View.VISIBLE);
            btnDragDropLaunch.setVisibility(View.GONE);
        }
        
        btnPrev.setEnabled(currentSoalIndex > 0);
        
        if (currentSoalIndex == soalList.size() - 1) {
            btnNext.setText("SELESAI");
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

    private android.media.MediaPlayer feedbackMediaPlayer;

    private void playFeedbackSound(boolean isCorrect) {
        if (feedbackMediaPlayer != null) {
            feedbackMediaPlayer.release();
        }
        feedbackMediaPlayer = android.media.MediaPlayer.create(this, isCorrect ? R.raw.suara_benar : R.raw.suara_salah);
        if (feedbackMediaPlayer != null) {
            feedbackMediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                feedbackMediaPlayer = null;
            });
            feedbackMediaPlayer.start();
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
        
        boolean isCorrect = (indeksPilihan == soal.getJawabanBenar());
        playFeedbackSound(isCorrect);
        
        if (isCorrect) {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 50, 50, 50}, -1)); // Getar pendek ganda
                } else {
                    vibrator.vibrate(new long[]{0, 50, 50, 50}, -1);
                }
            }
        } else {
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)); // Getar panjang sekali
                } else {
                    vibrator.vibrate(300);
                }
            }
        }

        // Tampilkan Dialog Penjelasan
        tampilkanDialogFeedback(isCorrect, rawFeedback);
    }
    
    private void tampilkanDialogFeedback(boolean isCorrect, String penjelasan) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_feedback_maskot);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            int width = (int)(getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvJudul = dialog.findViewById(R.id.tv_judul_feedback);
        TextView tvPenjelasan = dialog.findViewById(R.id.tv_penjelasan_feedback);
        MaterialButton btnLanjut = dialog.findViewById(R.id.btn_lanjut_feedback);
        ImageView imgMaskot = dialog.findViewById(R.id.img_maskot_feedback);

        if (isCorrect) {
            tvJudul.setText("Hebat! Jawabanmu Benar");
            tvJudul.setTextColor(getColor(R.color.success));
            imgMaskot.setImageResource(R.drawable.maskot_asmara); 
        } else {
            tvJudul.setText("Ups! Jawabanmu Kurang Tepat");
            tvJudul.setTextColor(getColor(R.color.danger));
            imgMaskot.setImageResource(R.drawable.maskot_asmara); 
        }
        
        tvPenjelasan.setText(penjelasan);

        btnLanjut.setOnClickListener(v -> {
            dialog.dismiss();
            autoNextUnanswered();
        });
        
        dialog.setCancelable(false);
        dialog.show();
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
                tampilkanDialogFeedback(false, "Waktu kamu sudah habis. " + soalList.get(currentSoalIndex).getFeedback());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1500 && resultCode == RESULT_OK) {
            // Berhasil susun pintu
            cekJawaban(0); // Pilihan 0 adalah indeks jawaban benar di Soal 15
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

