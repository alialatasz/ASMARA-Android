package com.asmara.app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class EdukasiActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private FloatingActionButton btnAudio;
    
    private TextView tvSubtitle;
    private Handler subtitleHandler = new Handler();
    private Runnable subtitleRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edukasi);

        // Inisialisasi MediaPlayer untuk memutar MP3
        mediaPlayer = MediaPlayer.create(this, R.raw.suara_narasi);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnAudio.setImageResource(R.drawable.ic_speaker);
                tvSubtitle.setVisibility(View.GONE);
                if (subtitleRunnable != null) {
                    subtitleHandler.removeCallbacks(subtitleRunnable);
                }
            });
        }

        tvSubtitle = findViewById(R.id.tv_subtitle);

        // Tombol kembali
        FloatingActionButton btnBack = findViewById(R.id.btn_back_edu);
        btnBack.setOnClickListener(v -> {
            stopAudio();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Tombol Audio - MP3
        btnAudio = findViewById(R.id.btn_audio);
        btnAudio.setOnClickListener(v -> {
            animateAudioButton(v);
            if (mediaPlayer == null) {
                Snackbar.make(v, "Gagal memuat file audio", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (isPlaying) {
                stopAudio();
                Snackbar.make(v, "⏹ Audio dihentikan", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.text_dark))
                    .show();
            } else {
                playAudio();
                Snackbar.make(v, "🔊 Memainkan narasi...", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getColor(R.color.primary))
                    .setTextColor(getColor(R.color.white))
                    .show();
            }
        });

        // Tombol navigasi ke Kuis
        com.google.android.material.button.MaterialButton btnKuis = findViewById(R.id.btn_lanjut_kuis);
        btnKuis.setOnClickListener(v -> {
            stopAudio();
            Intent intent = new Intent(EdukasiActivity.this, KuisMasukActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            
            // Subtitle logic
            tvSubtitle.setVisibility(View.VISIBLE);
            if (subtitleRunnable != null) {
                subtitleHandler.removeCallbacks(subtitleRunnable);
            }
            subtitleRunnable = new Runnable() {
                int step = 0;
                @Override
                public void run() {
                    if (step == 0) {
                        tvSubtitle.setText("Gedung Papak adalah bangunan bersejarah di Salatiga yang dibangun pada masa kolonial Belanda sekitar abad ke-19.");
                        subtitleHandler.postDelayed(this, 7000);
                    } else if (step == 1) {
                        tvSubtitle.setText("Bangunan ini memiliki arsitektur khas Eropa dengan atap datar yang menjadi ciri khasnya.");
                        subtitleHandler.postDelayed(this, 6000);
                    } else if (step == 2) {
                        tvSubtitle.setText("Kini Gedung Papak menjadi salah satu ikon budaya dan warisan sejarah Kota Salatiga yang dilindungi.");
                        subtitleHandler.postDelayed(this, 6000);
                    } else {
                        tvSubtitle.setVisibility(View.GONE);
                    }
                    step++;
                }
            };
            subtitleHandler.post(subtitleRunnable);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isPlaying = false;
        }
        if (tvSubtitle != null) {
            tvSubtitle.setVisibility(View.GONE);
        }
        if (subtitleRunnable != null) {
            subtitleHandler.removeCallbacks(subtitleRunnable);
        }
    }

    private void animateAudioButton(View view) {
        ScaleAnimation pulse = new ScaleAnimation(
            1f, 1.2f, 1f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulse.setDuration(150);
        pulse.setRepeatCount(1);
        pulse.setRepeatMode(Animation.REVERSE);
        view.startAnimation(pulse);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAudio();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
