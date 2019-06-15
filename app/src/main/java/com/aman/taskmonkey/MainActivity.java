package com.aman.taskmonkey;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

//problem in navitgation number

public class MainActivity extends AppCompatActivity {

    static SQLiteDatabase taskDatabase;

    //For navigation view
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ImageButton drawer;
    RelativeLayout navMenu;
    RelativeLayout mainData;
    TextView notificatoinNumber;
    ImageView notificationImage;
    TextView homeButton;
    TextView overdueButton;
    TextView completedButton;


    ListView highPriority;
    ListView mediumPriority;
    ListView lowPriority;

    static ArrayList<String> highNotes = new ArrayList<>();
    static ArrayList<String> mediumNotes = new ArrayList<>();
    static ArrayList<String> lowNotes = new ArrayList<>();

    ArrayList<Integer> dueToday = new ArrayList<>();
    ArrayList<Integer> overDue = new ArrayList<>();

    static ArrayAdapter forHigh;
    static ArrayAdapter forMedium;
    static ArrayAdapter forLow;

  //  @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificatoinNumber = (TextView) findViewById(R.id.notificatioinNumber);
        notificationImage = (ImageView) findViewById(R.id.notificationImage);

        highPriority = (ListView) findViewById(R.id.highPriority);
        mediumPriority = (ListView) findViewById(R.id.mediumPriority);
        lowPriority = (ListView) findViewById(R.id.lowPriority);

        createToolbar();


