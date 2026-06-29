package com.asmara.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirebaseManager {
    private static FirebaseDatabase database;
    private static DatabaseReference myRef;
    private static String uniqueId;

    public static void init(Context context) {
        if (database == null) {
            // Kita harus secara eksplisit memasukkan URL karena database berada di region asia-southeast1,
            // sedangkan google-services.json mungkin di-download sebelum database ini dibuat.
            database = FirebaseDatabase.getInstance("https://asmara-44eec-default-rtdb.asia-southeast1.firebasedatabase.app");
            myRef = database.getReference("users");
        }
        
        SharedPreferences prefs = context.getSharedPreferences("asmara_prefs", Context.MODE_PRIVATE);
        uniqueId = prefs.getString("user_id", null);
        
        // Buat ID unik untuk HP anak ini jika belum ada
        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
            prefs.edit().putString("user_id", uniqueId).apply();
        }
    }

    public static void backupDataProfil(Context context) {
        if (myRef == null || uniqueId == null) init(context);
        
        SharedPreferences prefs = context.getSharedPreferences("asmara_prefs", Context.MODE_PRIVATE);
        String nama = prefs.getString("nama_anak", "Pemain Misterius");
        
        // Jangan sinkronkan jika nama masih kosong atau misterius, kecuali ada aktivitas kuis
        if (nama.equals("Pemain Misterius") && prefs.getInt("kuis_selesai", 0) == 0) {
            return;
        }

        int totalXp = prefs.getInt("total_xp", 0);
        int totalBintang = prefs.getInt("total_bintang", 0);
        int kuisSelesai = prefs.getInt("kuis_selesai", 0);
        int skorTerbaik = prefs.getInt("skor_terbaik", 0);
        int avatarIndex = prefs.getInt("avatar_index", 0);

        Map<String, Object> userData = new HashMap<>();
        userData.put("nama", nama);
        userData.put("total_xp", totalXp);
        userData.put("total_bintang", totalBintang);
        userData.put("kuis_selesai", kuisSelesai);
        userData.put("skor_terbaik", skorTerbaik);
        userData.put("avatar_index", avatarIndex);
        userData.put("last_sync", System.currentTimeMillis());

        // Update ke Firebase (otomatis antre jika sedang offline)
        myRef.child(uniqueId).setValue(userData);
    }
}
