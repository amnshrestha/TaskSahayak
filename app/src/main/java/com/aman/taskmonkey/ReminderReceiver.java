package com.aman.taskmonkey;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReminderReceiver extends BroadcastReceiver {

    ArrayList<String> reminderDays = new ArrayList<>();

    boolean toCheck=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String title="";
        String action = intent.getAction();
        int id=-1;
        Log.i("Receiver", "Broadcast received: " + action);

        if (action.equals("my.action.string")) {

            id = intent.getExtras().getInt("id");
            title = intent.getExtras().getString("title");
            String reminderDaysSelected = intent.getExtras().getString("Days");


            StringTokenizer st = new StringTokenizer(reminderDaysSelected, "\"");
            reminderDays.clear();
            while (st.hasMoreTokens()) {
                String current = st.nextToken();


                if (!current.equals("reminderArrays") && !current.equals("{") && !current.equals(":[") && !current.equals(",") && !current.equals("]}")) {
                    reminderDays.add(current);
                }
            }




            toCheck = checkIfNecessary();



        }
        if (toCheck) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent repeating_intent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, id, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(android.R.drawable.arrow_up_float)
                    .setContentTitle(title)
                    .setContentText("Remember to complete your task")
                    .setAutoCancel(true);
            notificationManager.notify(id, builder.build());

        }



      }



    public boolean checkIfNecessary() {
        int day = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);
        String dayName = "";
        switch (day) {
            case Calendar.SUNDAY:
                dayName = "Sunday";
                break;
            case Calendar.MONDAY:
                dayName = "Monday";
                break;
            case Calendar.TUESDAY:
                dayName = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayName = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayName = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayName = "Friday";
                break;
            case Calendar.SATURDAY:
                dayName = "Saturday";
                break;

        }

        if (reminderDays.contains(dayName)) {
            return true;
        } else {
            return false;
        }
    }
}
