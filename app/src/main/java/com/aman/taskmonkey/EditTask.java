package com.aman.taskmonkey;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;


public class EditTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    int year, month, day, hour, minute;
    int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;

    int id;

    //For navigation view
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ImageButton drawer;
    RelativeLayout navMenu;
    RelativeLayout mainData;
    TextView homeButton;
    TextView overdueButton;
    TextView completedButton;


    boolean allright = true;


    //For Task
    boolean isNewTask;


    //Buttons
    Button dateTime;
    Button prioritySetting;
    Button status;
    EditText detailsInfo;

    String priority = "";
    String titleToSave;
    String displayDate = "";
    String subInformation = "";
    String reminder = "";
    String reminderSetting = "";
    String reminderHourAndSecond = "";
    String statusInfo = "";

    ArrayList<String> reminderDaysSelected = new ArrayList<>();

    java.util.Calendar newDateInfo;

    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        dateTime = (Button) findViewById(R.id.dateTime);
        prioritySetting = (Button) findViewById(R.id.priority);
        status = (Button) findViewById(R.id.status);
        title = (EditText) findViewById(R.id.title);
        detailsInfo = (EditText) findViewById(R.id.subInformation);


        newDateInfo =  java.util.Calendar.getInstance();

        setDefaults();

        createToolbar();

        settingDeadlineDate();

        reminderNotification();

    }

    public void createToolbar() {

        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            drawerLayout = (DrawerLayout) findViewById(R.id.activity_edit_task);
            drawer = (ImageButton) findViewById(R.id.drawerForEdit);

            ImageView deleteButton = (ImageView) findViewById(R.id.deleteButton);

            homeButton = (TextView) findViewById(R.id.Home);
            overdueButton = (TextView) findViewById(R.id.overDue);
            completedButton = (TextView) findViewById(R.id.CompletedButton);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            navMenu = (RelativeLayout) findViewById(R.id.nav_menu_for_edit);
            mainData = (RelativeLayout) findViewById(R.id.mainData_for_Edit);

            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);

            drawerLayout.setDrawerListener(toggle);

            final Context thisContext = this;

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
                    Intent intent = new Intent(EditTask.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            overdueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(EditTask.this, OverdueList.class);
                    startActivity(intent);
                }
            });
            completedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(EditTask.this, CompletedTasks.class);
                    startActivity(intent);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(thisContext)
                            .setTitle("Delete this task?")
                            .setMessage("Are you sure you want to delete this task?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!isNewTask) {
                                        MainActivity.taskDatabase.execSQL("DELETE FROM tasks WHERE id=" + id);
                                        MainActivity.taskDatabase.execSQL("DELETE FROM dates WHERE id=" + id);
                                    }

                                    Intent intent = new Intent(EditTask.this, MainActivity.class);
                                    startActivity(intent);


                                }
                            }).setNegativeButton("No", null)
                            .show();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


       /* final Context thisContext = this;

        toolbar.inflateMenu(R.menu.menu_for_edit);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.delete:
                        new AlertDialog.Builder(thisContext)
                                .setTitle("Delete this task?")
                                .setMessage("Are you sure you want to delete this task?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!isNewTask) {
                                            MainActivity.taskDatabase.execSQL("DELETE FROM tasks WHERE id=" + id);
                                            MainActivity.taskDatabase.execSQL("DELETE FROM dates WHERE id=" + id);
                                        }

                                        Intent intent = new Intent(EditTask.this, MainActivity.class);
                                        startActivity(intent);


                                    }
                                }).setNegativeButton("No", null)
                                .show();


                        return true;
                    default:
                        return false;

                }
            }
        });*/

    }


    public void setDefaults() {


        try {
            Intent intent = getIntent();

            id = intent.getIntExtra("id", -1);


            if (id == -1) {
                isNewTask = true;
                Toast.makeText(this, "New Task", Toast.LENGTH_SHORT).show();
            } else {
                isNewTask = false;
                Toast.makeText(this, "Old Task", Toast.LENGTH_SHORT).show();
            }


            if (isNewTask) {
                // Intent intent = getIntent();

                priority = intent.getStringExtra("priority");
                titleToSave = "Unnamed Task";
                this.subInformation = "";

                this.changedPriority(priority);
            } else {

                String actualTitle;
                String actualPriority;
                String actualdueDate;
                String actualtaskInfo;
                String actualreminder;
                String actualreminderType;
                String actualreminderTime;
                String actualStatus;
                Cursor c = MainActivity.taskDatabase.rawQuery("SELECT * FROM tasks WHERE id=" + String.valueOf(id), null);
                int titleIndex = c.getColumnIndex("title");
                int priorityIndex = c.getColumnIndex("priority");
                int dateIndex = c.getColumnIndex("duedate");
                int detailsIndex = c.getColumnIndex("taskinfo");
                int reminderIndex = c.getColumnIndex("reminder");
                int reminderTypeIndex = c.getColumnIndex("remindertype");
                int reminderTimeIndex = c.getColumnIndex("reminderhourandminute");
                int statusIndex = c.getColumnIndex("status");
                c.moveToFirst();
                actualTitle = c.getString(titleIndex);
                actualPriority = c.getString(priorityIndex);
                actualdueDate = c.getString(dateIndex);
                actualtaskInfo = c.getString(detailsIndex);
                actualreminder = c.getString(reminderIndex);
                actualreminderType = c.getString(reminderTypeIndex);
                actualreminderTime = c.getString(reminderTimeIndex);
                actualStatus = c.getString(statusIndex);

                priority = actualPriority;
                displayDate = actualdueDate;
                subInformation = actualtaskInfo;
                reminder = actualreminder;
                reminderSetting = actualreminderType;
                reminderHourAndSecond = actualreminderTime;
                statusInfo = actualStatus;

                c.close();

                Cursor c2 = MainActivity.taskDatabase.rawQuery("SELECT * FROM dates WHERE id=" + String.valueOf(id), null);

                if (c2.getCount() >= 1) {
                    while (c2.moveToNext()) {
                        int yearIndex = c2.getColumnIndex("year");
                        int monthIndex = c2.getColumnIndex("month");
                        int dayIndex = c2.getColumnIndex("day");
                        int hourIndex = c2.getColumnIndex("hour");
                        int minuteIndex = c2.getColumnIndex("minute");


                        int yearforDate = c2.getInt(yearIndex);
                        int monthforDate = c2.getInt(monthIndex);
                        int dayforDate = c2.getInt(dayIndex);
                        int hourforDate = c2.getInt(hourIndex);
                        int minuteforDate = c2.getInt(minuteIndex);

                        newDateInfo.set(Calendar.YEAR, yearforDate);
                        newDateInfo.set(Calendar.MONTH, monthforDate);
                        newDateInfo.set(Calendar.DATE, dayforDate);
                        newDateInfo.set(Calendar.HOUR_OF_DAY, hourforDate);
                        newDateInfo.set(Calendar.MINUTE, minuteforDate);

                        c2.close();
                    }
                } else {
                    Log.i("Error", "No such task found");
                }

                JSONObject json = new JSONObject(reminder);
                JSONArray items = json.optJSONArray("reminderArrays");
                reminderDaysSelected.clear();
                for (int i = 0; i < items.length(); i++) {
                    reminderDaysSelected.add(items.get(i).toString());
            }

                if (statusInfo == null) {
                    statusInfo = "Incomplete";
                }

                title.setText(actualTitle);
                dateTime.setText(actualdueDate);
                detailsInfo.setText(subInformation);
                switch (statusInfo) {
                    case "Incomplete":
                        status.setText("Status:Pending");
                        break;
                    case "Overdue":
                        status.setText("Status:Overdue");
                        break;
                    default:
                        status.setText("Status:Complete");
                        break;
                }
                this.changedPriority(priority);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void settingDeadlineDate() {
        dateTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c =Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditTask.this, EditTask.this, year, month, day);
                datePickerDialog.show();
            }
        });


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        //Use Find
        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EditTask.this, EditTask.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hourFinal = hourOfDay;
        minuteFinal = minute;

        Log.i("Hour of day from onTimeSet", String.valueOf(hourFinal));

        if (hourFinal < 12) {

            displayDate = yearFinal + "-" + String.format("%02d", monthFinal + 1) + "-" + String.format("%02d", dayFinal) + "\n" + String.format("%02d", hourFinal % 12) + ":" + String.format("%02d", minuteFinal) + " AM";

        } else if (hourFinal > 12) {

            displayDate = yearFinal + "-" + String.format("%02d", monthFinal + 1) + "-" + String.format("%02d", dayFinal) + "\n" + String.format("%02d", hourFinal % 12) + ":" + String.format("%02d", minuteFinal) + " PM";

        }else if (hourFinal==0){
            displayDate = yearFinal + "-" + String.format("%02d", monthFinal + 1) + "-" + String.format("%02d", dayFinal) + "\n" + "12" + ":" + String.format("%02d", minuteFinal) + " AM";

        }
        else
            displayDate = yearFinal + "-" + String.format("%02d", monthFinal + 1) + "-" + String.format("%02d", dayFinal) + "\n" + 12 + ":" + String.format("%02d", minuteFinal) + " PM";


        newDateInfo.set(Calendar.YEAR, yearFinal);
        newDateInfo.set(Calendar.MONTH, monthFinal);
        newDateInfo.set(Calendar.DATE, dayFinal);
        newDateInfo.set(Calendar.HOUR_OF_DAY, hourFinal);
        newDateInfo.set(Calendar.MINUTE, minuteFinal);

        Calendar c = Calendar.getInstance();

        if (newDateInfo.before(c) && !Objects.equals(statusInfo, "Complete")) {
            statusInfo = "Overdue";
        } else if (newDateInfo.after(c) && !Objects.equals(statusInfo, "Complete")) {
            statusInfo = "Incomplete";
        }
        switch (statusInfo) {
            case "Incomplete":
                status.setText("Status:Pending");
                break;
            case "Overdue":
                status.setText("Status:Overdue");
                break;
            default:
                status.setText("Status:Complete");
                break;
        }
        dateTime.setText(displayDate);
    }

    public void reminderChoice(View view) {
        final String[] choice = {"Daily", "Weekends", "Weekdays", "Custom"};
        new AlertDialog.Builder(this)
                .setTitle("Remind")
                .setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reminderSetting(choice[which]);
                    }
                }).show();
    }

    public void reminderSetting(String choice) {
        reminderDaysSelected.clear();
        switch (choice) {
            case "Daily":
                //Set the reminder as daily
                reminderDaysSelected.add("Sunday");
                reminderDaysSelected.add("Monday");
                reminderDaysSelected.add("Tuesday");
                reminderDaysSelected.add("Wednesday");
                reminderDaysSelected.add("Thursday");
                reminderDaysSelected.add("Friday");
                reminderDaysSelected.add("Saturday");
                setTime();
                break;
            case "Weekends":
                //Set the reminder as daily
                reminderDaysSelected.add("Saturday");
                reminderDaysSelected.add("Sunday");
                setTime();
                break;
            case "Weekdays":
                //Set the reminder as daily
                reminderDaysSelected.add("Monday");
                reminderDaysSelected.add("Tuesday");
                reminderDaysSelected.add("Wednesday");
                reminderDaysSelected.add("Thursday");
                reminderDaysSelected.add("Friday");
                setTime();
                break;
            default:
                setCustomDates();
                break;
        }
    }

    public void setTime() {
        timeChoice();
        reminderSetting = "choice";
    }


    final Calendar timeChosen = Calendar.getInstance();


    public void timeChoice() {

        TimePickerDialog timePicking = new TimePickerDialog(EditTask.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Set the reminder setting
                timeChosen.set(Calendar.HOUR_OF_DAY, hourOfDay);
                timeChosen.set(Calendar.MINUTE, minute);

                reminderHourAndSecond = hourOfDay + ":" + minute;

            }
        }, hour, minute, DateFormat.is24HourFormat(this));

        timePicking.show();

    }

    public void setCustomDates() {
        final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        final ArrayList<String> selectedDays = new ArrayList<>();
        new AlertDialog.Builder(this)
                .setTitle("Select days")
                .setMultiChoiceItems(days, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedDays.add(days[which]);
                        } else if (selectedDays.contains(days[which])) {
                            selectedDays.remove(days[which]);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTime();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
        reminderDaysSelected = selectedDays;
    }

    public void reminderNotification() {

        JSONObject json = new JSONObject();
        try {
            json.put("reminderArrays", new JSONArray(reminderDaysSelected));
            String reminderDaysSelected = json.toString();


            Intent intent = new Intent("my.action.string");
            intent.putExtra("id", id * 100000);
            intent.putExtra("Days", reminderDaysSelected);
            intent.putExtra("title",titleToSave);
            sendBroadcast(intent);

            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), id * 100000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeChosen.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);

            if(statusInfo.equals("Complete")){
                pendingIntent2.cancel();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void setPriority(View view) {
        final String[] priorities = {"High", "Medium", "Low"};


        new AlertDialog.Builder(this)
                .setTitle("Select Priority")
                .setItems(priorities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changedPriority(priorities[which]);
                    }
                })
                .show();
    }

    public void changedPriority(String choice) {

        try {
            this.priority = choice;
            switch (priority) {
                case "High":
                    prioritySetting.setBackgroundResource(R.drawable.highpriority);
                    break;
                case "Medium":
                    prioritySetting.setBackgroundResource(R.drawable.mediumpriority);
                    break;
                default:
                    prioritySetting.setBackgroundResource(R.drawable.lowpriority);
                    break;
            }
            prioritySetting.setText(priority + "\n" + "Priority");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void statusChanged(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Have you completed this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EditTask.this, "Congratulations", Toast.LENGTH_SHORT).show();
                        status.setText("Status:Complete");
                        statusInfo = "Complete";
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        status.setText("Status:Incomplete");
                        statusInfo = "Incomplete";
                    }
                })
                .show();

    }

    public void saveTask(View view) {
        titleToSave = title.getText().toString();
        subInformation = detailsInfo.getText().toString();

        try {
            allright = true;
            if (displayDate.equals("")) {
                Toast.makeText(this, "Setting a deadline helps complete a task. Please choose a date.", Toast.LENGTH_SHORT).show();
                allright = false;
            }
            if (allright) {
                setNotification();
                JSONObject json = new JSONObject();
                json.put("reminderArrays", new JSONArray(reminderDaysSelected));
                reminder = json.toString();

                if (isNewTask) {


                    //Use binding here perhaps
                    task newTask = new task(title.getText().toString(), MainActivity.highNotes.size());

                    String sql = "INSERT INTO tasks (title, priority,duedate,taskinfo,reminder,remindertype,reminderhourandminute,status) VALUES (?,?,?,?,?,?,?,?)";
                    SQLiteStatement statement = MainActivity.taskDatabase.compileStatement(sql);
                    statement.bindString(1, titleToSave);
                    statement.bindString(2, priority);
                    statement.bindString(3, displayDate);
                    statement.bindString(4, subInformation);
                    statement.bindString(5, reminder);
                    statement.bindString(6, reminderSetting);
                    statement.bindString(7, reminderHourAndSecond);
                    statement.bindString(8, statusInfo);
                    statement.execute();

                    Cursor c = MainActivity.taskDatabase.rawQuery("Select last_insert_rowid()", null);
                    c.moveToFirst();
                    id = c.getInt(0);
                    c.close();
                    //Toast.makeText(this, String.valueOf(id), Toast.LENGTH_SHORT).show();
                    statement.close();


                    String dateSql = "INSERT INTO dates (date, year, month, day, hour, minute, amorpm, id) VALUES (?,?,?,?,?,?,?,?)";
                    SQLiteStatement datestatement = MainActivity.taskDatabase.compileStatement(dateSql);
                    datestatement.bindString(1, displayDate);
                    /*datestatement.bindString(2, displayDate.substring(0, 4));
                    datestatement.bindString(3, displayDate.substring(5, 7));
                    datestatement.bindString(4, displayDate.substring(8, 10));
                    datestatement.bindString(5, displayDate.substring(11, 13));
                    datestatement.bindString(6, displayDate.substring(14, 16));
                    datestatement.bindString(7, displayDate.substring(17, 19));
                    datestatement.bindString(8, String.valueOf(id));*/
                    datestatement.bindString(2, String.valueOf(newDateInfo.get(Calendar.YEAR)));
                    datestatement.bindString(3, String.valueOf(newDateInfo.get(Calendar.MONTH)));
                    datestatement.bindString(4, String.valueOf(newDateInfo.get(Calendar.DATE)));
                    datestatement.bindString(5, String.valueOf(newDateInfo.get(Calendar.HOUR_OF_DAY)));
                    datestatement.bindString(6, String.valueOf(newDateInfo.get(Calendar.MINUTE)));
                    datestatement.bindString(7, displayDate.substring(17, 19));
                    datestatement.bindString(8, String.valueOf(id));
                    datestatement.execute();
                    datestatement.close();

                    /*try{
                        //Log.i("Length:",String.valueOf(displayDate.length()));
                        //Log.i("Date info:",displayDate);
                        //Log.i("Year:", displayDate.substring(0,4));
                        //Log.i("Month:", displayDate.substring(5,7));
                        //Log.i("Day:",displayDate.substring(8,10));
                        //Log.i("Hour:", displayDate.substring(11,13));
                        //Log.i("Minute:", displayDate.substring(14,16));
                        //Log.i("AMorPM:" ,displayDate.substring(17,19));}
                    catch(Exception e){
                        //Log.i("Waits","Wait for it");
                    }*/

                   /* sql="UPDATE tasks SET priority= ? WHERE id=" + id;
                    statement=MainActivity.taskDatabase.compileStatement(sql);
                    statement.bindString(1,priority);
                    statement.execute();*/

                    //This prints the data we have

                   /* MainActivity.taskDatabase.execSQL("INSERT INTO tasks (title, priority) VALUES ('" + titleToSave + "','" + priority + "')");
                    Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
*/

                    switch (this.priority) {
                        case "High":
                            MainActivity.highNotes.add(newTask.title);
                            MainActivity.forHigh.notifyDataSetChanged();

                            break;
                        case "Medium":
                            MainActivity.mediumNotes.add(newTask.title);
                            MainActivity.forMedium.notifyDataSetChanged();

                            break;
                        default:
                            MainActivity.lowNotes.add(newTask.title);
                            MainActivity.forLow.notifyDataSetChanged();

                            break;
                    }
                } else {
                    ////Log.i("Reached", "Here");

                    String sql = "UPDATE tasks SET title= ?, priority=?, duedate=?, taskinfo=?, reminder=?,remindertype=? ,reminderhourandminute=?, status=? WHERE id=" + id;
                    SQLiteStatement statement = MainActivity.taskDatabase.compileStatement(sql);
                    statement.bindString(1, titleToSave);
                    statement.bindString(2, priority);
                    statement.bindString(3, displayDate);
                    statement.bindString(4, subInformation);
                    statement.bindString(5, reminder);
                    statement.bindString(6, reminderSetting);
                    statement.bindString(7, reminderHourAndSecond);
                    statement.bindString(8, statusInfo);
                    statement.execute();
                    statement.close();

                    /*sql="UPDATE tasks SET priority= ? WHERE id=" + id;
                    statement=MainActivity.taskDatabase.compileStatement(sql);
                    statement.bindString(1,priority);
                    statement.execute();*/


                    //MainActivity.taskDatabase.execSQL("UPDATE tasks SET title= '" + titleToSave + "' WHERE id=" + id);
                    //MainActivity.taskDatabase.execSQL("UPDATE tasks SET priority= '" + priority + "' WHERE id=" + id);


                    String dateSql = "UPDATE dates SET date=?, year=?, month=?, day=?, hour=?, minute=?, amorpm=? WHERE id=" + id;
                    SQLiteStatement datestatement = MainActivity.taskDatabase.compileStatement(dateSql);
                    datestatement.bindString(1, displayDate);
                    /*datestatement.bindString(2, displayDate.substring(0, 4));
                    datestatement.bindString(3, displayDate.substring(5, 7));
                    datestatement.bindString(4, displayDate.substring(8, 10));
                    datestatement.bindString(5, displayDate.substring(11, 13));
                    datestatement.bindString(6, displayDate.substring(14, 16));
                    datestatement.bindString(7, displayDate.substring(17, 19));*/
                    datestatement.bindString(2, String.valueOf(newDateInfo.get(Calendar.YEAR)));
                    datestatement.bindString(3, String.valueOf(newDateInfo.get(Calendar.MONTH)));
                    datestatement.bindString(4, String.valueOf(newDateInfo.get(Calendar.DATE)));
                    datestatement.bindString(5, String.valueOf(newDateInfo.get(Calendar.HOUR_OF_DAY)));
                    datestatement.bindString(6, String.valueOf(newDateInfo.get(Calendar.MINUTE)));
                    datestatement.bindString(7, displayDate.substring(17, 19));

                    datestatement.execute();
                    datestatement.close();
                }
                ////Log.i("ReminderType",reminderSetting);
                ////Log.i("ReminderTime",reminderHourAndSecond);
                /*Cursor c = MainActivity.taskDatabase.rawQuery("SELECT * FROM tasks", null);

                int idIndex = c.getColumnIndex("id");
                int titleIndex = c.getColumnIndex("title");
                int priorityIndex = c.getColumnIndex("priority");
                int statusIndex = c.getColumnIndex("status");
*/

                //Print these to check
                /*if (c.getCount() >= 1) {
                    while (c.moveToNext()) {
                        String priority = c.getString(priorityIndex);
                        String titleOfTask = c.getString(titleIndex);
                        String idHere = String.valueOf(c.getInt(idIndex));
                        String statusHere=c.getString(statusIndex);
                        //Log.i("Id:", idHere);
                        //Log.i("Title of Task:", titleOfTask);
                        //Log.i("Priority:", priority);
                        //Log.i("Status:", statusHere);
                    }

                }*/

                /*
                 c=MainActivity.taskDatabase.rawQuery("SELECT * FROM dates",null);
                int idIndexdates=c.getColumnIndex("id");
                int dateIndex=c.getColumnIndex("date");
                int yearIndex=c.getColumnIndex("year");
                int monthIndex=c.getColumnIndex("month");
                int dayIndex=c.getColumnIndex("day");
                if (c.getCount() >= 1) {
                    while (c.moveToNext()) {
                        String idDate=c.getString(idIndexdates);
                        String tempDate = c.getString(dateIndex);
                        String tempYear = c.getString(yearIndex);
                        String tempMonth = c.getString(monthIndex);
                        String tempDay = String.valueOf(c.getInt(dayIndex));
                        //Log.i("Id:", idDate);
                        //Log.i("Date:", tempDate);
                        //Log.i("Year:", tempYear);
                        //Log.i("Month:", tempMonth);
                        //Log.i("Day:", tempDay);

                    }

                }else{
                    //Log.i("Oops:","Error");
                }
*/
                Intent intent = new Intent(EditTask.this, MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setNotification() {

        Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent("my.action.string.notification");
        intent.putExtra("id",id);
        intent.putExtra("title",titleToSave);

        //Calendar rightNow=Calendar.getInstance();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.i("Difference", String.valueOf(newDateInfo.getTimeInMillis()- Calendar.getInstance().getTimeInMillis()));

        System.out.println("Checking");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newDateInfo.getTimeInMillis());

        /*
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DATE);
        */

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, newDateInfo.getTimeInMillis(), pendingIntent);

        if(statusInfo.equals("Complete")){
            Log.i("Notification Cancelled","true");
            pendingIntent.cancel();
        }

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
