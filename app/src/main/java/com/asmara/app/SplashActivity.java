package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.button.MaterialButton;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_splash);

        ConstraintLayout layoutWelcome = findViewById(R.id.layout_welcome);
        View layoutLoading = findViewById(R.id.layout_loading);
        MaterialButton btnMulai = findViewById(R.id.btn_mulai);
        ImageView maskotLoading = findViewById(R.id.iv_maskot_loading);

        // Initial entry animation for Welcome screen
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        layoutWelcome.startAnimation(fadeIn);

        // ------------------ ANIMASI MASKOT WELCOME ------------------
        ImageView ivMaskotFront = findViewById(R.id.iv_maskot_front);
        if (ivMaskotFront != null) {
            // Animasi Idle (Pulse)
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ivMaskotFront, "scaleX", 1.0f, 1.06f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ivMaskotFront, "scaleY", 1.0f, 1.06f);
            scaleDownX.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownY.setRepeatCount(ValueAnimator.INFINITE);
            scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownY.setRepeatMode(ValueAnimator.REVERSE);
            scaleDownX.setDuration(1000);
            scaleDownY.setDuration(1000);

            AnimatorSet pulseSet = new AnimatorSet();
            pulseSet.playTogether(scaleDownX, scaleDownY);

            // Animasi Masuk (Pop-up & Bounce)
            ivMaskotFront.setTranslationY(300f);
            ivMaskotFront.setScaleX(0.5f);
            ivMaskotFront.setScaleY(0.5f);
            ivMaskotFront.setAlpha(0f);

            ivMaskotFront.animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(800)
                    .setStartDelay(300)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .withEndAction(pulseSet::start)
                    .start();
        }
        // -----------------------------------------------------------

        btnMulai.setOnClickListener(v -> {
            // Disable button to prevent double-clicks
            btnMulai.setEnabled(false);

            // Animasi transisi ke Loading
            AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(400);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    layoutWelcome.setVisibility(View.GONE);
                    layoutLoading.setVisibility(View.VISIBLE);

                    // Animasi Mascot Welcome membesar dengan efek membal (Overshoot)
                    ScaleAnimation scaleAnim = new ScaleAnimation(
                        0.3f, 1.0f, 0.3f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    );
                    scaleAnim.setDuration(800);
                    scaleAnim.setInterpolator(new OvershootInterpolator(1.2f));

                    AlphaAnimation fadeInLoading = new AlphaAnimation(0f, 1f);
                    fadeInLoading.setDuration(500);

                    AnimationSet loadingAnim = new AnimationSet(true);
                    loadingAnim.addAnimation(scaleAnim);
                    loadingAnim.addAnimation(fadeInLoading);
                    
                    maskotLoading.startAnimation(loadingAnim);

                    // Pindah ke halaman utama setelah delay animasi
                    new Handler().postDelayed(() -> {
                        android.content.SharedPreferences prefs = getSharedPreferences("ASMARA_PREFS", MODE_PRIVATE);
                        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

                        Intent intent;
                        if (isFirstRun) {
                            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }, 2500); // 2.5 detik durasi loading
                }
            });
            layoutWelcome.startAnimation(fadeOut);
        });
    }
}
