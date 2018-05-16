package com.example.altuggemalmaz.lapitchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    //This will be our link to the database
    private DatabaseReference mUserDatabase;

    //This will be the current FireBase user
    private FirebaseUser mCurrentUser;

    //The storage reference so that the profile images can be stored on the FireBase
    private StorageReference mImageStorage;

    //In order to fetch the realtime data on the database we need the UI connections as usual
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    //The Buttons (Link with the UI)
    private Button statusBtn;
    private Button imageBtn;

    //Database connection for online feature
    private DatabaseReference mOnlineRef;

    //Online Feature these two functions are guaranteed to be called
    @Override
    protected void onResume() {
        super.onResume();
        //Set the status to be online
        mOnlineRef.setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Set the status to be online
        mOnlineRef.setValue(false);
    }

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

        //Get the storage reference so that the profile images can be saved to the FireBase
        mImageStorage = FirebaseStorage.getInstance().getReference();

        //The current user instance that has been logged in
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //On the realtime database in order to reach where the data is stored for the specific user, we need the user ID
        final String current_uid = mCurrentUser.getUid();

        //To get to the data of the current user on the database tree we move to the location
        //where the data is stored on real time database
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Through this every value we get from this reference will be persisted on the phone through the firebase offline persistence
        mUserDatabase.keepSynced(true);

        //Database Link
        mOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("online");

        //This will fetch the data on the database it fetches the data that is stored
        //It gets us something similar like a hashmap
        mUserDatabase.addValueEventListener(new ValueEventListener() {

            //This function runs when there is a change on the database, it's a live action listener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //It gets the child area of the data snapshot, it gets its value and turns it into string
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                //If there is a image that is stored on the Database
                if (!image.equals("default"))
                {
                    //Location where the file is stored
                    //If you don't want the picture to disappear when it's grabbed from the database
                    //You can do after load placeholder(R.something) this shows a temporary image to the user while the original picture
                    //Is received from the database
                    //Network Policy offline code will do the persistence
                    //Currently this code is trying to retrieve the image offline
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {
                             //Do nothing all good
                        }

                        @Override
                        public void onError() {
                            //This means the image is not stored offline so we need to grab it from the database
                            Picasso.with(SettingsActivity.this).load(image).into(mDisplayImage);
                        }
                    });
                }

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

                //According to the id of the user save the user image inside the profile_images directory
                String uid = mCurrentUser.getUid();

                //Access the location where you are going to save the profile picture
                StorageReference storage = mImageStorage.child("profile_images").child(uid);

                //Put the file onto the directory and do some tasks when the task is done
                storage.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            //We will need the download URL to get it later on so we need to store this
                            String download_url = task.getResult().getDownloadUrl().toString();

                            //Save the image location on the database
                            mUserDatabase.child("image").setValue(download_url);

                            //To tell the user that this is done
                            Toast.makeText(SettingsActivity.this, "The image is updated", Toast.LENGTH_LONG).show();
                        }
                        else
                            {
                                Toast.makeText(SettingsActivity.this, "There was an error!", Toast.LENGTH_LONG).show();
                            }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
