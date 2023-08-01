package com.example.socialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.socialmedia.Adapters.CommentsAdapter;
import com.example.socialmedia.Models.CommentsModel;
import com.example.socialmedia.Models.Notification;
import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
    String postId;
    String postedBy;

    FirebaseDatabase database;
    FirebaseAuth auth;

    ArrayList<CommentsModel> comments = new ArrayList<>();

    int commentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarComments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postId = getIntent().getStringExtra("postId");
        postedBy = getIntent().getStringExtra("postedBy");

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        database.getReference().child("posts")
                .child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Posts posts = snapshot.getValue(Posts.class);
                        Picasso.get().load(posts.getPostImage()).placeholder(R.drawable.profileavatar)
                                .into(binding.postComment);
                        binding.likes.setText(posts.getLikeCount()+"");
                        binding.captionPost.setText(posts.getPostDescription());
                        binding.comments.setText(posts.getCommentsCount()+"");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        database.getReference().child("users")
                .child(postedBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        binding.nameComment.setText(users.getName());
                        Picasso.get().load(users.getProfile_img()).placeholder(R.drawable.profileavatar)
                                .into(binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentsModel comment = new CommentsModel();
                comment.setCommentBody(binding.addCommentet.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(auth.getUid());

                database.getReference().child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                database.getReference().child("posts")
                                        .child(postId)
                                        .child("commentsCount")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if(snapshot.exists()){
                                                    commentCount = snapshot.getValue(Integer.class);
                                                }
                                                    database.getReference().child("posts")
                                                            .child(postId)
                                                            .child("commentsCount")
                                                            .setValue(commentCount+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    binding.addCommentet.setText("");

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(auth.getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostId(postId);
                                                                    notification.setType("comment");
                                                                    notification.setPostedBy(postedBy);

                                                                    FirebaseDatabase.getInstance().getReference().child("notification")
                                                                            .child(postedBy)
                                                                            .push()
                                                                            .setValue(notification);
                                                                    Toast.makeText(CommentActivity.this, "Commented.."+notification.getNotificationBy(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        });
            }
        });

        CommentsAdapter adapter = new CommentsAdapter(comments, this);
        binding.rvComment.setAdapter(adapter);
        binding.rvComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        database.getReference().child("posts")
                .child(postId)
                .child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        comments.clear();
                        for(DataSnapshot dss: snapshot.getChildren()){
                            CommentsModel commentsModel = dss.getValue(CommentsModel.class);
                            comments.add(commentsModel);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);

    }
}