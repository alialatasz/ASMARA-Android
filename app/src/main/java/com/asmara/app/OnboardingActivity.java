package com.asmara.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutDots;
    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        layoutDots = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        TextView tvSkip = findViewById(R.id.tvSkip);

        setupOnboardingItems();
        ViewPager2 viewPagerOnboarding = findViewById(R.id.viewPagerOnboarding);
        viewPagerOnboarding.setAdapter(onboardingAdapter);

        setupDots();
        setCurrentDot(0);

        viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);
                if (position == onboardingAdapter.getItemCount() - 1) {
                    btnNext.setText("Mulai");
                } else {
                    btnNext.setText("Lanjut");
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (viewPagerOnboarding.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                viewPagerOnboarding.setCurrentItem(viewPagerOnboarding.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        tvSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(
                R.drawable.maskot_welcome, // Using mascot as placeholder for welcome
                "Selamat Datang di ASMARA",
                "Aplikasi Smartcard Augmented Reality untuk belajar geometri dengan lebih interaktif dan menyenangkan."
        ));
        items.add(new OnboardingItem(
                R.drawable.ic_qr_scan, // Using scan icon
                "Scan Smartcard",
                "Arahkan kamera HP ke Smartcard ASMARA atau gambar miniatur Gedung Papak untuk memunculkan model 3D."
        ));
        items.add(new OnboardingItem(
                R.drawable.ic_quiz, // Using quiz icon
                "Uji Pengetahuanmu",
                "Setelah mempelajari materi geometri, asah kemampuanmu melalui Kuis interaktif dan jadilah juara!"
        ));
        onboardingAdapter = new OnboardingAdapter(items);
    }

    private void setupDots() {
        ImageView[] dots = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dot_inactive));
            dots[i].setLayoutParams(params);
            layoutDots.addView(dots[i]);
        }
    }

    private void setCurrentDot(int index) {
        int childCount = layoutDots.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutDots.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dot_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dot_inactive));
            }
        }
    }

    private void finishOnboarding() {
        // Mainkan suara intro happy (singkat)
        try {
            android.media.MediaPlayer player = android.media.MediaPlayer.create(this, R.raw.intro_happy);
            if (player != null) {
                player.start();
                player.setOnCompletionListener(android.media.MediaPlayer::release);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Simpan flag bahwa onboarding sudah selesai
        SharedPreferences prefs = getSharedPreferences("ASMARA_PREFS", MODE_PRIVATE);
        prefs.edit().putBoolean("isFirstRun", false).apply();

        // Buka MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
