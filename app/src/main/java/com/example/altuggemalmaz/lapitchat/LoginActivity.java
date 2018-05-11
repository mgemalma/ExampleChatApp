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

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mLoginButton;

    //FireBase Authentication Linker
    private FirebaseAuth mAuth;

    //Progress Dialog that will apper when something loads
    private ProgressDialog mLoginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize the progress dialog
        mLoginProgress = new ProgressDialog(this);

        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //This will initialize the authentication State

        mAuth = FirebaseAuth.getInstance();

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login_button);

        //When the login button is clicked
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            //When there is a click this code will run
            @Override
            public void onClick(View v) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    //Print the text to say that there is a error
                    Toast.makeText(LoginActivity.this, "Please fill all the required fields",Toast.LENGTH_LONG).show();
                }
                else {


                    //Loging the User will be the title with this now
                    mLoginProgress.setTitle("Logging in User");

                    //This will be the info that will be told the user whats going on
                    mLoginProgress.setMessage("Please wait while we check your credentials !");

                    //When user clicks on to the progress dialog we don't want him to cancel the progress
                    mLoginProgress.setCanceledOnTouchOutside(false);

                    //This will show the progress dialog
                    mLoginProgress.show();

                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        //To sign in the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    //When the task is complete
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //Get rid of the progress dialog
                            mLoginProgress.dismiss();

                            //Switch to the new UI
                            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);

                            //This line of code makes sure that the user can't go back to the registration page using the phone back button
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            //Actually switches the UI
                            startActivity(mainIntent);

                            finish();

                            //Get the current user state
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {

                            //Get rid of the progress dialog
                            mLoginProgress.hide();

                            //Print the text to say that there is a error
                            Toast.makeText(LoginActivity.this, "We couldn't find your account!",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}
