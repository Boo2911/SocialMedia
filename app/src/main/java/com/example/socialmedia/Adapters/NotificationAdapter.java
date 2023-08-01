package com.example.socialmedia.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.CommentActivity;
import com.example.socialmedia.Models.Notification;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.example.socialmedia.databinding.SampleNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<Notification> lists;
    Context context;

    String notificationBy;

    public NotificationAdapter(ArrayList<Notification> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_notifications, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Notification notification = lists.get(position);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(notification.getNotificationBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfile_img()).placeholder(R.drawable.profileavatar)
                                                    .into(holder.binding.notificationProfile);
                        if(notification.getType().equals("like"))
                            holder.binding.notificationMsg.setText(Html.fromHtml("<b>"+users.getName()+"</b> "+ "liked your post."));
                        else if(notification.getType().equals("comment")){
                            holder.binding.notificationMsg.setText(Html.fromHtml("<b>"+users.getName()+"</b> "+ "commented on your post."));
                        }else  holder.binding.notificationMsg.setText(Html.fromHtml("<b>"+users.getName()+"</b> "+ "started following you."));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!notification.getType().equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("notification")
                            .child(notification.getPostedBy())
                            .child(notification.getNotificationId())
                            .child("checkOpen")
                            .setValue(true);

                    holder.binding.openNotification.setBackgroundColor(Color.parseColor("#E7E7E7"));
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", notification.getPostId());
                    intent.putExtra("postedBy", notification.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

            }
        });

        Boolean checkOpen = notification.isCheckOpen();
        if(checkOpen==true){
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else {
            holder.binding.openNotification.setBackgroundColor(Color.parseColor("#E7E7E7"));

        }

    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        SampleNotificationsBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = SampleNotificationsBinding.bind(itemView);

        }
    }
}
