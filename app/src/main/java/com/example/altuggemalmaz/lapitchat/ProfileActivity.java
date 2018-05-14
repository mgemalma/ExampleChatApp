package com.example.altuggemalmaz.lapitchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    //Initialize the UI links to work with it
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileCancelReq;

    //FireBase Link
    private DatabaseReference mDatabase;

    //Get the current users id who uses this chat currently
    private FirebaseUser mCurrent_user;

    //This will be the current state with the user profile that is viewed
    private String mCurrent_state;

    //In order to deal with the friend requests we need another Data Base reference
    private DatabaseReference mFriendReqDatabase;

    //In order to create a term as a friend we are create another database reference
    private DatabaseReference mFriendDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the UserID in order to show the content according to the user
        final String user_id = getIntent().getStringExtra("user_id");

        //The default string will be not friends
        mCurrent_state = "not_friends";

        //FireBase Link
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        //Get the current users link
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        //Initialize the link to the UI
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mProfileCancelReq = (Button) findViewById(R.id.profile_decline_req_btn);

        //Make the cancel request button disappear at first
        mProfileCancelReq.setVisibility(View.GONE);


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

                //Friend requests part according to the database if we have a request or if we sent a request
                //The button should be updated

                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Check if the current user has a relationship with the profile of the user its checking
                        if (dataSnapshot.hasChild(user_id))
                        {
                            //According to the reqType update the button of the requests
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received"))
                            {
                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                //Make the button appear again because we want to be able to decline a request now
                                mProfileCancelReq.setVisibility(View.VISIBLE);


                            } else if (req_type.equals("sent"))
                            {
                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Check if the current user is friends with the user that its looking its profile
                mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                    //If friends set up the fields accordingly
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id))
                        {
                            mCurrent_state = "friends";
                            mProfileSendReqBtn.setText("Unfriend");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //Now we have the proper real values it's time to add them to the view
                mProfileName.setText(name);
                mProfileStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //When this button is active when clicked on it it means that a request is received and we want to reject it
        mProfileCancelReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we are going to clear the database from the request
                //First delete the request trace from the current user
                mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                    //When the task is on success we also want to delete it from the other user
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(ProfileActivity.this, "Request Deleted!", Toast.LENGTH_SHORT).show();

                                //Also change the button to be cancel friend request once the request is sent
                                mCurrent_state = "not_friends";
                                mProfileSendReqBtn.setText("SEND FRIEND REQUEST");

                                //Hide this button since it won't be needed anymore
                                mProfileCancelReq.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });

        //This listener will get to run when the send request button is clicked
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /**  Not Friends State (This will only get to run when there is no connection between the users) **/
                //When the button is clicked we know that the request is already been sent consequently we don't
                //want the user to click on the button again
                //The button will only be available if the button changes to cancel friend request
                //If that is not happening it will disable itself

                mProfileSendReqBtn.setEnabled(false);

                if (mCurrent_state.equals("not_friends"))
                {
                    //For our requests we send the request so for our side the request_type should be sent
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            //Only update the other hand if the task is succesful
                            if (task.isSuccessful())
                            {
                                //For the other side the receiver of the request it should be received
                                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("received")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Show that the sending a friend request is success
                                        Toast.makeText(ProfileActivity.this, "Request Sent!", Toast.LENGTH_SHORT).show();

                                        //Also change the button to be cancel friend request once the request is sent
                                        mProfileSendReqBtn.setEnabled(true);
                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");
                                    }
                                });
                            } else
                                {
                                    //Show the error
                                    Toast.makeText(ProfileActivity.this, "Failed sending request!", Toast.LENGTH_SHORT).show();
                                }

                        }
                    });

                }

                /** This will get to run when the request is sent by the user basically when the request is sent **/
                if (mCurrent_state.equals("req_sent"))
                {
                    //we are going to clear the database from the request
                    //First delete the request trace from the current user
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                        //When the task is on success we also want to delete it from the other user
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Request Deleted!", Toast.LENGTH_SHORT).show();

                                    //Also change the button to be cancel friend request once the request is sent
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("SEND FRIEND REQUEST");
                                }
                            });
                        }
                    });

                }

                /** We are now putting down the fact that when the request is received by the user in that case
                 * the user should be able to accept that request **/

                //When this statement runs this means that the user accepted the request
                if (mCurrent_state.equals("req_received"))
                {

                    //The current Date time to store it on the Database
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    //Now this entry should be added to the database because we are saving the value and adding the user as a friend
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {

                         //We also need to update the other users friends database
                        @Override
                        public void onSuccess(Void aVoid) {

                            //Friend is being updated on the database reciprocally
                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {

                                //When the all database updating is done
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //Now the database should be updated to so that the request is deleted
                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

                                        //When the task is on success we also want to delete it from the other user
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Toast.makeText(ProfileActivity.this, "Request Deleted!", Toast.LENGTH_SHORT).show();

                                                    //Also change the button to be cancel friend request once the request is sent
                                                    mProfileSendReqBtn.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    mProfileSendReqBtn.setText("Unfriend");

                                                    //Hide this button since it won't be needed anymore we accepted the request
                                                    mProfileCancelReq.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }


                //If the current state is friends and
                if (mCurrent_state.equals("friends"))
                {
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    //Make the button to be clickable again
                                    mProfileSendReqBtn.setEnabled(true);

                                    //Set the current state to be not friends
                                    mCurrent_state = "not_friends";

                                    //Make the button send friend request again
                                    mProfileSendReqBtn.setText("SEND FRIEND REQUEST");
                                }
                            });
                        }
                    });
                }

            }
        });
    }
}
