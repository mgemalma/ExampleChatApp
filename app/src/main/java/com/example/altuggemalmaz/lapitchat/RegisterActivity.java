package com.example.altuggemalmaz.lapitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    //FireBase Authentication Linker
    private FirebaseAuth mAuth;

    //Progress Dialog that will apper when something loads
    private ProgressDialog mRegProgress;

    //This is the link to the database
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

       //Though this we get the link to the UI elements
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_nm);
        mEmail = (TextInputLayout) findViewById(R.id.reg_display_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);

        //This will initialize the authentication State

        mAuth = FirebaseAuth.getInstance();

        //This will check to see if the view is clicked or not
        mCreateBtn.setOnClickListener(new View.OnClickListener() {

            //If the view is clicked this function will get to run
            @Override
            public void onClick(View v) {

                //Get the text from the TextInputLayouts
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();


                //To check if any fields that is entered is empty
                if (TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) )
                {
                    //Print the text to say that there is a error
                    Toast.makeText(RegisterActivity.this, "Please fill all the required fields",Toast.LENGTH_LONG).show();
                }
                else {

                    //Registering User will be the title with this now
                    mRegProgress.setTitle("Registering User");

                    //This will be the info that will be told the user whats going on
                    mRegProgress.setMessage("Please wait while we create your account !");

                    //When user clicks on to the progress dialog we don't want him to cancel the progress
                    mRegProgress.setCanceledOnTouchOutside(false);

                    //This will show the progress dialog
                    mRegProgress.show();

                    //Finally register the user
                    register_user(display_name, email, password);
                }

            }
        });

    }

    private void register_user(final String display_name, String email, String password) {

        //When the registration is complete we are going to now it through the task
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            /** From here the user info will be sent to the database **/
                            //We need to get the id of the user to store the user private info on database accordingly.
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                            //We get the current userID to store unique user info on database
                            String uid = current_user.getUid();

                            //To store the info to the database like a tree this is what you do, that's why you have child code
                            //This code only creates the content like the header, the real values are stored in the hashmap.
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            //In database the values are stored like <key, value> consequently you have to send the values like that
                            //That is why we are using a hashmap
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status", "Hi there I'm using Lapit Chat App.");
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");

                            //This is what really sends the values to the database
                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        //This will dismiss the progress dialog
                                        mRegProgress.dismiss();

                                        //If the task is succesful then switch from registration view to the Main Activity
                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);

                                        //This line of code makes sure that the user can't go back to the registration page using the phone back button in this case the app exits
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        //To make this statement execute and reflect on UI
                                        startActivity(mainIntent);
                                        //To make sure the UI doesn't come back again here with the back button
                                        finish();

                                    }
                                }
                            });


                        } else {

                            //If there is a error than this should be hidden for some reason
                            mRegProgress.hide();
                            //Print the text to say that there is a error
                            Toast.makeText(RegisterActivity.this, "There is a error",Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }
}
