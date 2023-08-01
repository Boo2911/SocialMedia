package com.example.socialmedia.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmedia.Adapters.ViewPagerNotfAdapter;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

FragmentNotificationsBinding binding;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
     View view = inflater.inflate(R.layout.fragment_notifications, container, false);
     binding = FragmentNotificationsBinding.bind(view);

     binding.viewPager.setAdapter(new ViewPagerNotfAdapter(requireActivity().getSupportFragmentManager()));
     binding.tabLayout.setupWithViewPager(binding.viewPager);
        return view;
    }
}