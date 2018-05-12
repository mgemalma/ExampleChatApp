package com.example.altuggemalmaz.lapitchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    //The Status input that is received from the user
    private TextInputLayout mStatus;
    private Button mSavebtn;

    //FireBase Connections to connect with the database and Update the values
    private DatabaseReference mStatusDatabase;

    //The know which user is currently logged in
    private FirebaseUser mCurrentUser;

    //To show the user the progress and something is happening
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //The user id is needed so that the user info can be grabbed easily on the database tree
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String curUID = mCurrentUser.getUid();

        //Initializing the Database connection and reaching where the user info is located
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(curUID);

        //Over here we receive the status which was send by the settings activity through the Intent
        String status_value = getIntent().getStringExtra("status_value");


        //Initialize the UI links to control them
        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);

        //Since we received the string value now we are going to display it on the TextInputLayout
        mStatus.getEditText().setText(status_value);

        //Onclick listener to know when the button is clicked
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initialization of the progress dialog
                //"This" is given to make sure that the dialog will appear on this activity
                mProgress =  new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save your changes!");
                mProgress.show();


                //Get the status from the UI
                String status = mStatus.getEditText().getText().toString();

                //To update the value on the database
                //The on complete listener makes sure that the dismiss function is only executed
                //When the updating the database is done
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            mProgress.dismiss();
                        } else
                            {
                                mProgress.hide();
                                Toast.makeText(getApplicationContext(), "There was an error in saving changes!", Toast.LENGTH_LONG).show();
                            }
                    }
                });
            }
        });

    }
}
