package com.asmara.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.PixelCopy;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Viewer3DActivity extends AppCompatActivity {

    private SceneView sceneView;
    private Node modelNode;
    
    // Variabel untuk gestur
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private float currentScale = 0.5f;
    private float rotationAngleX = 0f;
    private float rotationAngleY = 30f;

    // Data bangun ruang dari QR / Petualangan
    private String tipeBangun = "GEDUNG";
    private String namaBangun = "Gedung Papak";
    private String bagianGedung = "Gedung Papak (Utuh)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_3d);

        // Ambil data dari Intent (dari QR Scanner atau Petualangan)
        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            tipeBangun = incomingIntent.getStringExtra("TIPE_BANGUN");
            namaBangun = incomingIntent.getStringExtra("NAMA_BANGUN");
            bagianGedung = incomingIntent.getStringExtra("BAGIAN_GEDUNG");

            // Fallback jika data kosong
            if (tipeBangun == null) tipeBangun = "GEDUNG";
            if (namaBangun == null) namaBangun = "Gedung Papak";
            if (bagianGedung == null) bagianGedung = "Gedung Papak (Utuh)";
        }

        sceneView = findViewById(R.id.scene_view);
        TextView tvInfo = findViewById(R.id.tv_info_viewer);
        MaterialButton btnMateri = findViewById(R.id.btn_edukasi_viewer);
        FloatingActionButton btnBack = findViewById(R.id.btn_back_viewer);
        TextView tvJudul = findViewById(R.id.tv_judul_viewer);
        TextView tvSubjudul = findViewById(R.id.tv_subjudul_viewer);

        // GARANSI: Memaksa UI untuk tampil di atas kanvas 3D
        findViewById(R.id.header_bar_viewer).bringToFront();
        findViewById(R.id.bottom_bar_viewer).bringToFront();

        // Update judul header sesuai bangun ruang
        if (tvJudul != null) {
            tvJudul.setText(namaBangun);
        }
        if (tvSubjudul != null) {
            tvSubjudul.setText("Bagian: " + bagianGedung);
        }

        // Update teks tombol
        btnMateri.setText("Lihat Materi " + namaBangun);

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnMateri.setOnClickListener(v -> {
            Intent intent = new Intent(Viewer3DActivity.this, MateriDetailActivity.class);
            // Gunakan tipeBangun (misal: "Balok") untuk membuka materi yang sesuai
            intent.putExtra("MATERI_TYPE", tipeBangun);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        FloatingActionButton btnScreenshot = findViewById(R.id.btn_screenshot);
        if (btnScreenshot != null) {
            btnScreenshot.setOnClickListener(v -> takeScreenshotAndShare());
        }

        // Setup Detektor Gestur (Cubit dan Geser)
        setupGestures();

        // Meneruskan sentuhan dari SceneView ke detektor gestur
        sceneView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            gestureDetector.onTouchEvent(event);
            return true;
        });

        // Tentukan file model 3D berdasarkan tipe bangun ruang
        String namaFileModel = tentukanFileModel(tipeBangun);

        // Update pesan loading
        tvInfo.setText("Memuat model 3D " + namaBangun + "...");

        ModelRenderable.builder()
                .setSource(this, android.net.Uri.parse(namaFileModel))
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

                    tvInfo.setText(namaBangun + " berhasil dimuat!\nGeser 1 jari untuk memutar\nCubit 2 jari untuk memperbesar");
                })
                .exceptionally(throwable -> {
                    tvInfo.setText("Model 3D \"" + namaFileModel + "\" belum tersedia.\nFile .glb/.gltf perlu ditambahkan ke folder assets.");
                    return null;
                });
    }

    /**
     * Menentukan nama file model .glb berdasarkan tipe bangun ruang.
     * Jika model spesifik belum ada, fallback ke gedung.glb (model yang sudah ada).
     */
    private String tentukanFileModel(String tipe) {
        switch (tipe) {
            case "BALOK":
                return "balok.glb";
            case "KUBUS":
                return "kubus.glb";
            case "LIMAS":
                return "limas.glb";
            case "PRISMA":
                return "prisma.glb";
            case "TABUNG":
                return "tabung.glb";
            case "PERSEGI_PANJANG":
                return "persegi_panjang.glb";
            case "GEDUNG":
            default:
                return "gedung.glb";
        }
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

    private void takeScreenshotAndShare() {
        // PixelCopy membutuhkan Surface dari SceneView
        if (sceneView == null) return;
        
        Toast literalToast = Toast.makeText(this, "Mengambil foto...", Toast.LENGTH_SHORT);
        literalToast.show();

        // Siapkan bitmap
        Bitmap bitmap = Bitmap.createBitmap(sceneView.getWidth(), sceneView.getHeight(), Bitmap.Config.ARGB_8888);
        
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        
        PixelCopy.request(sceneView, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    // Buat file sementara
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    String fileName = "ASMARA_" + timeStamp + ".jpg";
                    File cachePath = new File(getExternalCacheDir(), "images");
                    cachePath.mkdirs(); // don't forget to make the directory
                    File imagePath = new File(cachePath, fileName);
                    
                    FileOutputStream stream = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    stream.close();
                    
                    // Share Intent
                    Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", imagePath);
                    if (contentUri != null) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Lihat eksplorasi 3D saya di aplikasi ASMARA! #BelajarGeometri");
                        
                        // Menjalankan di UI thread
                        runOnUiThread(() -> {
                            literalToast.cancel();
                            startActivity(Intent.createChooser(shareIntent, "Bagikan karya 3D-mu via"));
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(Viewer3DActivity.this, "Gagal memproses foto", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(Viewer3DActivity.this, "Gagal mengambil foto", Toast.LENGTH_SHORT).show());
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }
}
