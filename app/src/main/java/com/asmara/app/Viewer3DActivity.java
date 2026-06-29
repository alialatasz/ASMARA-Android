package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class Viewer3DActivity extends AppCompatActivity {

    private SceneView sceneView;
    private Node modelNode;
    
    // Variabel untuk gestur
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float currentScale = 0.5f; // Skala awal yang lebih kecil
    private float rotationAngleX = 0f;
    private float rotationAngleY = 30f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_3d);

        sceneView = findViewById(R.id.scene_view);
        TextView tvInfo = findViewById(R.id.tv_info_viewer);
        MaterialButton btnEdukasi = findViewById(R.id.btn_edukasi_viewer);
        FloatingActionButton btnBack = findViewById(R.id.btn_back_viewer);

        // GARANSI 100%: Memaksa UI untuk tampil di atas kanvas 3D
        findViewById(R.id.header_bar_viewer).bringToFront();
        findViewById(R.id.bottom_bar_viewer).bringToFront();

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnEdukasi.setOnClickListener(v -> {
            Intent intent = new Intent(Viewer3DActivity.this, EdukasiActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Setup Detektor Gestur (Cubit dan Geser)
        setupGestures();

        // Meneruskan sentuhan dari SceneView ke detektor gestur
        sceneView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            return true;
        });

        ModelRenderable.builder()
                .setSource(this, android.net.Uri.parse("gedung.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    modelNode = new Node();
                    modelNode.setParent(sceneView.getScene());
                    modelNode.setRenderable(renderable);

                    // Posisi lebih ke belakang dan ke bawah agar muat di layar
                    modelNode.setLocalPosition(new Vector3(0f, -1.0f, -4.0f));
                    
                    // Skala awal diperkecil agar tidak kepotong
                    modelNode.setLocalScale(new Vector3(currentScale, currentScale, currentScale));
                    updateModelRotation();

                    tvInfo.setText("🏛️ Gedung Papak berhasil dimuat!\n✨ Geser 1 jari untuk memutar\n✨ Cubit 2 jari untuk memperbesar");
                })
                .exceptionally(throwable -> {
                    tvInfo.setText("⚠️ Oops! File gedung.glb belum ditemukan di folder assets.");
                    Toast.makeText(this, "Gagal memuat model 3D", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    private void setupGestures() {
        // 1. Gestur Zoom (Pinch)
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (modelNode != null) {
                    currentScale *= detector.getScaleFactor();
                    // Batasi ukuran zoom agar tidak terlalu kecil atau terlalu besar
                    currentScale = Math.max(0.1f, Math.min(currentScale, 3.0f));
                    modelNode.setLocalScale(new Vector3(currentScale, currentScale, currentScale));
                }
                return true;
            }
        });

        // 2. Gestur Putar (Drag)
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (modelNode != null) {
                    // Mengubah sudut rotasi berdasarkan pergerakan jari
                    rotationAngleY -= distanceX * 0.5f;
                    rotationAngleX -= distanceY * 0.5f;
                    
                    // Batasi rotasi atas/bawah agar tidak terbalik
                    rotationAngleX = Math.max(-45f, Math.min(rotationAngleX, 45f));
                    
                    updateModelRotation();
                }
                return true;
            }
        });
    }

    private void updateModelRotation() {
        if (modelNode != null) {
            Quaternion rotY = Quaternion.axisAngle(new Vector3(0, 1, 0), rotationAngleY);
            Quaternion rotX = Quaternion.axisAngle(new Vector3(1, 0, 0), rotationAngleX);
            modelNode.setLocalRotation(Quaternion.multiply(rotY, rotX));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }
}
