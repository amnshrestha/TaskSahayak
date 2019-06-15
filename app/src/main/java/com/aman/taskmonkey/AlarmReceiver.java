package com.aman.taskmonkey;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by aman on 8/10/2018.
 */
public class AlarmReceiver extends BroadcastReceiver {

    int id = -1;

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent2 = new Intent(context, OverdueList.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String action = intent.getAction();

        checkDates();

        Log.i("Receiver", "Broadcast received: " + action);
        String title = "";

        if (action.equals("my.action.string.notification")) {

            id = intent.getExtras().getInt("id");
            title = intent.getExtras().getString("title");
            Log.i("State", String.valueOf(id));
        } else {
            Log.i("nothing", "found");
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Notification notification = builder.setContentTitle("Task Overdue")
                .setContentIntent(pendingIntent)
                .setContentText(title)
                .setTicker("New Alert!")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo2)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public void checkDates() {

        try {
            if (id != -1) {

                boolean updatingStatus = true;

                Cursor cursorForChecking = MainActivity.taskDatabase.rawQuery("SELECT * FROM tasks WHERE id=" + id, null);
                int statusIndex = cursorForChecking.getColumnIndex("status");
                if (cursorForChecking.getCount() >= 1) {
                    while (cursorForChecking.moveToNext()) {
                        String statusChecked = cursorForChecking.getString(statusIndex);
                        if (statusChecked.equals("Complete")) {
                            updatingStatus = false;
                        }
                    }
                }
                if (updatingStatus) {
                    String updateStatus = "UPDATE tasks SET status='Overdue' WHERE id=" + id;
                    SQLiteStatement datestatement = MainActivity.taskDatabase.compileStatement(updateStatus);
                    datestatement.execute();
                    datestatement.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
