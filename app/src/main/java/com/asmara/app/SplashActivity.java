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
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
