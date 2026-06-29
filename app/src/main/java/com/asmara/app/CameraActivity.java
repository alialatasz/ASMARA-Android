package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class CameraActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private ModelRenderable placeholderRenderable;
    private boolean modelPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Tombol kembali
        FloatingActionButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        MaterialButton btnBukaEdukasi = findViewById(R.id.btn_buka_edukasi);
        TextView tvChatBubble = findViewById(R.id.tv_chat_bubble);

        btnBukaEdukasi.setOnClickListener(v -> {
            Intent intent = new Intent(CameraActivity.this, EdukasiActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Inisialisasi AR Fragment
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        // Memuat model 3D (gedung.glb) dari folder assets
        ModelRenderable.builder()
                .setSource(this, android.net.Uri.parse("gedung.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    placeholderRenderable = renderable;
                })
                .exceptionally(throwable -> {
                    tvChatBubble.setText("Oops! File gedung.glb belum dimasukkan ke folder assets.");
                    return null;
                });

        // Event saat pengguna mengetuk lantai (plane) yang sudah terdeteksi titik-titiknya
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (placeholderRenderable == null || modelPlaced) {
                return; // Jangan lakukan apa-apa jika model belum siap atau sudah diletakkan
            }

            // 1. Membuat 'Jangkar' (Anchor) di titik dunia nyata yang diketuk
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            // 2. Menempelkan objek 3D ke jangkar tersebut (bisa digeser/diperbesar pengguna)
            TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
            modelNode.setParent(anchorNode);
            modelNode.setRenderable(placeholderRenderable);
            modelNode.select(); // Memberikan garis seleksi di sekeliling model

            modelPlaced = true;
            
            // 3. Update UI agar tombol Info Sejarah muncul
            tvChatBubble.setText("Wah! Gedung Papak berhasil muncul! Kamu bisa memutar atau mencubitnya layaknya foto untuk membesarkannya lho.");
            btnBukaEdukasi.setVisibility(View.VISIBLE);
        });
    }
}
