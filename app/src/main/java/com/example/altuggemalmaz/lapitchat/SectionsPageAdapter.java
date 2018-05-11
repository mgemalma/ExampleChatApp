package com.example.altuggemalmaz.lapitchat;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPageAdapter extends FragmentPagerAdapter
{


    //The Fragment manager will be passed from the main activity in this case
     public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

         //According to the tab requested this switch statement will do it
         switch(position)
         {
             case 0:
                  RequestsFragment requestsFragment = new RequestsFragment();
                  return requestsFragment;
             case 1:
                 ChatsFragment chatsFragment = new ChatsFragment();
                 return chatsFragment;
             case 2:
                 FriendsFragment friendsFragment = new FriendsFragment();
                 return friendsFragment;

                 default:
                     return null;

         }
    }

    @Override
    public int getCount() {
         //Since there will be 3 tabs the count should be 3
        return 3;
    }

    //This function will return the appropriate page title that is requested
    public CharSequence getPageTitle(int position) {
        //According to the tab requested this switch statement will return the title for it
        switch(position)
        {
            case 0:

                return "REQUESTS";
            case 1:
                return "CHATS";
            case 2:
                return "FRIENDS";

            default:
                return null;

        }
    }
}
