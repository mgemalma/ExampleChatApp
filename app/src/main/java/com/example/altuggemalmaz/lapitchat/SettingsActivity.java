package com.example.altuggemalmaz.lapitchat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    //This will be our link to the database
    private DatabaseReference mUserDatabase;

    //This will be the current FireBase user
    private FirebaseUser mCurrentUser;

    //The storage reference so that the profile images can be stored on the FireBase

    //In order to fetch the realtime data on the database we need the UI connections as usual
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    //The Buttons (Link with the UI)
    private Button statusBtn;
    private Button imageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);

        //Initializing The Buttons
        statusBtn = (Button) findViewById(R.id.settings_status_btn);
        imageBtn = (Button) findViewById(R.id.settings_image_btn);

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

        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //In order to show the status on the status string we send the current status to the
                //Status view, through the intent we can do that, so we first receieve the status
                String status = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);

                //Over here we are sending the status value to the status activity
                status_intent.putExtra("status_value",status);

                startActivity(status_intent);
            }
        });


        //This will get to run when change image button is clicked
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //This code is to select a image or something
                //You initialize the intent
                Intent galleryIntent = new Intent();

                //You select the intent type
                galleryIntent.setType("image/*");

                //Specify that through this you want to get the content
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                //With this you start the Intent
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),1);


                /*
                // start picker to get image for cropping and then use the image in cropping activity
                //This is way too easier than using the code above this code is library imported from another repo
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
                */

            }
        });
    }

    //This will run when the image is selected for the user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Request Code is the code we passed startActivityForResult
        if (requestCode == 1 && resultCode == RESULT_OK)
        {

            //Get the image data from the activity
            Uri imageUri = data.getData();

            //Pass the image data to this so that the image can be cropped
            //setAspectRatio will constrain the crop size to be square so that the cropped image
            //will not get messed up
            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
        }

        //If statement checks that the result that is retrieved is from the CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //Over here it gets the photo that is cropped
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //If the resulting data is ok
            if (resultCode == RESULT_OK) {

                //Than we get the URI for that data
                Uri resultUri = result.getUri();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
