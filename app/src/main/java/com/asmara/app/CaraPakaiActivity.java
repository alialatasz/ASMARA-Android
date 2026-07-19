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
            InputStream in = getAssets().open("smartcard_asmara.pdf");
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            String fileName = "Smartcard_ASMARA_" + System.currentTimeMillis() + ".pdf";
            File outFile = new File(downloadDir, fileName);
            
            OutputStream out = new java.io.FileOutputStream(outFile);
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
            Toast.makeText(this, "Gagal mengunduh file PDF", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
