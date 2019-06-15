package com.aman.taskmonkey;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryTasks extends AppCompatActivity {

    ArrayList<String>titles=new ArrayList<String>();
    ArrayAdapter<String> forTitles;
    ArrayList<Integer>idList=new ArrayList<Integer>();
    ListView listOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_tasks);

        listOfTasks=(ListView)findViewById(R.id.categoryItems);

        forTitles=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,titles);

        listOfTasks.setAdapter(forTitles);



        Intent intent=getIntent();
        String categoryName=intent.getStringExtra("categoryName");
        checkDatabase(categoryName);
        onClickListeners();

    }

    public void checkDatabase(String name) {
        titles.clear();idList.clear();
        Cursor c = MainActivity.taskDatabase.rawQuery("SELECT * FROM tasks WHERE status='Overdue'", null);

        int idIndexdates = c.getColumnIndex("id");
        int titleIndex = c.getColumnIndex("title");
        if (c.getCount() >= 1) {
            while (c.moveToNext()) {
                String id = c.getString(idIndexdates);
                String title = c.getString(titleIndex);
                titles.add(title);
                idList.add(Integer.parseInt(id));
            }
        }
        forTitles.notifyDataSetChanged();
    }

    public void onClickListeners(){

       listOfTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent=new Intent(CategoryTasks.this,EditTask.class);
               intent.putExtra("id",idList.get(position));
           }
       });

    }
}
