package com.example.altuggemalmaz.lapitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLogBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mLogBtn = (Button) findViewById(R.id.start_login_btn);

        //When the login button is clicked on UI
        mLogBtn.setOnClickListener(new View.OnClickListener() {

            //Switch to the login UI
            @Override
            public void onClick(View v) {

                //Switch to the start Activity to the Login Activity
                Intent log_intent = new Intent(StartActivity.this, LoginActivity.class);

                startActivity(log_intent);
            }
        });


        //When the button is clicked on the UI
        mRegBtn.setOnClickListener(new View.OnClickListener() {

            //When there is a click on the UI
            @Override
            public void onClick(View v) {

                //To switch from this view (Start Activity) to the RegisterActvity
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);

                //Actually switch to the new UI by calling this function
                startActivity(reg_intent);

            }
        });
    }
}
