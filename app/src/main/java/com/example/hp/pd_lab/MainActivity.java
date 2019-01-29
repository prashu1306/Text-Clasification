package com.example.hp.pd_lab;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    Toolbar toolbar;
    public static ArrayList<String> smsMessagesList = new ArrayList<>();
    public static ArrayList<String> List = new ArrayList<>();
    //Button buttonSend;
    ListView listViewMessages;
    ArrayAdapter arrayAdapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //drawerLayout =(DrawerLayout)findViewById(R.id.activity_main);
        listViewMessages = (ListView) findViewById(R.id.listViewMessages);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsMessagesList);
        listViewMessages.setAdapter(arrayAdapter);

        setUpToolbar();
        NavigationView navigationView=findViewById(R.id.nview);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ContentResolver contentResolver = getContentResolver();

        if(savedInstanceState==null) {
            //refreshSmsInbox();
             getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
             new all_fragment()).commit();
             navigationView.setCheckedItem(R.id.all);
        }
    }
    public void refreshSmsInbox() {

        ContentResolver contentResolver = getContentResolver();
        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {}
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }

    private void setUpToolbar(){
        drawerLayout =(DrawerLayout)findViewById(R.id.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //actionBarDrawerToggle.syncState();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {

      /* List = smsMessagesList;
        switch(item.getItemId()){
            case R.id.pm:
               arrayAdapter.clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new primary_fragment()).commit();
                break;
            case R.id.spam:
                arrayAdapter.clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Spam_fragment()).commit();

                break;
            case R.id.all:
                arrayAdapter.clear();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new all_fragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;*/
        Fragment fragment = null;
        switch(item.getItemId())
        {
            case R.id.pm:
                fragment = new primary_fragment((Context)this);
                break;
            case R.id.spam:
                fragment = new Spam_fragment((Context)this);
                break;
            case R.id.all:
                fragment = new all_fragment();
                break;

        }
        if(fragment != null)
        {
            FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,fragment);
            // Toast.makeText(this, "null pointer exception", Toast.LENGTH_LONG).show();
            ft.commit();   //error here
        }
        drawerLayout =(DrawerLayout)findViewById(R.id.activity_main);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawermenu,menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
      /*int id = item.getItemId();
      if(id == R.id.pm)
      {
          Toast.makeText(MainActivity.this, "This is my Toast message!", Toast.LENGTH_LONG).show();
      }
      else if(id == R.id.promo)
      {

      }
      else if(id == R.id.spam)
      {

      }*/
        return super.onOptionsItemSelected(item);
    }
   /* public  void displayselectedscreen(@NonNull MenuItem item)
    {
        Fragment fragment = null;
        switch(item.getItemId())
        {
            case R.id.pm:
                fragment = new primary_fragment();
                break;
            case R.id.spam:
                fragment = new Spam_fragment();
                break;
            case R.id.all:
                fragment = new all_fragment();
                break;

        }
        if(fragment != null)
        {
            FragmentTransaction ft = MainActivity.this.getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,fragment);
            // Toast.makeText(this, "null pointer exception", Toast.LENGTH_LONG).show();
            ft.commit();   //error here
        }
        drawerLayout =(DrawerLayout)findViewById(R.id.activity_main);
        drawerLayout.closeDrawer(GravityCompat.START);
    }*/

}
