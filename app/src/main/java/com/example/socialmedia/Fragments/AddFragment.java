package com.example.socialmedia.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;


public class AddFragment extends Fragment {

FragmentAddBinding binding;

FirebaseAuth auth ;
FirebaseDatabase database;

FirebaseStorage storage;

ProgressDialog dialog;

Uri uri;
    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddBinding.inflate(inflater, container, false);

        dialog.setProgress(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading Post..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Users user = snapshot.getValue(Users.class);
                    Picasso.get().load(user.getProfile_img())
                                    .placeholder(R.drawable.profileavatar)
                                            .into(binding.postProfile);
                    binding.postUsername.setText(user.getName());
                    binding.postProfession.setText(user.getProfession());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String caption = binding.postCaption.getText().toString();
                if(!caption.equals("")){
                    binding.btnPost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.after_follow_btn));
                    binding.btnPost.setTextColor(ContextCompat.getColor(getContext(), R.color.bg_frag));
                    binding.btnPost.setEnabled(true);
                }else {

                    binding.btnPost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_follow_btn));
                    binding.btnPost.setTextColor(ContextCompat.getColor(getContext(), R.color.grad_start));
                    binding.btnPost.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                final StorageReference reference = storage.getReference().child("posts")
                        .child(auth.getUid()).child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Posts post = new Posts();
                                post.setPostDescription(binding.postCaption.getText().toString());
                                post.setPostedAt(new Date().getTime());
                                post.setPostedBy(auth.getUid());
                                post.setPostImage(uri.toString());

                                database.getReference().child("posts")
                                        .push()
                                        .setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                transaction.replace(R.id.linearLayout, new HomeFragment());
                                                transaction.commit();
                                                Toast.makeText(getContext(), "Posted Successfully..", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                    }
                });
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null){
            uri = data.getData();
            binding.post.setImageURI(uri);
            binding.post.setVisibility(View.VISIBLE);


            binding.btnPost.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.after_follow_btn));
            binding.btnPost.setTextColor(ContextCompat.getColor(getContext(), R.color.bg_frag));
            binding.btnPost.setEnabled(true);
        }
    }
}