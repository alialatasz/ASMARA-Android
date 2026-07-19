package com.asmara.app;

import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CaraPakaiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cara_pakai);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_cara_pakai);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cara Pakai");
        }
        toolbar.setNavigationOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        MaterialButton btnDownload = findViewById(R.id.btn_download_smartcard);
        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> downloadSmartcard());
        }
    }

    private void downloadSmartcard() {
        try {
            InputStream in = getAssets().open("ASMARACard.pdf");
            String fileName = "ASMARACard.pdf";
            java.io.OutputStream out;
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                android.content.ContentResolver resolver = getContentResolver();
                android.content.ContentValues contentValues = new android.content.ContentValues();
                contentValues.put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS);
                android.net.Uri uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                out = resolver.openOutputStream(uri);
            } else {
                java.io.File downloadDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
                if (!downloadDir.exists()) downloadDir.mkdirs();
                java.io.File outFile = new java.io.File(downloadDir, fileName);
                out = new java.io.FileOutputStream(outFile);
                
                android.media.MediaScannerConnection.scanFile(this, 
                    new String[]{outFile.getAbsolutePath()}, 
                    new String[]{"application/pdf"}, null);
            }
            
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            
            Toast.makeText(this, "Berhasil! File PDF tersimpan di folder Download HP Anda", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