        forHigh = new ArrayAdapter(this, android.R.layout.simple_list_item_1, highNotes);
        forMedium = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mediumNotes);
        forLow = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lowNotes);

        highPriority.setAdapter(forHigh);
        mediumPriority.setAdapter(forMedium);
        lowPriority.setAdapter(forLow);

        setListenersForListView();
        initialize();
        checkDates();
        setNotificationNumber();


    }

    public void createToolbar() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
            drawer = (ImageButton) findViewById(R.id.drawer);

            homeButton = (TextView) findViewById(R.id.Home);
            overdueButton = (TextView) findViewById(R.id.overDue);
            completedButton = (TextView) findViewById(R.id.CompletedButton);

            //Still trying


            //trial

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            navMenu = (RelativeLayout) findViewById(R.id.nav_menu);
            mainData = (RelativeLayout) findViewById(R.id.mainData);

            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);

            drawerLayout.setDrawerListener(toggle);


            drawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (drawerLayout.isDrawerOpen(navMenu)) {
                        drawerLayout.closeDrawer(navMenu);
                    } else if (!drawerLayout.isDrawerOpen(navMenu)) {
                        drawerLayout.openDrawer(navMenu);
                    }
                }
            });

            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            overdueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, OverdueList.class);
                    startActivity(intent);
                }
            });
            completedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, CompletedTasks.class);
                    startActivity(intent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setListenersForListView() {
        highPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                openEditor(position, "High");
            }
        });
        mediumPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditor(position, "Medium");
            }
        });
        lowPriority.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openEditor(position, "Low");
            }
        });
    }


    public void checkDates() {
        dueToday.clear();
        overDue.clear();


        int year;
        int month;
        int day;
        int hour;
        int minute;
        int id;
        String amorpm;



        Calendar rightNow = Calendar.getInstance();

        try {
            //Cursor cursor=taskDatabase.rawQuery("SELECT * FROM dates WHERE year<="+currentYear+" AND month<="+currentMonth+" AND day<="+currentDay,null);
            //Cursor cursor = taskDatabase.rawQuery("SELECT * FROM dates WHERE year<=" + currentYear, null);
            Cursor cursor = taskDatabase.rawQuery("SELECT * FROM dates", null);
            int idIndex = cursor.getColumnIndex("id");
            int yearIndex = cursor.getColumnIndex("year");
            int monthIndex = cursor.getColumnIndex("month");
            int dayIndex = cursor.getColumnIndex("day");
            int hourIndex = cursor.getColumnIndex("hour");
            int minuteIndex = cursor.getColumnIndex("minute");
            int amorpmIndex = cursor.getColumnIndex("amorpm");
            if (cursor.getCount() >= 1) {
                while (cursor.moveToNext()) {

                    id = cursor.getInt(idIndex);
                    year = cursor.getInt(yearIndex);
                    month = cursor.getInt(monthIndex);
                    day = cursor.getInt(dayIndex);
                    hour = cursor.getInt(hourIndex);
                    minute = cursor.getInt(minuteIndex);

                    Calendar newSetDate = Calendar.getInstance();
                    newSetDate.set(Calendar.YEAR, year);
                    newSetDate.set(Calendar.MONTH, month);
                    newSetDate.set(Calendar.DATE, day);
                    newSetDate.set(Calendar.HOUR_OF_DAY, hour);
                    newSetDate.set(Calendar.MINUTE, minute);


                    Log.i("Set Date", "following");
                    Log.i("Year", String.valueOf(newSetDate.get(Calendar.YEAR)));
                    Log.i("Month", String.valueOf(newSetDate.get(Calendar.MONTH)));
                    Log.i("Date", String.valueOf(newSetDate.get(Calendar.DATE)));
                    Log.i("Hour", String.valueOf(newSetDate.get(Calendar.HOUR_OF_DAY)));
                    Log.i("Minute", String.valueOf(newSetDate.get(Calendar.MINUTE)));

                    Log.i("Current Date", "following");
                    Log.i("Year", String.valueOf(rightNow.get(Calendar.YEAR)));
                    Log.i("Month", String.valueOf(rightNow.get(Calendar.MONTH)));
                    Log.i("Date", String.valueOf(rightNow.get(Calendar.DATE)));
                    Log.i("Hour", String.valueOf(rightNow.get(Calendar.HOUR_OF_DAY)));
                    Log.i("Minute", String.valueOf(rightNow.get(Calendar.MINUTE)));

                    if (newSetDate.before(rightNow)) {
                        boolean updatingStatus = true;
                        Cursor cursorForChecking = taskDatabase.rawQuery("SELECT * FROM tasks WHERE id=" + id, null);
                        int statusIndex = cursorForChecking.getColumnIndex("status");
                        if (cursorForChecking.getCount() >= 1) {
                            //Log.i("Entering the if", "Condition");
                            while (cursorForChecking.moveToNext()) {
                                String statusChecked = cursorForChecking.getString(statusIndex);
                                //Log.i("Status", statusChecked);
                                if (statusChecked.equals("Complete")) {

                                    updatingStatus = false;
                                }
                            }
                        }

                        if (updatingStatus) {
                            overDue.add(id);

                            String updateStatus = "UPDATE tasks SET status='Overdue' WHERE id=" + id;
                            SQLiteStatement datestatement = MainActivity.taskDatabase.compileStatement(updateStatus);
                            datestatement.execute();
                            datestatement.close();
                        }
                    }


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {

            taskDatabase = this.openOrCreateDatabase("TaskList", MODE_PRIVATE, null);
            taskDatabase.execSQL("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY, title VARCHAR, status VARCHAR, reminder VARCHAR, remindertype VARCHAR, reminderhourandminute VARCHAR, priority VARCHAR, taskinfo VARCHAR, duedate VARCHAR)");
            // taskDatabase.execSQL("CREATE TABLE IF NOT EXISTS categories(id INTEGER PRIMARY KEY, category VARCHAR)");
            taskDatabase.execSQL("CREATE TABLE IF NOT EXISTS dates(id INTEGER PRIMARY KEY, date VARCHAR, year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, amorpm VARCHAR)");


            //taskDatabase.execSQL("DELETE FROM tasks");
            //taskDatabase.execSQL("DELETE FROM dates");


            Cursor c = taskDatabase.rawQuery("SELECT * FROM tasks", null);


            int titleIndex = c.getColumnIndex("title");
            int priorityIndex = c.getColumnIndex("priority");
            int statusIndex = c.getColumnIndex("status");


            if (c.getCount() >= 1) {
                highNotes.clear();
                lowNotes.clear();
                mediumNotes.clear();

                highNotes.add("Add a task...");
                mediumNotes.add("Add a task...");
                lowNotes.add("Add a task...");
                while (c.moveToNext()) {
                    String priority = c.getString(priorityIndex);
                    String titleOfTask = c.getString(titleIndex);
                    String status = c.getString(statusIndex);

                    if (!status.equals("Complete")) {

                        if (priority.equals("High")) {
                            highNotes.add(titleOfTask);
                            forHigh.notifyDataSetChanged();
                        } else if (priority.equals("Medium")) {
                            mediumNotes.add(titleOfTask);
                            forMedium.notifyDataSetChanged();
                        } else {
                            lowNotes.add(titleOfTask);
                            forLow.notifyDataSetChanged();
                        }
                    }

                }
                if (highNotes.size() == 0) highNotes.add("Add a note...");
                if (mediumNotes.size() == 0) mediumNotes.add("Add a note...");
                if (lowNotes.size() == 0) lowNotes.add("Add a note...");
            } else {
                highNotes.clear();
                lowNotes.clear();
                mediumNotes.clear();
                highNotes.add("Add a task...");
                mediumNotes.add("Add a task...");
                lowNotes.add("Add a task...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openEditor(int position, String priority) {
        Intent intent = new Intent(this.getApplicationContext(), EditTask.class);
        String titleOfTask = "";
        if (priority.equals("High")) {
            titleOfTask = highNotes.get(position);
        } else if (priority.equals("Medium")) {
            titleOfTask = mediumNotes.get(position);
        } else {
            titleOfTask = lowNotes.get(position);
        }


        try {
            Cursor c = taskDatabase.rawQuery("SELECT * FROM tasks WHERE title='" + titleOfTask + "'", null);
            if (c.moveToFirst()) {
                c.moveToFirst();
                int idIndex = c.getColumnIndex("id");
                int idToSend = c.getInt(idIndex);
                intent.putExtra("id", idToSend);


                startActivity(intent);
            } else {
                //  //Log.i("Priority being sent", priority);
                intent.putExtra("priority", priority);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNotificationNumber() {

        if (overDue.size() == 0) {
            notificatoinNumber.setVisibility(View.INVISIBLE);
        } else {
            notificatoinNumber.setVisibility(View.VISIBLE);
            notificatoinNumber.setText(String.valueOf(overDue.size()));
        }

        notificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OverdueList.class);
                startActivity(intent);
            }
        });
        notificatoinNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OverdueList.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(navMenu)) {
            drawerLayout.closeDrawer(navMenu);
        } else if (!drawerLayout.isDrawerOpen(navMenu)) {
            super.onBackPressed();
        }
    }
}
