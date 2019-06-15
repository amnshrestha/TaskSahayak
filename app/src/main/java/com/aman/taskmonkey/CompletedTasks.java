package com.aman.taskmonkey;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.HashMap;

public class CompletedTasks extends AppCompatActivity {

    ArrayList<String>titles=new ArrayList<String>();
    ArrayAdapter<String> forTitles;
    ArrayList<Integer>idList=new ArrayList<Integer>();
    ListView listOfTasks;
    TextView emptyTextView;


    //Copy Pastaed
    //For navigation view
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    ImageButton drawer;
    RelativeLayout navMenu;
    RelativeLayout mainData;
    Context con = this;
    TextView homeButton;
    TextView overdueButton;
    TextView completedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        listOfTasks=(ListView)findViewById(R.id.categoryItems);

        emptyTextView=(TextView)findViewById(R.id.emptyList);

        forTitles=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,titles);


        //might need to shift this to createToolbar()

        toolbar = (Toolbar) findViewById(R.id.toolbarForCompletedTasks);
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_completed_tasks);
        drawer = (ImageButton) findViewById(R.id.drawerForCompletedTasks);



        listOfTasks.setAdapter(forTitles);

        createToolbar();
        checkDatabase();
        listOfTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CompletedTasks.this,EditTask.class);
                intent.putExtra("id",idList.get(position));
                startActivity(intent);
            }
        });




    }

    public void createToolbar(){
        try {


            homeButton = (TextView) findViewById(R.id.Home);
            overdueButton = (TextView) findViewById(R.id.overDue);
            completedButton = (TextView) findViewById(R.id.CompletedButton);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            navMenu = (RelativeLayout) findViewById(R.id.nav_menu_for_completed);
            mainData = (RelativeLayout) findViewById(R.id.mainDataforCompletedTasks);

            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);

            drawerLayout.setDrawerListener(toggle);

            final Context thisContext = this;

            drawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Clicked", "Here");
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
                    Log.i("HEre","Here");
                    Intent intent = new Intent(CompletedTasks.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            overdueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("HEre","Here");
                    Intent intent = new Intent(CompletedTasks.this, OverdueList.class);
                    startActivity(intent);
                }
            });
            completedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("HEre","Here");
                    Intent intent = new Intent(CompletedTasks.this, CompletedTasks.class);
                    startActivity(intent);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkDatabase() {
        titles.clear();idList.clear();
        Cursor c = MainActivity.taskDatabase.rawQuery("SELECT * FROM tasks WHERE status='Complete'", null);

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
        if(titles.isEmpty()){
            listOfTasks.setVisibility(View.INVISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        }else{
            listOfTasks.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.INVISIBLE);
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

