package com.example.altuggemalmaz.lapitchat;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class LapitChat extends Application {

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
    }
}
