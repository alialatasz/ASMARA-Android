package com.asmara.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class KoleksiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koleksi);

        // Setup toolbar dengan back button
        MaterialToolbar toolbar = findViewById(R.id.toolbar_koleksi);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Koleksi Sejarah");
        }
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Event listener klik kartu
        android.view.View cardPapak = findViewById(R.id.card_papak);
        android.view.View cardPakuwon = findViewById(R.id.card_pakuwon);

        if (cardPapak != null) {
            cardPapak.setOnClickListener(v -> {
                AnimationHelper.animateButton(v);
                android.content.Intent intent = new android.content.Intent(KoleksiActivity.this, SejarahDetailActivity.class);
                intent.putExtra("NAMA_GEDUNG", "Papak");
                startActivity(intent);
            });
        }

        if (cardPakuwon != null) {
            cardPakuwon.setOnClickListener(v -> {
                AnimationHelper.animateButton(v);
                android.content.Intent intent = new android.content.Intent(KoleksiActivity.this, SejarahDetailActivity.class);
                intent.putExtra("NAMA_GEDUNG", "Pakuwon");
                startActivity(intent);
            });
        }

        android.view.View cardRumahDinas = findViewById(R.id.card_rumah_dinas);
        if (cardRumahDinas != null) {
            cardRumahDinas.setOnClickListener(v -> {
                AnimationHelper.animateButton(v);
                android.content.Intent intent = new android.content.Intent(KoleksiActivity.this, SejarahDetailActivity.class);
                intent.putExtra("NAMA_GEDUNG", "RumahDinas");
                startActivity(intent);
            });
        }

        android.view.View[] viewsToAnimate = new android.view.View[]{
            cardPapak, cardPakuwon, cardRumahDinas
        };
        AnimationHelper.animateStaggeredSlideDown(viewsToAnimate, 200, 100);
    }
}
