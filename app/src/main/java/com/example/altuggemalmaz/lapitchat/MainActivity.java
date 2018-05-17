package com.example.altuggemalmaz.lapitchat;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;

    //Access To The Toolbar Element On UI
    private android.support.v7.widget.Toolbar mToolbar;

    //To access the viewPager that is on the UI that covers the blank space
    private ViewPager mViewPager;
    private SectionsPageAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    //Database connection for online feature
    private DatabaseReference mOnlineRef;

    //Online Feature
    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            //Sends the view to the start activity
            sendtostart();
        } else
        {
            //Set the status to be online
            mOnlineRef.setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Set the status to be offline
        if(mAuth.getCurrentUser() != null)
        {
            mOnlineRef.setValue(false);

            //Also update the last seen variable
            //FireBase server timeStamp is sent to the server
            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("lastSeen").setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get rid of the title toolbar that is given unneceserally
       // requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //Initialize the toolbar
        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_toolbar);
        //mToolbar.setTitle("Lapit Chat");

        //Just initialize the ViewPager for the Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        //Initialize the class which will handle the tabs
        mSectionsPagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //For the viewPager the adapter will be the class we have created
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        //Database Link
        if (mAuth.getCurrentUser() != null)
        mOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("online");


    }

    //When the main activity view is on this code will run first
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
            //Sends the view to the start activity
            sendtostart();
        } else
            {
                //Set the status to be online
                mOnlineRef.setValue(true);
            }
    }

    private void sendtostart() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        //Redirect the user to the startActivity view
        startActivity(startIntent);
        //Make this so that the user can't access the main view through the back button
        finish();
    }

    //This will get the menu for us
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //This sticks the menu to the toolbar the name of the file is main_menu.xml so in the menu directory main_menu.xml
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }


    //When there is a click on the menu tab
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //According to which item is clicked
        if (item.getItemId() == R.id.main_logout_btn)
        {
            FirebaseAuth.getInstance().signOut();
            sendtostart();
        }

        if (item.getItemId() == R.id.main_settings_btn)
        {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_all_btn)
        {
            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }
        return true;
    }
}
