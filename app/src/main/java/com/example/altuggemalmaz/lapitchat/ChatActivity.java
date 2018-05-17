package com.example.altuggemalmaz.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {


    //The user with which we are going to send messages to
    private String mChatUser;

    //Toolbar Global Variable
    private Toolbar mChatToolbar;

    //Database Reference
    private DatabaseReference mUserDatabase;

    //Initializing The UI connections
    TextView displayName;
    TextView lastSeen;
    ImageView imageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Initialize the toolbar
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        //mChatToolbar.setTitle("Chat Page");

        //Get the user_id of the
        mChatUser = getIntent().getStringExtra("user_id");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);

        //Initialize the UI connections
        displayName = (TextView) findViewById(R.id.chat_display_name);
        lastSeen = (TextView) findViewById(R.id.chat_last_seen);
        imageUser = (ImageView) findViewById(R.id.chat_bar_image);

        //Get the name of the user
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                displayName.setText(name);
                String image = dataSnapshot.child("image").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();

                if (!image.equals("default"))
                {
                    Picasso.with(ChatActivity.this).load(image).into(imageUser);
                }

                //If the lastSeen of the user exists then update the last seen bar accordingly
                if (dataSnapshot.hasChild("lastSeen")){
                    if ( online.equals("true"))
                    {
                       lastSeen.setText("Online");
                    }
                 else if (online.equals("false"))
                    {
                        String lastSeenDB = dataSnapshot.child("lastSeen").getValue().toString();
                        lastSeen.setText(lastSeenDB);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
