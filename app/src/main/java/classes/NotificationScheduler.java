package classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.petnotes.R;

import java.util.Calendar;

public class NotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    public static void scheduleNotification(Context context, String title, String message, PendingIntent pendingIntent) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long currentTimeMillis = SystemClock.elapsedRealtime();
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTimeMillis, pendingIntent);
            Log.d(TAG, "Notification scheduled for current time: " + currentTimeMillis);
        } else {
            Log.e(TAG, "Failed to get AlarmManager");
        }
    }
}

