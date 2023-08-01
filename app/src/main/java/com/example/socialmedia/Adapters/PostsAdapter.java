package com.example.socialmedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.CommentActivity;
import com.example.socialmedia.Models.Notification;
import com.example.socialmedia.Models.Posts;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.PostsSampleViewBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.viewHolder>{

    ArrayList<Posts> lists;
    Context context;


    public PostsAdapter(ArrayList<Posts> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.posts_sample_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Posts models = lists.get(position);
        Picasso.get().load(models.getPostImage())
                .placeholder(R.drawable.profileavatar)
                .into(holder.binding.userPost);
        holder.binding.likes.setText(models.getLikeCount()+"");
        holder.binding.comments.setText(models.getCommentsCount()+"");


        String description = models.getPostDescription();
        if(description.equals("")){
            holder.binding.postCaption.setVisibility(View.GONE);
        }else {
            holder.binding.postCaption.setText(description);
            holder.binding.postCaption.setVisibility(View.VISIBLE);
        }

        holder.binding.postCaption.setText(models.getPostDescription());
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(models.getPostedBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        holder.binding.postsUsername.setText(users.getName());
                        holder.binding.userProfession.setText(users.getProfession());
                        Picasso.get().load(users.getProfile_img()).placeholder(R.drawable.profileavatar)
                                .into(holder.binding.profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("posts")
                .child(models.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            holder.binding.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);
                        }else{
                            holder.binding.likes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                            .child(models.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                                            .child(models.getPostId())
                                                            .child("likeCount")
                                                            .setValue(models.getLikeCount()+1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked, 0, 0, 0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setType("like");
                                                                    notification.setPostId(models.getPostId());
                                                                    notification.setPostedBy(models.getPostedBy());
                                                                    notification.setNotificationAt(new Date().getTime());

                                                                    FirebaseDatabase.getInstance().getReference().child("notification")
                                                                            .child(models.getPostedBy())
                                                                            .push()
                                                                            .setValue(notification);

                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





        holder.binding.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", models.getPostId());
                intent.putExtra("postedBy", models.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }





    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        PostsSampleViewBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = PostsSampleViewBinding.bind(itemView);

        }
    }
}
