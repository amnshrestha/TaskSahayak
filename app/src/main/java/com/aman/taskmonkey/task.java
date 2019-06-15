package com.aman.taskmonkey;

import android.media.Image;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aman on 7/12/2018.
 */

public class task {

    String title;
    String status;
    String reminder;
    String priority;
    String taskInfo;

    int id;


    Date duedate;

    Time dueTime;

    ArrayList<String> Categories;

    public task(String title, int id){

        this.title=title;
        this.id=id;

    }















    public String getTitle() {
        return title;
    }
    public String getStatus() {
        return status;
    }
    public String getReminder() {
        return reminder;
    }
    public String getPriority() {
        return priority;
    }
    public String getTaskInfo() {
        return taskInfo;
    }
    public int getId() {
        return id;
    }
    public Date getDuedate() {
        return duedate;
    }
    public Time getDueTime() {
        return dueTime;
    }
    public ArrayList<String> getCategories() {
        return Categories;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
    public void setDueTime(Time dueTime) {
        this.dueTime = dueTime;
    }
    public void setCategories(ArrayList<String> categories) {
        Categories = categories;
    }



}
