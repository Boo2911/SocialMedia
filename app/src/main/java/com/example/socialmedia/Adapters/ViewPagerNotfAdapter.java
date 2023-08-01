package com.example.socialmedia.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.socialmedia.Fragments.NotificationAFragment;
import com.example.socialmedia.Fragments.RequestFragment;

public class ViewPagerNotfAdapter extends FragmentPagerAdapter {
    public ViewPagerNotfAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new NotificationAFragment();
        }else if(position==1){
            return new RequestFragment();

        }
        return new NotificationAFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0){

            return "NOTIFICATIONS";

        }else return "REQUESTS";
    }
}
