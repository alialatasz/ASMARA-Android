package com.asmara.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;

/**
 * Helper class untuk menjadwalkan dan membatalkan notifikasi harian ASMARA.
 */
public class NotifHelper {

    // Jam notifikasi: 16:00 (4 sore)
    private static final int JAM_NOTIF = 16;
    private static final int MENIT_NOTIF = 0;

    /**
     * Menjadwalkan notifikasi harian setiap pukul 16:00.
     * Jika jam sudah lewat hari ini, dijadwalkan untuk besok.
     */
    public static void jadwalkanNotifikasi(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Hitung waktu notifikasi berikutnya (hari ini pukul 16:00 atau besok jika sudah lewat)
        Calendar kalender = Calendar.getInstance();
        kalender.set(Calendar.HOUR_OF_DAY, JAM_NOTIF);
        kalender.set(Calendar.MINUTE, MENIT_NOTIF);
        kalender.set(Calendar.SECOND, 0);
        kalender.set(Calendar.MILLISECOND, 0);

        // Jika jam sudah lewat hari ini, jadwalkan besok
        if (kalender.getTimeInMillis() <= System.currentTimeMillis()) {
            kalender.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Gunakan setExactAndAllowWhileIdle agar notifikasi tetap tepat waktu di Doze mode
        // Gunakan try-catch karena di Android 14+ (API 34) ini bisa memicu SecurityException jika izin ditolak
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, kalender.getTimeInMillis(), pendingIntent);
                } else {
                    // Fallback jika tidak ada izin Exact Alarm
                    alarmManager.set(AlarmManager.RTC_WAKEUP, kalender.getTimeInMillis(), pendingIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, kalender.getTimeInMillis(), pendingIntent);
            }
        } catch (SecurityException e) {
            // Fallback terakhir jika tetap terjadi error keamanan
            alarmManager.set(AlarmManager.RTC_WAKEUP, kalender.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Membatalkan semua notifikasi terjadwal ASMARA.
     */
    public static void batalkanNotifikasi(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }
}
