package com.example.socialmedia.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmedia.Adapters.NotificationAdapter;
import com.example.socialmedia.Models.Notification;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.FragmentNotificationABinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationAFragment extends Fragment {

  FragmentNotificationABinding binding;

  FirebaseDatabase database;
  FirebaseAuth auth;

  ArrayList<Notification> lists = new ArrayList<>();
    public NotificationAFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_notification_a, container, false);
      binding = FragmentNotificationABinding.bind(view);

      NotificationAdapter notiAdapter = new NotificationAdapter(lists, getContext());

      binding.notificationsRv.setAdapter(notiAdapter);
      binding.notificationsRv.setLayoutManager(new LinearLayoutManager(getContext()));

      database.getReference().child("notification")
              .child(auth.getUid())
                      .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                          lists.clear();
                          for(DataSnapshot dss1: snapshot.getChildren()){
                            Notification notification = dss1.getValue(Notification.class);
                            notification.setNotificationId(dss1.getKey());
                            lists.add(notification);
                          }
                          notiAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                      });



        return view;
    }
}