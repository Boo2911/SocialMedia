package com.example.socialmedia.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia.Adapters.FriendsAdapter;
import com.example.socialmedia.Models.FriendsModel;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ArrayList<FriendsModel> friendsList= new ArrayList<>();

    FirebaseAuth auth;
    FirebaseStorage storage;

    FirebaseDatabase database;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        binding = FragmentProfileBinding.bind(view);

        FriendsAdapter adapter = new FriendsAdapter(friendsList, getContext());
        binding.friendsRv.setAdapter(adapter);
        binding.friendsRv
                .setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        database.getReference().child("users")
                        .child(auth.getUid())
                                .child("followers")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                friendsList.clear();
                                                for(DataSnapshot ss1:snapshot.getChildren()){
                                                    FriendsModel friends = ss1.getValue(FriendsModel.class);
                                                    friendsList.add(friends);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

        database.getReference().child("users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Users users = snapshot.getValue(Users.class);
                            Picasso.get()
                                    .load(users.getCover_img()).placeholder(R.drawable.profileavatar)
                                    .into(binding.bigImage);

                            Picasso.get()
                                    .load(users.getProfile_img()).placeholder(R.drawable.profileavatar)
                                    .into(binding.profileImage);

                            binding.userProfession.setText(users.getProfession());
                            binding.username.setText(users.getName());
                            binding.followers.setText(users.getFollowerCount()+"");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        binding.addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        binding.verifiedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 22);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==11){
            if(data.getData()!=null){
                Uri uri = data.getData();
                binding.bigImage.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("cover_img")
                        .child(auth.getUid());

                reference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Cover Photo Updated", Toast.LENGTH_SHORT).show();
                            }
                        });

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        database.getReference().child("users").child(auth.getUid()).child("cover_img")
                                .setValue(uri.toString());
                    }
                });
            }
        }else {
            if(data.getData()!=null){
                Uri uri = data.getData();
                binding.profileImage.setImageURI(uri);

                final StorageReference reference = storage.getReference().child("profile_img")
                        .child(auth.getUid());

                reference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getContext(), "Profile Photo Updated", Toast.LENGTH_SHORT).show();
                            }
                        });

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        database.getReference().child("users").child(auth.getUid()).child("profile_img")
                                .setValue(uri.toString());
                    }
                });
            }
        }

    }
}