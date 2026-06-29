package com.asmara.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "asmara_belajar_channel";
    public static final int NOTIF_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        // Intent untuk membuka MainActivity saat notifikasi diklik
        Intent openApp = new Intent(context, MainActivity.class);
        openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Pesan notifikasi yang ramah anak, berganti-ganti setiap hari
        String[] pesanList = {
            "Yuk, jelajahi Gedung Papak dan uji pemahamanmu hari ini! 🏛️",
            "Sudah belajar sejarah Salatiga belum? Ayo, kamu pasti bisa! 💪",
            "Hei! Ada kuis seru menantimu di ASMARA! Siap bermain? 🧠",
            "Jangan lupa belajar! Maskot ASMARA sudah menunggumu lho! 🦉",
            "Hari ini kamu mau dapat berapa bintang? Ayo main ASMARA! ⭐"
        };
        int hariIni = (int) (System.currentTimeMillis() / 86400000 % pesanList.length);
        String pesan = pesanList[hariIni];

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Halo! Waktunya Belajar Sejarah! 🦉")
                .setContentText(pesan)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(pesan))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 200, 100, 200});

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIF_ID, builder.build());
        }

        // Jadwalkan ulang untuk hari berikutnya
        NotifHelper.jadwalkanNotifikasi(context);
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Belajar ASMARA",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi harian pengingat belajar sejarah Salatiga");
            channel.enableVibration(true);
            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
