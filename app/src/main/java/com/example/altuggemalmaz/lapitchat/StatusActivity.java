package com.example.altuggemalmaz.lapitchat;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class StatusActivity extends AppCompatActivity {

    //The Status input that is received from the user
    private TextInputLayout mStatus;
    private Button mSavebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);

        //Onclick listere
    }
}
