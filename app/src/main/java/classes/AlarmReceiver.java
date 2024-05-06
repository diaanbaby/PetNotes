package classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.petnotes.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String noteName = intent.getStringExtra("note_name");
        String noteDate = intent.getStringExtra("note_date");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.example.petnotes.channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Pet note!")
                .setContentText(noteName + " " + noteDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }
}
