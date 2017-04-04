package com.ece.ing4.ppe.smartpillbox.smartpillbox.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.ece.ing4.ppe.smartpillbox.smartpillbox.MyGlobalVars;
import com.ece.ing4.ppe.smartpillbox.smartpillbox.R;
import com.ece.ing4.ppe.smartpillbox.smartpillbox.SmartPillboxMainActivity;

/**
 * Created by Kevin on 03/04/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // getting from intent
        String user_id = intent.getStringExtra(MyGlobalVars.TAG_USER_ID);
        String user_name = intent.getStringExtra(MyGlobalVars.TAG_NAME);
        String medical_staff = intent.getStringExtra(MyGlobalVars.TAG_MEDICAL_STAFF);

        // prepare to send
        Intent contentIntent = new Intent(context, SmartPillboxMainActivity.class);
        contentIntent.putExtra(MyGlobalVars.TAG_USER_ID,user_id);
        contentIntent.putExtra(MyGlobalVars.TAG_NAME, user_name);
        contentIntent.putExtra(MyGlobalVars.TAG_MEDICAL_STAFF,medical_staff);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(user_id), contentIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setSmallIcon(R.drawable.ic_menu_treatment)
                        .setContentTitle(MyGlobalVars.TAG_APP_NAME)
                        .setContentText(MyGlobalVars.TAG_NOTIFICATION_MESSAGE+user_id+user_name+medical_staff)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Integer.parseInt(user_id), notification);

    }
}
