package com.castup.castup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsViewFragments extends FragmentPagerAdapter {


    public TabsViewFragments(@NonNull FragmentManager fm) {
        super(fm);
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){

            case 0 :

                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 1 :

                ContantsFragment ContantsFragment = new ContantsFragment();
                return ContantsFragment;

            case 2 :

                GroupsFragment  RoomsFragment = new GroupsFragment();
                return RoomsFragment;


             default: return null ;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {


        switch (position){

            case 0 :
                return "Chat";

            case 1 :
                return "Contants";

            case 2 :
                return "Rooms";

            default: return null ;
        }
    }
}
