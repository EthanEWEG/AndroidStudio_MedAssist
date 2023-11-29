package algonquin.cst2335.medassist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CancelReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            PendingIntent pendingIntent = intent.getParcelableExtra("key");
            Log.d("key4", "Received cancellation intent for PendingIntent: " + pendingIntent.toString());
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);

    }
}

