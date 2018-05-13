package com.example.altuggemalmaz.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    //Initialize the UI links to work with it
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn;

    //FireBase Link
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the UserID in order to show the content according to the user
        String user_id = getIntent().getStringExtra("user_id");

        //FireBase Link
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        //Initialize the link to the UI
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);

        //Get the Data snapshot from the FireBase for the specific user and then update the fields accordingly
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                //If there is a image that is stored on the Database
                if (!image.equals("default"))
                {
                    //Location where the file is stored
                    //If you don't want the picture to disappear when it's grabbed from the database
                    //You can do after load placeholder(R.something) this shows a temporary image to the user while the original picture
                    //Is received from the database
                    Picasso.with(ProfileActivity.this).load(image).into(mProfileImage);
                }

                //Now we have the proper real values it's time to add them to the view
                mProfileName.setText(name);
                mProfileStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
