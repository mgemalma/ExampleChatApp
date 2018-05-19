package com.example.altuggemalmaz.lapitchat;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class AltugChat extends Application {

    //For online feature
    //Database links
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;


    @Override
    public void onCreate() {
        super.onCreate();

        //Turn on the fireBase offline features (Does the persistance for the app)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //For picasso persistence this is the code

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

//        mAuth = FirebaseAuth.getInstance();
//
//        if (mAuth.getCurrentUser() != null)
//        {
//
//
//            mUserDatabase = FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid());
//
//            mUserDatabase.child("online").onDisconnect().setValue(false);
//        }

    }
}
