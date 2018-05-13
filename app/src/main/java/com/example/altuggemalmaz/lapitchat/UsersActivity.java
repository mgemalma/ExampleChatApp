package com.example.altuggemalmaz.lapitchat;

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

        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    //Since we want to retrieve the data realtime we have to do this on OnStart Method

    /*@Override
    protected void onStart() {
        super.onStart();


        //We need to pass the class that we have created for the users View
        FirebaseRecyclerAdapter<Users , UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,
                R.layout.users_single_layout, UsersViewHolder.class, mUsersDatabase) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
    }*/

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

            @Override
            protected void onBindViewHolder(UserViewHolder holder, int position, Users model) {
                // Bind the Chat object to the ChatHolder
                holder.setName(model.name);
                holder.setStatus(model.status);
//                holder.setImage(model.image);
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

//        public void setImage(String image)
//        {
//            CircleImageView mDisplayImage = (CircleImageView) findViewById(R.id.user_single_image);
//            Picasso.with().load(image).into(mDisplayImage);
//        }
    }


   /* public class UsersViewHolder extends RecyclerView.ViewHolder
    {

        public UsersViewHolder(View itemView) {
            super(itemView);
        }


    }*/
}
