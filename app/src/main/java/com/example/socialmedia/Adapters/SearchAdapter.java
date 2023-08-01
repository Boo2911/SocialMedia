package com.example.socialmedia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.Models.FriendsModel;
import com.example.socialmedia.Models.Notification;
import com.example.socialmedia.Models.Users;
import com.example.socialmedia.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHolder>{

    Context context;
    ArrayList<Users> lists;

    public SearchAdapter(Context context, ArrayList<Users> lists) {
        this.context = context;
        this.lists = lists;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_sample, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Users models = lists.get(position);
        holder.profession.setText(models.getProfession());
        Picasso.get().load(models.getProfile_img()).placeholder(R.drawable.profileavatar)
                .into(holder.profile_img);

        holder.username.setText(models.getName());


        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(models.getUserId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    holder.btn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.after_follow_btn));
                                    holder.btn.setText("following");
                                    holder.btn.setTextColor(context.getResources().getColor(R.color.bg_frag));
                                    holder.btn.setEnabled(false);
                                }
                                else {
                                    holder.btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            FriendsModel friendsModel = new FriendsModel();
                                            friendsModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                            friendsModel.setFollowedAt(new Date().getTime());

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("users")
                                                    .child(models.getUserId())
                                                    .child("followers")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .setValue(friendsModel)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("users")
                                                                    .child(models.getUserId())
                                                                    .child("followerCount")
                                                                    .setValue(models.getFollowerCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            holder.btn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.after_follow_btn));
                                                                            holder.btn.setText("following");
                                                                            holder.btn.setTextColor(context.getResources().getColor(R.color.bg_frag));
                                                                            holder.btn.setEnabled(false);


                                                                            Notification notification = new Notification();
                                                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                            notification.setNotificationAt(new Date().getTime());
                                                                            notification.setType("follow");
                                                                            notification.setCheckOpen(true);

                                                                            FirebaseDatabase.getInstance().getReference().child("notification")
                                                                                    .child(models.getUserId())
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
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView profile_img;
        TextView username, profession;
        AppCompatButton btn;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            profile_img = itemView.findViewById(R.id.follow_profile);
            username = itemView.findViewById(R.id.follow_username);
            profession = itemView.findViewById(R.id.follow_profession);
            btn = itemView.findViewById(R.id.btn_follow);
        }
    }
}
