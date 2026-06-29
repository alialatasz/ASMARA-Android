package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class HasilKuisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_kuis);

        // Get data from Intent
        int skor = getIntent().getIntExtra("SKOR", 0);
        int benar = getIntent().getIntExtra("BENAR", 0);
        int salah = getIntent().getIntExtra("SALAH", 0);

        // Simpan hasil kuis ke profil anak
        ProfilActivity.simpanHasilKuis(this, skor, benar);

        TextView tvScoreNumber = findViewById(R.id.tv_score_number);
        TextView tvCorrect = findViewById(R.id.tv_correct);
        TextView tvWrong = findViewById(R.id.tv_wrong);
        TextView tvGradeTitle = findViewById(R.id.tv_grade_title);
        TextView tvGradeIcon = findViewById(R.id.tv_grade_icon);
        
        TextView star1 = findViewById(R.id.star1);
        TextView star2 = findViewById(R.id.star2);
        TextView star3 = findViewById(R.id.star3);

        tvScoreNumber.setText(String.valueOf(skor));
        tvCorrect.setText("✅ " + benar + " Benar");
        tvWrong.setText("❌ " + salah + " Salah");

        // Logic for Grade
        if (skor >= 90) {
            tvGradeIcon.setText("🏆");
            tvGradeTitle.setText("LUAR BIASA!");
            tvGradeTitle.setTextColor(getColor(R.color.accent_orange));
            star1.setAlpha(1.0f); star2.setAlpha(1.0f); star3.setAlpha(1.0f);
        } else if (skor >= 70) {
            tvGradeIcon.setText("🌟");
            tvGradeTitle.setText("HEBAT!");
            tvGradeTitle.setTextColor(getColor(R.color.success));
            star1.setAlpha(1.0f); star2.setAlpha(1.0f); star3.setAlpha(0.3f);
        } else if (skor >= 50) {
            tvGradeIcon.setText("👍");
            tvGradeTitle.setText("BAGUS!");
            tvGradeTitle.setTextColor(getColor(R.color.primary));
            star1.setAlpha(1.0f); star2.setAlpha(0.3f); star3.setAlpha(0.3f);
        } else {
            tvGradeIcon.setText("💪");
            tvGradeTitle.setText("TETAP SEMANGAT!");
            tvGradeTitle.setTextColor(getColor(R.color.danger));
            star1.setAlpha(0.3f); star2.setAlpha(0.3f); star3.setAlpha(0.3f);
        }

        MaterialButton btnHome = findViewById(R.id.btn_kembali_home);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(HasilKuisActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
