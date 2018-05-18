package com.example.altuggemalmaz.lapitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    //The user with which we are going to send messages to
    private String mChatUser;

    //Toolbar Global Variable
    private Toolbar mChatToolbar;

    //Authentication Instance
    private FirebaseUser mAuth;

    //Database Reference
    private DatabaseReference mUserDatabase;

    //Initializing The UI connections
    private TextView displayName;
    private TextView lastSeen;
    private ImageView imageUser;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    //The current user who is chatting with the chat user
    private String mCurrentUser;

    //The root reference to the DataBase
    private DatabaseReference mRootRef;

    //The link to the recyclerView for messages
    private RecyclerView mMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Initialize the toolbar
        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        //mChatToolbar.setTitle("Chat Page");

        //Get the user_id of the
        mChatUser = getIntent().getStringExtra("user_id");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);

        //Initialize the UI connections
        displayName = (TextView) findViewById(R.id.chat_display_name);
        lastSeen = (TextView) findViewById(R.id.chat_last_seen);
        imageUser = (ImageView) findViewById(R.id.chat_bar_image);
        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);
        mMessagesList = (RecyclerView) findViewById(R.id.chat_messages_list);


        //Get the current user id who is using this app
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUser = mAuth.getUid();

        //Get the name of the user
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                displayName.setText(name);
                String image = dataSnapshot.child("image").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();

                if (!image.equals("default"))
                {
                    Picasso.with(ChatActivity.this).load(image).into(imageUser);
                }

                //If the lastSeen of the user exists then update the last seen bar accordingly
                if (dataSnapshot.hasChild("lastSeen")){
                    if ( online.equals("true"))
                    {
                       lastSeen.setText("Online");
                    }
                 else if (online.equals("false"))
                    {

                        //Get the last seen stamp from the database and convert it to a proper string
                        String lastSeenDB = dataSnapshot.child("lastSeen").getValue().toString();
                        String lastseenVal= GetTimeAgo.getTimeAgo(Long.parseLong(lastSeenDB),  ChatActivity.this);
                        lastSeen.setText(lastseenVal);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       //Connection to the Chat area of the database
        mRootRef.child("Chat").child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //If the current user hasn't chat with the chatUser before create a instance of the chat on the DataBase
                if (!dataSnapshot.hasChild(mChatUser))
                {

                    //The proper info for the chat
                    //This values will be in both users DB
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    //Store the chat parameters in both users dataBase
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUser + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUser, chatAddMap);

                    //Now put the values to the DataBase
                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            //If the database has an error
                            if (databaseError != null)
                            {
                                //State the error on the log
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //When the send button is clicked then the message that is in the message view has to be sent
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We don't do anything here because when the user even hits enter we want to send the message
                //That is why we leave it to the function to take care
                sendMessage();
            }
        });

    }

    private void sendMessage() {

        //Get the text in the EditText
        String message = mChatMessageView.getText().toString();

        //Make sure that the text that is entered is not empty
        if (!TextUtils.isEmpty(message))
        {
            //Since we know that the text is not empty we will create the text Object
            //In which we will put the message contents in it

            //The database references for both of the users side so that the messages can be stored properly
            String current_user_ref = "messages/" + mCurrentUser + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUser;

            //To get the database reference and also create a unique message id
            //The push will give us the notification unique id for the database
            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUser).child(mChatUser).push();

            //We will also store the push_id in a string
            //Get Key will give us the pushID
            String push_id = user_message_push.getKey();

            //Now create the message object details that is going to be stored on the dataBase
            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);

            Map messageUserMap = new HashMap();

            //Now push the message content according to the unqiue message id
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            //Finally send the message contents into the DataBase
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    //If the database has an error
                    if (databaseError != null)
                    {
                        //State the error on the log
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
