package com.example.altuggemalmaz.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //This will be our link to the database
    private DatabaseReference mUserDatabase;

    //This will be the current FireBase user
    private FirebaseUser mCurrentUser;

    //In order to fetch the realtime data on the database we need the UI connections as usual
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);

        //The UI connections has to be established and initialized over here
        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_display_name);
        mStatus = (TextView) findViewById(R.id.settings_status);

        //The current user instance that has been logged in
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //On the realtime database in order to reach where the data is stored for the specific user, we need the user ID
        String current_uid = mCurrentUser.getUid();

        //To get to the data of the current user on the database tree we move to the location
        //where the data is stored on real time database
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


        //This will fetch the data on the database it fetches the data that is stored
        //It gets us something similar like a hashmap
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            //This function runs when there is a change on the database, it's a live action listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //It gets the child area of the data snapshot, it gets its value and turns it into string
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                //Now we have the proper real values it's time to add them to the view
                mName.setText(name);
                mStatus.setText(status);


            }

            //This runs to handle errors
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
