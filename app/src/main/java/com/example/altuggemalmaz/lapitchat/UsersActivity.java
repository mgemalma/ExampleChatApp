package com.example.altuggemalmaz.lapitchat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    //To help establish the link between the UI
    private RecyclerView mUsersList;

    //To connect to the Database
    private DatabaseReference  mUsersDatabase;

    //The Current User
    private FirebaseUser mCurUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //Initialize the link to the UI
        mUsersList = (RecyclerView) findViewById(R.id.users_list);

        //This puts the arrow to go back to the view that we set in the AndroidManifest.xml file specificly for this view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Since we want to get the values in the Users we want to make the Database reference point to that particular section
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    //Since we want to retrieve the data realtime we have to do this on OnStart Method

    @Override
    protected void onStart() {
        super.onStart();
        startListening();

    }
    public void startListening(){

        //From the database this links the data on DB to the Users class (Build) functionality does it
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(mUsersDatabase, Users.class).build();

        //Adapter for the recycle view
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                return new UserViewHolder(view);
            }

            //Note position variable tells us where the user is tapping the location where the User info is located
            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder
                    holder.setName(model.name);
                    holder.setStatus(model.status);
                    holder.setImage(model.image, UsersActivity.this);

                    //User ID of the user so that the user info can be retrieved from the Profile Page
                    //According to the position this will give us the key the reference String which is the userID
                    final String user_id = getRef(position).getKey();

                    //When the view is clicked in that case we want to switch the UI
                    holder.mView.setOnClickListener(new View.OnClickListener() {

                        //When there is a click on according view of users this will get to run
                        @Override
                        public void onClick(View v) {

                            Intent profileIntent = new Intent(UsersActivity.this , ProfileActivity.class);
                            profileIntent.putExtra("user_id", user_id);
                            startActivity(profileIntent);

                        }
                    });
            }

        };
        mUsersList.setAdapter(adapter);
        adapter.startListening();
    }

    // Class to show the users (Users View Holder)
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        //On the users UI this links it with the UI and updates it accordingly with the value o DB
        public void setName(String name){
            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }

        //On the users UI this links it with the UI and updates it accordingly with the value o DB
        public void setStatus(String status){
            TextView statusView = (TextView) mView.findViewById(R.id.user_single_status);
            statusView.setText(status);
        }

        public void setImage(String image, Context ctx)
        {
            //If there is no image set for that user we don't want it to be updated
            if (image.equals("default"))
                return;

            //If there is a image in that case retrieve the image
            CircleImageView mDisplayImage = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(image).into(mDisplayImage);
        }
    }

}
