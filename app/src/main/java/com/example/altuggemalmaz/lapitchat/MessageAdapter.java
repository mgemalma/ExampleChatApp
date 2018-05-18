package com.example.altuggemalmaz.lapitchat;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    //This is going to hold the messages
    //This one is going to hold the messages from the model message class
    private List<Messages> mMessageList;

    //The current user authentication instance
    private FirebaseAuth mAuth;

    //This is where the message list can be passed
    public MessageAdapter (List<Messages> mMessageList)
    {
            this.mMessageList = mMessageList;
    }


    //The view holder and the updater class
    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
            public TextView messageText;
            public CircleImageView profileImage;

            //Initializes the links to the UI links
            public MessageViewHolder(View view)
            {
                super(view);
                messageText =  (TextView) view.findViewById(R.id.message_text_layout);
                profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
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
    public void onBindViewHolder(MessageViewHolder viewHolder, int i)
    {
        //Get the current user instance
        mAuth = FirebaseAuth.getInstance();

        //Also get the current user id
        String current_user_id = mAuth.getCurrentUser().getUid();

        //Get the selected message from the list
        Messages c = mMessageList.get(i);

        //Get the info of who send the message
        String from_user = c.getFrom();

        //If the message is sent from another user
        if (from_user.equals(current_user_id))
        {
            viewHolder.messageText.setBackgroundColor(Color.WHITE);
            viewHolder.messageText.setTextColor(Color.BLACK);
        }
        else
            {
                viewHolder.messageText.setBackgroundResource(R.drawable.message_text_backgroud);
                viewHolder.messageText.setTextColor(Color.WHITE);
        }

        //Set the message to the view for the single text
        viewHolder.messageText.setText(c.getMessage());
    }


    //Return the message list size
    public int getItemCount()
    {
        return mMessageList.size();
    }

}
