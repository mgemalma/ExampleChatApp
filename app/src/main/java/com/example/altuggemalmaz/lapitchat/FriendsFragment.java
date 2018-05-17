 package com.example.altuggemalmaz.lapitchat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


 /**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    //Link to the UI of list
    private RecyclerView mFriendsList;

    //Database Links
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    //The current user
     private String mCurrent_user_id;

     //The view that we are on will be recorded here
     private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_friends, container, false);

        //Set the connection to the list view through the mFriendsList
        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);

        //Initialize the current user link
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getUid();

        //Reach to the friends part in the database
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Inflate the lavout for this fragment
        return mMainView;
    }

     @Override
     public void onStart() {
         super.onStart();

         //From the database this links the data on DB to the Users class (Build) functionality does it
         FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(mFriendsDatabase, Friends.class).build();


         //Use the firebase UI functionality to link the database the list and the single user layout UI to construct a list
         FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {

                //This function takes the data from the model class and passes it onto the UI
                @Override
                protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                    //From the model class we created we get the date and pass it to the UI
                    holder.setDate(model.getDate());

                    //From the fireBase UI since we are accesing the data through the userid we can get it through
                    final String list_user_id = getRef(position).getKey();

                    System.out.print(list_user_id);

                    mUsersDatabase.child(list_user_id).
                            addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String userName = dataSnapshot.child("name").getValue().toString();
                                    String userThumb = dataSnapshot.child("image").getValue().toString();
                                    if (dataSnapshot.hasChild("online"))
                                    {
                                        String userOnline = dataSnapshot.child("online").getValue().toString();
                                        holder.setUserOnline(userOnline);
                                    }
                                    holder.setName(userName);
                                    holder.setImage(userThumb, getContext());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                    //Set the onclick listener for the friends tab so that when the user clicks one of the user tabs
                    //the more detailed view can appear
                    holder.mView.setOnClickListener(new View.OnClickListener() {

                        //For this one will show an alert dialog
                        @Override
                        public void onClick(View v) {

                            CharSequence options[] = new CharSequence[]{"Open Profile" , "Send Message"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle("Select Options");
                            builder.setItems(options, new DialogInterface.OnClickListener() {

                                //This will tell us which option on alert dialog is selected
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //If Open Profile Option is selected then
                                    if (which == 0)
                                    {
                                        //Send the view to the profile of that user
                                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                        profileIntent.putExtra("user_id", list_user_id);
                                        startActivity(profileIntent);
                                    }


                                    //If send a message feature is clicked
                                    if (which == 1)
                                    {

                                        //Then switch the view to the ChatActivity
                                        Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                        chatIntent.putExtra("user_id", list_user_id);
                                        startActivity(chatIntent);
                                    }
                                }
                            });

                            builder.show();
                        }
                    });

                }

                @NonNull
                @Override
                public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    //Create the view link
                    // Create a new instance of the ViewHolder, in this case we are using a custom
                    // layout called R.layout.message for each item
                    //Link the single user layout UI to the list view and pass it to the class we created so that the
                    //UI fields can be updated according to the user
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                    return new FriendsFragment.FriendsViewHolder(view);
                }
            };

         mFriendsList.setAdapter(friendsRecyclerViewAdapter);
         friendsRecyclerViewAdapter.startListening();
     }

     public static class FriendsViewHolder extends RecyclerView.ViewHolder
     {
         View mView;


         //Get the view that is going to be modified from here
         public FriendsViewHolder(View itemView) {
             super(itemView);
             mView = itemView;
         }

         public void setDate(String date)
         {
             TextView userNameView = (TextView) mView.findViewById(R.id.user_single_status);
             userNameView.setText(date);

         }

         public void setName(String name)
         {
             TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
             userNameView.setText(name);
         }

         public void setImage(String image, Context ctx)
         {
             //If there is no image set for that user we don't want it to be updated
             if (image.equals("default")){
                 return;
             }

             //If there is a image in that case retrieve the image
             CircleImageView mDisplayImage = (CircleImageView) mView.findViewById(R.id.user_single_image);
             Picasso.with(ctx).load(image).into(mDisplayImage);
         }

         //Icon will appear according to the availability of the user
         public  void setUserOnline(String online)
         {
             ImageView user_online_view = (ImageView) mView.findViewById(R.id.user_single_online_icon);
             if (online.equals("true"))
             {
                 user_online_view.setVisibility(View.VISIBLE);
             }else
                 {
                     user_online_view.setVisibility(View.INVISIBLE);
                 }
         }
     }
 }
