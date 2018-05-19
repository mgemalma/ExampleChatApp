package com.example.altuggemalmaz.lapitchat;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    //This is going to hold the messages
    //This one is going to hold the messages from the model message class
    private List<Messages> mMessageList;

    //The current user authentication instance
    private FirebaseAuth mAuth;

    //DataBase Reference
    private DatabaseReference rootRef;

    //This is where the message list can be passed
    public MessageAdapter (List<Messages> mMessageList)
    {
            this.mMessageList = mMessageList;
    }


    //The view holder and the updater class
    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
            public TextView messageText;
            public TextView displayName;
            public TextView messageTime;
            public CircleImageView profileImage;
            public ImageView messageImage;

            //Initializes the links to the UI links
            public MessageViewHolder(View view)
            {
                super(view);
                messageText =  (TextView) view.findViewById(R.id.message_text);
                profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
                displayName = (TextView) view.findViewById(R.id.message_display_name);
                messageTime = (TextView) view.findViewById(R.id.message_time);
                messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

            }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);
    }

    //This will get the message from the model class and pass it onto the UI
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {
        //Get the current user instance
        mAuth = FirebaseAuth.getInstance();

        //Also get the current user id
        String current_user_id = mAuth.getCurrentUser().getUid();

        //Get the selected message from the list
        final Messages c = mMessageList.get(i);

        //Get the info of who send the message
        String from_user = c.getFrom();

        //Get the type of the message if its image or not
        String message_type = c.getType();

        //Connect to the Database
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        //Persistence
        rootRef.keepSynced(true);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                viewHolder.displayName.setText(name);

                String image = dataSnapshot.child("image").getValue().toString();

                //If there is a image that is stored on the Database
                if (!image.equals("default"))
                {
                    //Location where the file is stored
                    //If you don't want the picture to disappear when it's grabbed from the database
                    //You can do after load placeholder(R.something) this shows a temporary image to the user while the original picture
                    //Is received from the database
                    Picasso.with(viewHolder.itemView.getContext()).load(image).into(viewHolder.profileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text"))
        {
            //Set the message to the view for the single text
            viewHolder.messageText.setText(c.getMessage());

            //Make the image part disappear
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        }
        else
            {
                //If image make the text part invisible
                viewHolder.messageText.setVisibility(View.INVISIBLE);
                //Location where the file is stored
                //If you don't want the picture to disappear when it's grabbed from the database
                //You can do after load placeholder(R.something) this shows a temporary image to the user while the original picture
                //Is received from the database
                //Network Policy offline code will do the persistence
                //Currently this code is trying to retrieve the image offline
                Picasso.with(viewHolder.itemView.getContext()).load(c.getMessage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.messageImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Do nothing all good
                    }

                    @Override
                    public void onError() {
                        //This means the image is not stored offline so we need to grab it from the database
                        Picasso.with(viewHolder.itemView.getContext()).load(c.getMessage()).into(viewHolder.messageImage);
                    }
                });
            }

        //Update the time of the text
        long time = c.getTime();
        String timeString = GetTimeAgo.getTimeAgo(time);
        viewHolder.messageTime.setText(timeString);


    }


    //Return the message list size
    public int getItemCount()
    {
        return mMessageList.size();
    }

}
